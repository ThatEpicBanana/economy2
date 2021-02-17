package mods.banana.economy2.itemmodules.module_creators;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mods.banana.bananaapi.itemsv2.StackBuilder;
import mods.banana.economy2.itemmodules.ItemModule;
import mods.banana.economy2.itemmodules.display.ModuleDisplay;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import mods.banana.economy2.itemmodules.items.NbtModifier;
import mods.banana.economy2.itemmodules.items.accepts.ListAccepts;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CreateEnchants {
    public static void onInit() {
        try {
            Gson gson = new GsonBuilder()
//                    .registerTypeAdapter(MatchToolLootCondition.class, new MatchToolLootCondition.Serializer())
                    .setPrettyPrinting()
                    .create();

            Files.createDirectories(Paths.get("enchantments/predicates"));
            Files.createDirectories(Paths.get("enchantments/item_modules"));
            Files.createDirectories(Paths.get("enchantments/tags/items"));

            // roman numerals
            String[] roman = {"i", "ii", "iii", "iv", "v"};

            // initialize array
//            JsonArray array = new JsonArray();
            Map<Identifier, NbtMatcher> map = new HashMap<>();

            for(Enchantment ench : Registry.ENCHANTMENT) {
                for(int i = 1; i <= ench.getMaxLevel(); i++) {
                    String id = Registry.ENCHANTMENT.getId(ench).getPath() + (ench.getMaxLevel() != 1 ? "-" + roman[i-1] : "");

                    // get accepts
                    ArrayList<Identifier> uncombinable = new ArrayList<>();

                    for(Enchantment ench2 : Registry.ENCHANTMENT) {
                        if(!ench.canCombine(ench2)) {
                            String id2 = Registry.ENCHANTMENT.getId(ench2).getPath() + "-any";
                            uncombinable.add(new Identifier("enchantments", id2));
                        }
                    }

                    ListAccepts accepts = new ListAccepts(uncombinable, false);

                    // put nbt matcher
                    map.put(
                            new Identifier("ench", id),
                            new NbtModifier(
                                    new Identifier("enchantments", ench.type.toString().toLowerCase(Locale.ROOT)),
                                    new Identifier("ench", id),
                                    new Identifier("enchantments", id),
                                    null,
                                    accepts
                            )
                    );

                    // get predicate
                    Path predicate = Paths.get("enchantments/predicates/" + id + ".json");

                    if(!Files.exists(predicate)) Files.createFile(predicate);

                    JsonObject predicateJson = new JsonObject();

                    predicateJson.add(
                            "predicate",
                            ItemPredicate.Builder.create()
                                    .nbt(StringNbtReader.parse("{Enchantments:[{lvl:" + i + "s, id: \"minecraft:" + Registry.ENCHANTMENT.getId(ench).getPath() + "\"}]}"))
                                    .build()
                                    .toJson()
                    );

                    predicateJson.addProperty("condition", "minecraft:match_tool");

                    Files.write(predicate, gson.toJson(predicateJson).getBytes());
                }

                String id = Registry.ENCHANTMENT.getId(ench).getPath() + "-any";

                // get predicate
                Path predicate = Paths.get("enchantments/predicates/" + id + ".json");

                if(!Files.exists(predicate)) Files.createFile(predicate);

                JsonObject predicateJson = new JsonObject();

                predicateJson.add(
                        "predicate",
                        ItemPredicate.Builder.create()
                                .nbt(StringNbtReader.parse("{Enchantments:[{ id: \"minecraft:" + Registry.ENCHANTMENT.getId(ench).getPath() + "\"}]}"))
                                .build()
                                .toJson()
                );

                predicateJson.addProperty("condition", "minecraft:match_tool");

                Files.write(predicate, gson.toJson(predicateJson).getBytes());
            }

            // make item module
            ItemModule module = new ItemModule(
                    "Enchantments",
                    map,
                    new ModuleDisplay(
                            "Enchantments",
                            new StackBuilder()
                                    .item(Items.EXPERIENCE_BOTTLE)
                                    .name("Enchantments")
                                    .build()
                    )
            );

            // write module to file
            Path modulePath = Paths.get("enchantments/item_modules/enchantment_books.json");
            if(!Files.exists(modulePath)) Files.createFile(modulePath);
            Files.write(modulePath, ItemModule.Serializer.GSON.toJson(module).getBytes());

            // add tags
            for(EnchantmentTarget target : EnchantmentTarget.values()) {
                JsonArray array1 = new JsonArray();

                for(Item item : Registry.ITEM) {
                    if(target.isAcceptableItem(item)) array1.add(Registry.ITEM.getId(item).toString());
                }

                JsonObject tagJson = new JsonObject();
                tagJson.add("values", array1);

                Path tag = Paths.get("enchantments/tags/items/" + target.toString().toLowerCase(Locale.ROOT) + ".json");
                if(!Files.exists(tag)) Files.createFile(tag);
                Files.write(tag, gson.toJson(tagJson).getBytes());
            }

//            Files.write(Paths.get("enchants.json"), gson.toJson(map).getBytes());
        } catch (IOException | CommandSyntaxException e) {
            e.printStackTrace();
        }
    }
}

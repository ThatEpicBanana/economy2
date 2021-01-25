package mods.banana.economy2.itemmodules.module_creators;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mods.banana.economy2.itemmodules.items.JsonNbtItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateEnchantBooks {
    public static void onInit() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(MatchToolLootCondition.class, LootConditionTypes.createGsonSerializer()).create();

            // roman numerals
            String[] roman = {"i", "ii", "iii", "iv", "v"};

            // initialize array
            JsonArray array = new JsonArray();

            Item enchantment_book = Items.ENCHANTED_BOOK;

            Files.createDirectories(Path.of("enchantment_book_module/predicates/"));
            Files.createDirectories(Path.of("enchantment_book_module/item_modules/"));

            for(Enchantment ench : Registry.ENCHANTMENT) {
                for(int i = 1; i <= ench.getMaxLevel(); i++) {
                    String id = Registry.ENCHANTMENT.getId(ench).getPath() + (ench.getMaxLevel() != 1 ? "-" + roman[i-1] : "");

                    array.add(new Gson().toJsonTree(new JsonNbtItem(
                            "book:" + id,
                            "enchantment_book_module:" + id,
                            null
                    )));

                    Path predicate = Paths.get("enchantment_book_module/predicates/" + id + ".json");

                    Files.createFile(predicate);

                    Files.write(predicate,
                            gson.toJson(
                                    new MatchToolLootCondition(
                                            ItemPredicate.Builder.create()
                                                    .item(enchantment_book)
                                                    .nbt(StringNbtReader.parse("{StoredEnchantments:[{lvl:" + i + "s, id: \"minecraft:" + Registry.ENCHANTMENT.getId(ench).getPath() + "\"}]}"))
                                                    .build()
                                    )
                            ).getBytes()
                    );
                }
            }

            Files.write(Paths.get("enchantment_book_module/item_modules/enchantment_books.json"), gson.toJson(array).getBytes());
        } catch (IOException | CommandSyntaxException e) {
            e.printStackTrace();
        }
    }
}

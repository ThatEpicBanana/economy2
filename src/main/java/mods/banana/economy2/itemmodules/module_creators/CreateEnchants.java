package mods.banana.economy2.itemmodules.module_creators;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mods.banana.bananaapi.itemsv2.StackBuilder;
import mods.banana.economy2.itemmodules.ItemModule;
import mods.banana.economy2.itemmodules.display.MatcherDisplay;
import mods.banana.economy2.itemmodules.display.ModuleDisplay;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import mods.banana.economy2.itemmodules.items.NbtModifier;
import mods.banana.economy2.itemmodules.items.accepts.ListAccepts;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
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
    public static Map<Enchantment, Item> items = new HashMap<>();
    public static Map<EnchantmentTarget, Item> targetItemMap = new HashMap<>();

    static {
        items.put(Enchantments.AQUA_AFFINITY, Items.DIAMOND_PICKAXE);
        items.put(Enchantments.BANE_OF_ARTHROPODS, Items.COBWEB);
        items.put(Enchantments.BINDING_CURSE, Items.BARRIER);
        items.put(Enchantments.BLAST_PROTECTION, Items.TNT);
        items.put(Enchantments.CHANNELING, Items.END_ROD);
        items.put(Enchantments.DEPTH_STRIDER, Items.DIAMOND_BOOTS);
        items.put(Enchantments.EFFICIENCY, Items.GOLDEN_PICKAXE);
        items.put(Enchantments.FEATHER_FALLING, Items.FEATHER);
        items.put(Enchantments.FIRE_ASPECT, Items.FIRE_CHARGE);
        items.put(Enchantments.FIRE_PROTECTION, Items.MAGMA_CREAM);
        items.put(Enchantments.FLAME, Items.TIPPED_ARROW); // fire protection
        items.put(Enchantments.FORTUNE, Items.LAPIS_LAZULI);
        items.put(Enchantments.FROST_WALKER, Items.ICE);
        items.put(Enchantments.IMPALING, Items.PRISMARINE_SHARD);
        items.put(Enchantments.INFINITY, Items.ARROW);
        items.put(Enchantments.KNOCKBACK, Items.STICK);
        items.put(Enchantments.LOOTING, Items.GUNPOWDER);
        items.put(Enchantments.LOYALTY, Items.BONE);
        items.put(Enchantments.LUCK_OF_THE_SEA, Items.TROPICAL_FISH);
        items.put(Enchantments.LURE, Items.HEART_OF_THE_SEA);
        items.put(Enchantments.MENDING, Items.EXPERIENCE_BOTTLE);
        items.put(Enchantments.MULTISHOT, Items.FIREWORK_ROCKET);
        items.put(Enchantments.PIERCING, Items.FLINT);
        items.put(Enchantments.POWER, Items.TIPPED_ARROW); // sharpness
        items.put(Enchantments.PROJECTILE_PROTECTION, Items.SPECTRAL_ARROW);
        items.put(Enchantments.PROTECTION, Items.NETHERITE_CHESTPLATE);
        items.put(Enchantments.PUNCH, Items.SLIME_BALL);
        items.put(Enchantments.QUICK_CHARGE, Items.CROSSBOW); //{ChargedProjectiles: [{id: "minecraft:arrow", Count: 1b}], Charged: 1b}
        items.put(Enchantments.RESPIRATION, Items.PUFFERFISH);
        items.put(Enchantments.RIPTIDE, Items.TRIDENT);
        items.put(Enchantments.SHARPNESS, Items.BLAZE_POWDER);
        items.put(Enchantments.SILK_TOUCH, Items.GLASS);
        items.put(Enchantments.SMITE, Items.ROTTEN_FLESH);
        items.put(Enchantments.SOUL_SPEED, Items.SOUL_SOIL);
        items.put(Enchantments.SWEEPING, Items.NETHERITE_SWORD);
        items.put(Enchantments.THORNS, Items.CACTUS);
        items.put(Enchantments.UNBREAKING, Items.ANVIL);
        items.put(Enchantments.VANISHING_CURSE, Items.ARMOR_STAND);

        targetItemMap.put(EnchantmentTarget.ARMOR, Items.NETHERITE_CHESTPLATE);
        targetItemMap.put(EnchantmentTarget.ARMOR_HEAD, Items.NETHERITE_HELMET);
        targetItemMap.put(EnchantmentTarget.ARMOR_CHEST, Items.NETHERITE_CHESTPLATE);
        targetItemMap.put(EnchantmentTarget.ARMOR_LEGS, Items.NETHERITE_LEGGINGS);
        targetItemMap.put(EnchantmentTarget.ARMOR_FEET, Items.NETHERITE_BOOTS);

        targetItemMap.put(EnchantmentTarget.BOW, Items.BOW);
        targetItemMap.put(EnchantmentTarget.CROSSBOW, Items.CROSSBOW);
        targetItemMap.put(EnchantmentTarget.FISHING_ROD, Items.FISHING_ROD);
        targetItemMap.put(EnchantmentTarget.TRIDENT, Items.TRIDENT);

        targetItemMap.put(EnchantmentTarget.DIGGER, Items.NETHERITE_PICKAXE);
        targetItemMap.put(EnchantmentTarget.BREAKABLE, Items.NETHERITE_HOE);
        targetItemMap.put(EnchantmentTarget.WEAPON, Items.NETHERITE_SWORD);
        targetItemMap.put(EnchantmentTarget.WEARABLE, Items.NETHERITE_CHESTPLATE);
        targetItemMap.put(EnchantmentTarget.VANISHABLE, Items.CARVED_PUMPKIN);
    }

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

                    StackBuilder builder = new StackBuilder();

                    builder.item(targetItemMap.get(ench.type));

//                    builder.enchant(ench, i);

//                    if (Enchantments.FLAME.equals(ench)) {
//                        builder.tag(StringNbtReader.parse("{Potion: \"minecraft:fire_resistance\"}"));
//                    } else if (Enchantments.POWER.equals(ench)) {
//                        builder.tag(StringNbtReader.parse("{Potion: \"minecraft:strength\"}"));
//                    } else if (Enchantments.QUICK_CHARGE.equals(ench)) {
//                        builder.tag(StringNbtReader.parse("{ChargedProjectiles: [{id: \"minecraft:arrow\", Count: 1b}], Charged: 1b}"));
//                    }

                    // put nbt matcher
                    map.put(
                            new Identifier("ench", id),
                            new NbtModifier(
                                    new Identifier("enchantments", ench.type.toString().toLowerCase(Locale.ROOT)),
                                    new Identifier("ench", id),
                                    new Identifier("enchantments", id),
                                    null,
                                    accepts,
                                    new MatcherDisplay(builder, false)
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

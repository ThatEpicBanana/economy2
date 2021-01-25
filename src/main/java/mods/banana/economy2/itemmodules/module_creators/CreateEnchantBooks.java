package mods.banana.economy2.itemmodules.module_creators;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import mods.banana.economy2.itemmodules.items.JsonNbtItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CreateEnchantBooks {
    public static void onInit() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            // roman numerals
            String[] roman = {"i", "ii", "iii", "iv", "v"};

            // initialize array
            JsonArray array = new JsonArray();

            for(Enchantment ench : Registry.ENCHANTMENT) {
                for(int i = 1; i <= ench.getMaxLevel(); i++) {
//                    array.add(new Gson().toJsonTree(new JsonNbtItem(
//                            "item",
//                            "minecraft:enchanted_book",
//                            "book:" + Registry.ENCHANTMENT.getId(ench).getPath() + (ench.getMaxLevel() != 1 ? "-" + roman[i-1] : ""),
//                            "{StoredEnchantments:[{lvl:" + i + "s, id: \"minecraft:" + Registry.ENCHANTMENT.getId(ench).getPath() + "\"}]}",
//                            null
//                    )));
                }
            }

            Files.write(Paths.get("enchantment_books.json"), gson.toJson(array).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

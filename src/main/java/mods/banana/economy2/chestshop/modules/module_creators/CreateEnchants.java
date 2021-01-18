package mods.banana.economy2.chestshop.modules.module_creators;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import mods.banana.economy2.chestshop.modules.JsonNbtItem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateEnchants {
    public static void onInit() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

//            System.out.println(CreateEnchants.class.getResource("/").getPath() + "enchantment_books.json");
//            Path source = Paths.get(CreateEnchants.class.getResource("/").getPath());
//            Path file = Paths.get(source.toAbsolutePath().toString() + "/enchantment_books.json");
//            FileWriter writer = new FileWriter(file.toFile());
//            FileWriter writer = new FileWriter(Paths.get(FabricLoader.getInstance().getConfigDir().toString(), "enchantment_books.json").toFile());

            JsonArray array = new JsonArray();

            String[] roman = {"i", "ii", "iii", "iv", "v"};

            for(Enchantment ench : Registry.ENCHANTMENT) {
                for(int i = 1; i <= ench.getMaxLevel(); i++) {
                    array.add(new Gson().toJsonTree(new JsonNbtItem(
                            "minecraft:enchanted_book",
                            "ench:" + Registry.ENCHANTMENT.getId(ench).getPath() + (ench.getMaxLevel() != 1 ? "-" + roman[i-1] : ""),
                            "{StoredEnchantments:[{lvl:" + i + "s, id: \"minecraft:" + Registry.ENCHANTMENT.getId(ench).getPath() + "\"}]}",
                            null
                    )));
                }
            }

            Files.write(Paths.get("enchantment_books.json"), gson.toJson(array).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

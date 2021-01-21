package mods.banana.economy2.chestshop.itemmodules.module_creators;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import mods.banana.economy2.itemmodules.JsonNbtItem;
import net.minecraft.potion.Potion;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CreatePotions {
    public static void onInit() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JsonArray array = new JsonArray();

        for(Potion potion : Registry.POTION) {
            array.add(gson.toJsonTree(new JsonNbtItem(
                    "minecraft:potion",
                    "pot:" + getName(potion),
                    "{Potion:\"" + Registry.POTION.getId(potion).toString() + "\"}",
                    null
            )));
        }

        for(Potion potion : Registry.POTION) {
            array.add(gson.toJsonTree(new JsonNbtItem(
                    "minecraft:splash_potion",
                    "splash:" + getName(potion),
                    "{Potion:\"" + Registry.POTION.getId(potion).toString() + "\"}",
                    null
            )));
        }

        for(Potion potion : Registry.POTION) {
            array.add(gson.toJsonTree(new JsonNbtItem(
                    "minecraft:lingering_potion",
                    "linger:" + getName(potion),
                    "{Potion:\"" + Registry.POTION.getId(potion).toString() + "\"}",
                    null
            )));
        }

        for(Potion potion : Registry.POTION) {
            array.add(gson.toJsonTree(new JsonNbtItem(
                    "minecraft:tipped_arrow",
                    "arrow:" + getName(potion),
                    "{Potion:\"" + Registry.POTION.getId(potion).toString() + "\"}",
                    null
            )));
        }

        try {
            Files.write(Paths.get("potions.json"), gson.toJson(array).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getName(Potion potion) {
        return Registry.POTION.getId(potion).getPath().replaceAll("long", "lng").replaceAll("strong", "str");
    }
}

package mods.banana.economy2.chestshop.modules;

import mods.banana.economy2.Economy2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

public class ItemModules {
    public static void onInit() throws IOException {
        registerConfig(new ItemModule("heads", "heads.json"));
        registerConfig(new ItemModule("god_items", "god_items.json"));
        registerConfig(new ItemModule("enchantment_books", "enchantment_books.json"));
//        System.out.println(ItemModuleHandler.getModule("enchantment_books"));
    }

    private static void registerConfig(ItemModule module) {
        ItemModuleHandler.register(module);
        if(Economy2.CONFIG.getValue("chestShop.ItemModule." + module.getName(), Boolean.class)) ItemModuleHandler.activeModules.add(module);
    }
}

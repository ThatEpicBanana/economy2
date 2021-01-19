package mods.banana.economy2.chestshop.itemmodules;

import mods.banana.economy2.Economy2;

public class ItemModules {
    public static void onInit() {
        ClassLoader loader = ItemModules.class.getClassLoader();
        registerConfig(new ItemModule("heads", "heads.json", loader));
        registerConfig(new ItemModule("god_items", "god_items.json", loader));
        registerConfig(new ItemModule("enchantment_books", "enchantment_books.json", loader));
        registerConfig(new ItemModule("potions", "potions.json", loader));
    }

    private static void registerConfig(ItemModule module) {
        ItemModuleHandler.register(module);
        if(Economy2.CONFIG.getValue("chestShop.ItemModule." + module.getName(), Boolean.class)) ItemModuleHandler.activeModules.add(module);
    }
}

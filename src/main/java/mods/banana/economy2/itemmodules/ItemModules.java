package mods.banana.economy2.itemmodules;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import mods.banana.economy2.Economy2;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ItemModules {
    public static void onInit() {
//        ClassLoader loader = ItemModules.class.getClassLoader();
//        registerConfig(new ItemModule("heads", "heads.json", loader));
//        registerConfig(new ItemModule("god_items", "god_items.json", loader));
//        registerConfig(new ItemModule("enchantment_books", "enchantment_books.json", loader));
//        registerConfig(new ItemModule("potions", "potions.json", loader));
//        registerConfig(new ItemModule("enchants", "enchants.json", loader));
//
//        getItemModulesFromFile();
    }

    private static void registerConfig(ItemModule module) {
        ItemModuleHandler.register(module);
        if(Economy2.CONFIG.getValue("ItemModule." + module.getName(), Boolean.class)) ItemModuleHandler.activeModules.add(module);
    }

    public static void getItemModulesFromFile() {
        try {
            // get item module file
            String saveDirectory = Economy2.CONFIG.getValue("file.saveDirectory", String.class);
            Path itemModuleFile = Path.of(saveDirectory, "/itemModules.json");
            // create new file if needed
            createFile(itemModuleFile);

            // get item modules from file
            JsonArray itemModules = new Gson().fromJson(new FileReader(itemModuleFile.toString()), JsonArray.class);

            // for each item module
            for(JsonElement itemModule : itemModules) {
                // register file
                ItemModuleHandler.register(new ItemModule(itemModule.getAsString(), new FileReader(saveDirectory + "/" + itemModule.getAsString())));
                // activate file
                ItemModuleHandler.activate(itemModule.getAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createFile(Path file) throws IOException {
        // create new file if needed
        if(!Files.exists(file)) {
            // log file creation
            if(file.toFile().createNewFile()) Economy2.LOGGER.info("Created item module file");

            // create new writer
            FileWriter writer = new FileWriter(file.toString());

            // write basic json
            writer.write("[]");
            // close writer
            writer.close();
        }
    }
}

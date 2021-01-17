package mods.banana.economy2.chestshop;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.oroarmor.config.ConfigItem;
import mods.banana.economy2.Economy2;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemModules {
    public static ArrayList<ItemModule> registeredModules = new ArrayList<>();
    public static ArrayList<ItemModule> activeModules = new ArrayList<>();

    public static void onInit() {
        ItemModule heads = new ItemModule("heads", "heads.json");
        registeredModules.add(heads);
        if(Economy2.CONFIG.getValue("chestShop.nbtItemModule.heads", Boolean.class)) activeModules.add(heads);
    }

    public static ItemStack fromIdentifier(Identifier identifier) {
        for(ItemModule module : activeModules) {
            if(module.getValues().containsKey(identifier)) {
                return module.getValues().get(identifier);
            }
        }
        return null;
    }

    public static Identifier fromStack(ItemStack itemStack) {
        for(ItemModule module : activeModules) {
            for(Identifier current : module.getValues().keySet()) {
                if(ScreenHandler.canStacksCombine(itemStack, module.getValues().get(current))) return current;
            }
        }
        return null;
    }

    public static void onChange(ConfigItem<Boolean> item) {
        // for each module
        for(ItemModule module : registeredModules) {
            // check if module changed is this one
            if(item.getName().equals(module.getName())) {
                // if so, add or remove the module
                if(item.getValue()) {
                    // make sure the active modules do not already contain the module
                    if(!activeModules.contains(module)) activeModules.add(module);
                } else activeModules.remove(module);

                return;
            }
        }
    }
}

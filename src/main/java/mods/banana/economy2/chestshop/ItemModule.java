package mods.banana.economy2.chestshop;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemModule {
    private final String name;
    private final Map<Identifier, ItemStack> values;
    private final NbtItem[] items;

    public ItemModule(String name, Map<Identifier, ItemStack> values) {
        this.name = name;
        this.values = values;
        this.items = new NbtItem[10];
    }

    /**
     * Returns a new ItemModule created from a json file
     * The json file must be an array of Items to be added.
     * An item is defined by a base item, identifier, and tag - all of them strings.
     * @param name module name
     * @param fileName file name originating from the resources folder
     */
    public ItemModule(String name, String fileName) {
        this(name, new InputStreamReader(ItemModules.class.getClassLoader().getResourceAsStream(fileName)));
    }

    public ItemModule(String name, Reader reader) {
        // output values
        Map<Identifier, ItemStack> values = new HashMap<>();

        // get the items from the file
        List<NbtItem> items = new Gson().fromJson(reader, new TypeToken<List<NbtItem>>() {}.getType());

        // for each item
        for(NbtItem item : items) {
            // get new item and put it into a stack
            ItemStack newStack = new ItemStack(Registry.ITEM.get(new Identifier(item.item)));
            // set stack's tag using the brigadier string reader
            try {
                newStack.setTag(StringNbtReader.parse(item.tag));
            } catch (CommandSyntaxException e) { e.printStackTrace(); }
            // add stack to values with associated identifier
            values.put(new Identifier(item.identifier), newStack);
        }

        this.name = name;
        this.values = values;
        this.items = new NbtItem[10];
    }

    public String getName() { return name; }
    public Map<Identifier, ItemStack> getValues() { return values; }
}

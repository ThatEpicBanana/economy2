package mods.banana.economy2.chestshop.modules;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ibm.icu.impl.ClassLoaderUtil;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.nio.cs.StreamDecoder;
import sun.nio.cs.UTF_8;

public class ItemModule {
    private final String name;
    private final Map<Identifier, NbtItem> values;

    public ItemModule(String name, Map<Identifier, NbtItem> values) {
        this.name = name;
        this.values = values;
    }

    /**
     * Returns a new ItemModule created from a json file defined by an array of Items to be added.
     * An item is defined by a base item, identifier, optional parent, and tag - all of them strings.
     * @param name module name
     * @param file file name originating from the resources folder
     */
    public ItemModule(String name, String file) throws IOException {
        this(name, new InputStreamReader(ItemModuleHandler.class.getClassLoader().getResourceAsStream(file)));
    }

    /**
     * Returns a new ItemModule created from a json reader defined by an array of Items to be added.
     * An item is defined by a base item, identifier, optional parent, and tag - all of them strings.
     * @param name module name
     * @param reader reader to be read from
     */
    public ItemModule(String name, Reader reader) {
        // output values
        Map<Identifier, NbtItem> values = new HashMap<>();

        // get the items from the file
        List<JsonNbtItem> items = new Gson().fromJson(reader, new TypeToken<List<JsonNbtItem>>() {}.getType());

        try {
            // for each item
            for(JsonNbtItem jsonItem : items) {
                NbtItem item = jsonItem.toNbtItem();
                values.put(item.getIdentifier(), item);
            }
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }

        this.name = name;
        this.values = values;
    }

    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append(name).append(":\n");
        for(NbtItem item : values.values()) {
            string.append(item.toString()).append("\n");
        }
        return string.toString();
    }

    public String getName() { return name; }
    public Map<Identifier, NbtItem> getValues() { return values; }
}

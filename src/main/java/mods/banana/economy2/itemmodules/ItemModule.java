package mods.banana.economy2.itemmodules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import mods.banana.economy2.itemmodules.items.JsonNbtItem;
import net.minecraft.util.Identifier;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemModule {
    private final String name;
    private final Map<Identifier, NbtMatcher> values;

    public ItemModule(String name, Map<Identifier, NbtMatcher> values) {
        this.name = name;
        this.values = values;
    }

    /**
     * Returns a new ItemModule created from a json file defined by an array of Items to be added.
     * An item is defined by a base item, identifier, optional parent, and tag - all of them strings.
     * @param name module name
     * @param file file name originating from the resources folder
     * @param classLoader classloader for your class, used to get to resource folder
     */
    public ItemModule(String name, String file, ClassLoader classLoader) {
        this(name, new InputStreamReader(classLoader.getResourceAsStream(file)));
    }

    /**
     * Returns a new ItemModule created from a json reader defined by an array of Items to be added.
     * An item is defined by a base item, identifier, optional parent, and tag - all of them strings.
     * @param name module name
     * @param reader reader to be read from
     */
    public ItemModule(String name, Reader reader) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Identifier.class, new Identifier.Serializer()).create();

        // output values
        Map<Identifier, NbtMatcher> values = new HashMap<>();

        // get the items from the file
        List<JsonNbtItem> items = gson.fromJson(reader, new TypeToken<List<JsonNbtItem>>() {}.getType());

        // for each item
        for(JsonNbtItem jsonItem : items) {
            NbtMatcher item = jsonItem.toNbtItem();
            values.put(item.getIdentifier(), item);
        }

        this.name = name;
        this.values = values;

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append(name).append(":\n");
        for(NbtMatcher item : values.values()) {
            string.append(item.toString()).append("\n");
        }
        return string.toString();
    }

    public String getName() { return name; }
    public Map<Identifier, NbtMatcher> getValues() { return values; }
}

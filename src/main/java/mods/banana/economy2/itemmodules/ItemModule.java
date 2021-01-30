package mods.banana.economy2.itemmodules;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import mods.banana.bananaapi.helpers.ItemStackHelper;
import mods.banana.economy2.itemmodules.display.ModuleDisplay;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import mods.banana.economy2.itemmodules.items.JsonNbtItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemModule {
    private final String name;
    private final Map<Identifier, NbtMatcher> values;
    private final ModuleDisplay display;

    public ItemModule(String name, Map<Identifier, NbtMatcher> values, ModuleDisplay display) {
        this.name = name;
        this.values = values;
        this.display = display;
    }

    public ItemModule(String name, Map<Identifier, NbtMatcher> values) {
        this(name, values, null);
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
    @Deprecated
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
        this.display = null;

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

    public ItemStack getItemStack() {
        if(hasDisplay()) { // if has display
            ModuleDisplay display = getDisplay();
            if(display.hasStack()) return display.getStack(); // if display has a set stack, return it
            else return ItemStackHelper.setName(new ItemStack(Items.PAPER), new LiteralText(display.getName())); // else return paper with the display name
        } else return ItemStackHelper.setName(new ItemStack(Items.PAPER), new LiteralText(getName())); // else return paper with the module name
    }

    public String getName() { return name; }
    public Map<Identifier, NbtMatcher> getValues() { return values; }

    public boolean hasDisplay() { return display != null; }
    public ModuleDisplay getDisplay() { return display; }

    public static class Serializer implements JsonSerializer<ItemModule>, JsonDeserializer<ItemModule> {
        public static Gson GSON = new GsonBuilder()
                .registerTypeAdapter(ItemModule.class, new ItemModule.Serializer())
                .registerTypeAdapter(NbtMatcher.class, new NbtMatcher.Serializer())
                .registerTypeAdapter(ModuleDisplay.class, new ModuleDisplay.Serializer())
                .create();

        @Override
        public JsonElement serialize(ItemModule src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray array = new JsonArray();

            for(NbtMatcher matcher : src.getValues().values()) {
                array.add(context.serialize(matcher, NbtMatcher.class));
            }

            JsonObject module = new JsonObject();
            module.add("values", array);

            module.addProperty("name", src.getName());

            return module;
        }

        @Override
        public ItemModule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();

            Map<Identifier, NbtMatcher> values = new HashMap<>();

            for(JsonElement element : object.get("values").getAsJsonArray()) {
                NbtMatcher matcher = context.deserialize(element, NbtMatcher.class);
                values.put(matcher.getIdentifier(), matcher);
            }

            return new ItemModule(
                    object.get("name").getAsString(),
                    values,
                    object.has("display") ? context.deserialize(object.get("display"), ModuleDisplay.class) : null
            );
        }
    }
}

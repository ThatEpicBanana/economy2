package mods.banana.economy2.itemmodules;

import com.google.gson.*;
import mods.banana.bananaapi.helpers.ItemStackHelper;
import mods.banana.economy2.itemmodules.display.ModuleDisplay;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import mods.banana.economy2.itemmodules.items.accepts.DefaultedAccepts;
import mods.banana.economy2.itemmodules.items.accepts.ListAccepts;
import mods.banana.economy2.itemmodules.items.accepts.MatcherAccepts;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;
import java.util.HashMap;
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

    public Map<Identifier, NbtMatcher> getValuesOfType(NbtMatcher.Type type) {
        Map<Identifier, NbtMatcher> values = new HashMap<>();

        for(Map.Entry<Identifier, NbtMatcher> matcher : getValues().entrySet()) {
            if(matcher.getValue().typeMatches(type))
                values.put(matcher.getKey(), matcher.getValue());
        }

        return values;
    }

    public boolean hasDisplay() { return display != null; }
    public ModuleDisplay getDisplay() { return display; }

    public static class Serializer implements JsonSerializer<ItemModule>, JsonDeserializer<ItemModule> {
        public static Gson GSON = new GsonBuilder()
                .registerTypeAdapter(ItemModule.class, new ItemModule.Serializer())
                .registerTypeAdapter(NbtMatcher.class, new NbtMatcher.Serializer())
                .registerTypeAdapter(ModuleDisplay.class, new ModuleDisplay.Serializer())
                .registerTypeAdapter(MatcherAccepts.class, new MatcherAccepts.Serializer())
                .registerTypeAdapter(ListAccepts.class, new MatcherAccepts.Serializer())
                .registerTypeAdapter(DefaultedAccepts.class, new MatcherAccepts.Serializer())
                .setPrettyPrinting()
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

            if(src.hasDisplay()) module.add("display", context.serialize(src.getDisplay()));

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

package mods.banana.economy2.itemmodules.display;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mods.banana.bananaapi.helpers.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;

public class ModuleDisplay {
    private final String name;
    private final ItemStack stack;

    public ModuleDisplay(String name) {
        this(name, null);
    }

    public ModuleDisplay(String name, ItemStack stack) {
        this.name = name;
        this.stack = stack;
    }

    public ItemStack getStack() { return stack; }
    public boolean hasStack() { return stack != null; }
    public String getName() { return name; }

    public static class Serializer implements JsonSerializer<ModuleDisplay>, JsonDeserializer<ModuleDisplay> {
        public static Gson GSON = new GsonBuilder().registerTypeAdapter(ModuleDisplay.class, new ModuleDisplay.Serializer()).create();

        @Override
        public ModuleDisplay deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            JsonObject itemTag = object.get("item").getAsJsonObject();
            try {
                // get all values from nbt
                String name = object.has("name") ? object.get("name").getAsString() : null;
                CompoundTag nbt = itemTag.has("tag") ? StringNbtReader.parse(itemTag.get("tag").getAsString()) : new CompoundTag();
                Item item = Registry.ITEM.getOrEmpty(new Identifier(itemTag.get("item").getAsString())).orElse(null);

                // if display has tag but no item, set the item to paper
                if(nbt != null && item == null) item = Items.PAPER;

                // if there is an item, create a new item stack
                if(item != null) return new ModuleDisplay(name, ItemStackHelper.setTag(new ItemStack(item), nbt));
                // if there isn't, just create one with a name
                else return new ModuleDisplay(name);
            } catch (CommandSyntaxException e) {
                throw new JsonParseException(e);
            }
        }

        @Override
        public JsonElement serialize(ModuleDisplay src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();

            if(src.getName() != null) object.addProperty("name", src.getName());

            JsonObject item = new JsonObject();
            if(!src.getStack().getItem().equals(Items.AIR)) item.addProperty("item", Registry.ITEM.getId(src.getStack().getItem()).toString());
            if(src.getStack().hasTag()) item.addProperty("tag", src.getStack().getTag().toString());

            object.add("item", item);

            return object;
        }
    }
}

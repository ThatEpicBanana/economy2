package mods.banana.economy2.itemmodules.display;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mods.banana.bananaapi.itemsv2.StackBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;

public class MatcherDisplay {
    private final Item item;
    private final CompoundTag tag;

    public MatcherDisplay(Item item, CompoundTag tag) {
        this.item = item;
        this.tag = tag;
    }

    public MatcherDisplay(ItemStack stack) {
        this(stack.getItem(), stack.getOrCreateTag());
    }

    public Item getItem() { return item; }
    public CompoundTag getTag() { return tag; }

    public static class Serializer implements JsonSerializer<MatcherDisplay>, JsonDeserializer<MatcherDisplay> {
        @Override
        public MatcherDisplay deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();

            StackBuilder builder = new StackBuilder();

            if(object.has("item")) builder.item(Registry.ITEM.get(new Identifier(object.get("item").getAsString())));

            try {
                builder.tag(object.has("nbt") ? StringNbtReader.parse(object.get("nbt").getAsString()) : new CompoundTag());
            } catch (CommandSyntaxException e) {
                throw new JsonSyntaxException("Invalid nbt tag: " + e.getMessage());
            }

            if(object.has("lore")) {
                // text serializer already knows how to handle lone strings
            }

            return null;
        }

        @Override
        public JsonElement serialize(MatcherDisplay src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();

            if(src.getItem() != null) object.addProperty("item", Registry.ITEM.getId(src.getItem()).toString());
            if(src.getTag() != null) object.addProperty("nbt", src.tag.toString());

            return object;
        }
    }
}

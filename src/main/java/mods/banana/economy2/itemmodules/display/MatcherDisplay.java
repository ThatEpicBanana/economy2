package mods.banana.economy2.itemmodules.display;

import com.google.gson.*;
import mods.banana.bananaapi.itemsv2.StackBuilder;
import mods.banana.bananaapi.itemsv2.StackReader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.lang.reflect.Type;

public class MatcherDisplay {
//    private final Item item;
//    private final CompoundTag tag;
    private final StackBuilder builder;
    private final boolean hasName;
    private final boolean overwriteTag;

    public MatcherDisplay(Item item, CompoundTag tag, boolean overwriteTag) {
        this.overwriteTag = overwriteTag;
        this.builder = new StackBuilder().item(item).tag(tag);
        hasName = slowHasName();
    }

    public MatcherDisplay(ItemStack stack, boolean overwriteTag) {
        this.builder = new StackBuilder(stack);
        this.overwriteTag = overwriteTag;
        hasName = slowHasName();
    }

    public MatcherDisplay(StackBuilder builder, boolean overwriteTag) {
        this.builder = builder;
        this.overwriteTag = overwriteTag;
        hasName = slowHasName();
    }

    private boolean slowHasName() {
        return new StackReader(getStack()).getName() != null;
    }

    public ItemStack getStack() {
        return getBuilder().build();
    }

    public Item getItem() { return builder.build().getItem(); }
    public CompoundTag getTag() { return builder.build().getOrCreateTag(); }
    public StackBuilder getBuilder() { return builder.clone(); }
    public boolean hasName() { return hasName; }
    public boolean overwritesTag() { return overwriteTag; }

    public static class Serializer implements JsonSerializer<MatcherDisplay>, JsonDeserializer<MatcherDisplay> {
        private static final Gson GSON = new GsonBuilder()
                .registerTypeAdapter(MatcherDisplay.class, new MatcherDisplay.Serializer())
                .create();

        @Override
        public MatcherDisplay deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            return new MatcherDisplay(
                    StackBuilder.Serializer.fromJson(json).build(),
                    object.has("overwrite") && object.get("overwrite").getAsJsonPrimitive().getAsBoolean()
            );
        }

        @Override
        public JsonElement serialize(MatcherDisplay src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = StackBuilder.Serializer.toJson(new StackBuilder().item(src.getItem()).tag(src.getTag())).getAsJsonObject();
            object.addProperty("overwrite", src.overwritesTag());
            return object;
        }

        public static MatcherDisplay fromJson(JsonElement json) {
            return GSON.fromJson(json, MatcherDisplay.class);
        }

        public static JsonElement toJson(MatcherDisplay display) {
            return GSON.toJsonTree(display);
        }
    }
}

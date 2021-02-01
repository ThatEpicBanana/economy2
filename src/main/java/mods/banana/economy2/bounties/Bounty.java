package mods.banana.economy2.bounties;

import com.google.gson.*;
import mods.banana.economy2.itemmodules.ItemModuleHandler;
import mods.banana.economy2.itemmodules.items.NbtItem;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Bounty {
    private final UUID owner;
    private final NbtItem baseItem;
    private final List<NbtMatcher> mustMatch;
    private final List<NbtMatcher> cannotMatch;
    private final int amount;
    private final long price;

    public Bounty(UUID owner, NbtItem baseItem, List<NbtMatcher> mustMatch, int amount, long price) {
        this.owner = owner;
        this.baseItem = baseItem;
        this.mustMatch = mustMatch;
        this.cannotMatch = new ArrayList<>();
        this.amount = amount;
        this.price = price;
    }

    public Bounty(UUID owner, NbtItem baseItem, List<NbtMatcher> mustMatch, List<NbtMatcher> cannotMatch, int amount, long price) {
        this.owner = owner;
        this.baseItem = baseItem;
        this.mustMatch = mustMatch;
        this.cannotMatch = cannotMatch;
        this.amount = amount;
        this.price = price;
    }

    public ItemStack toItemStack() {
        // initialize stack
        ItemStack stack = baseItem.toItemStack();
        // set tag if null
        if(!stack.hasTag()) stack.setTag(new CompoundTag());
        // apply each nbt matcher to stack
        for (NbtMatcher item : mustMatch) item.apply(stack);
        // return the new stack
        return stack;
    }

    public int getAmount() { return amount; }
    public long getPrice() { return price; }

    public UUID getOwner() { return owner; }
    public NbtItem getBaseItem() { return baseItem; }

    public List<NbtMatcher> getMustMatch() { return mustMatch; }
    public List<NbtMatcher> getCannotMatch() { return cannotMatch; }

    public static class Serializer implements JsonSerializer<Bounty>, JsonDeserializer<Bounty> {
        public static Gson GSON = new GsonBuilder().registerTypeAdapter(Bounty.class, new Bounty.Serializer()).create();

        @Override
        public Bounty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();

            List<NbtMatcher> mustMatch = new ArrayList<>();
            for(JsonElement element : object.get("mustMatch").getAsJsonArray()) {
                NbtMatcher matcher = ItemModuleHandler.getRegisteredMatcher(new Identifier(element.getAsString()));
                if(matcher == null) throw new JsonParseException("cannot find matcher " + element.getAsString() + " in bounty");
                mustMatch.add(matcher);
            }

            List<NbtMatcher> cannotMatch = new ArrayList<>();
            for(JsonElement element : object.get("cannotMatch").getAsJsonArray()) {
                NbtMatcher matcher = ItemModuleHandler.getRegisteredMatcher(new Identifier(element.getAsString()));
                if(matcher == null) throw new JsonParseException("cannot find matcher " + element.getAsString() + " in bounty");
                cannotMatch.add(matcher);
            }

            Identifier baseItemId = new Identifier(object.get("baseItem").getAsString());
            NbtMatcher baseItem = ItemModuleHandler.getRegisteredMatcher(baseItemId);

            if(baseItem == null) {
                Item item = Registry.ITEM.getOrEmpty(baseItemId).orElse(null);
                if(item != null) baseItem = new NbtItem(item);
            }

            if(!(baseItem instanceof NbtItem)) throw new JsonParseException("base item " + object.get("baseItem").getAsString() + " was not found");

            return new Bounty(
                    UUID.fromString(object.get("owner").getAsString()),
                    (NbtItem) baseItem,
                    mustMatch,
                    cannotMatch,
                    object.get("amount").getAsInt(),
                    object.get("price").getAsLong()
            );
        }

        @Override
        public JsonElement serialize(Bounty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();

            object.addProperty("owner", src.getOwner().toString());
            object.addProperty("baseItem", src.getBaseItem().getIdentifier().toString());

            object.addProperty("amount", src.getAmount());
            object.addProperty("price", src.getPrice());

            JsonArray mustMatch = new JsonArray();
            for(NbtMatcher matcher : src.getMustMatch()) {
                mustMatch.add(matcher.getIdentifier().toString());
            }

            JsonArray cannotMatch = new JsonArray();
            for(NbtMatcher matcher : src.getCannotMatch()) {
                cannotMatch.add(matcher.getIdentifier().toString());
            }

            object.add("mustMatch", mustMatch);
            object.add("cannotMatch", cannotMatch);

            return object;
        }
    }
}

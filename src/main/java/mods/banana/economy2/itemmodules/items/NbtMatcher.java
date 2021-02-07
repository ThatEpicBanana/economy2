package mods.banana.economy2.itemmodules.items;

import com.google.gson.*;
import mods.banana.bananaapi.helpers.PredicateHelper;
import mods.banana.bananaapi.helpers.TagHelper;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.itemmodules.interfaces.ConditionInterface;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public abstract class NbtMatcher {
    private final Identifier identifier;
    private final Identifier predicate;
    private final Identifier parent;
    private final Identifier accepts;
    private List<NbtMatcher> children = new ArrayList<>();

    public enum Type {
        ITEM,
        MODIFIER,
        BOTH // (used for matching)
    }

    // full constructor with parent
    public NbtMatcher(Identifier identifier, Identifier predicate, Identifier parent, Identifier accepts) {
        this.identifier = identifier;
        this.predicate = predicate;
        this.parent = parent;
        this.accepts = accepts;
    }

    // full constructor with children
    public NbtMatcher(Identifier identifier, Identifier predicate, Identifier parent, Identifier accepts, List<NbtMatcher> children) {
        this(identifier, predicate, parent, accepts);
        this.children = children;
    }

    public CompoundTag getCompoundTag() {
        return getPredicateInfo().getTag();
    }

    public boolean matches(ItemStack stack, Type type) {
        if(softMatches(stack, type)) {
            // make sure children don't match
            for(NbtMatcher child : children) if(child.softMatches(stack, type)) return false;
            // if none do return yes
            return true;
        }
        return false;
    }

    public boolean softMatches(ItemStack stack, Type type) {
        return typeMatches(type) && // check if type matches
                itemMatches(stack.getItem()) && // check if item matches
                (getPredicateId() == null || PredicateHelper.test(getPredicate(), stack)); // check if predicate matches
//        new MatchToolLootCondition(ItemPredicate.ANY);
    }

    public abstract boolean itemMatches(Item item);
    public abstract Item getItem();
    public abstract Identifier getItemId();

    public boolean typeMatches(Type type) {
        return type == Type.BOTH || type == getType();
    }

    public ItemStack apply(ItemStack stack) {
        // either combine tags
        if(stack.hasTag()) stack.setTag(TagHelper.combine(stack.getTag(), getPredicateInfo().getTag()));
        // or just set it
        else stack.setTag(getPredicateInfo().getTag().copy());

        // and return
        return stack;
    }

    public boolean accepts(ItemStack stack) {
        return accepts == null || PredicateHelper.test(getAccepts(), stack);
    }

    public boolean accepts(NbtMatcher matcher, Item baseItem) {
        ItemStack stack = new ItemStack(baseItem);
        stack.setTag(matcher.getCompoundTag());
        return accepts(stack);
    }

    public ItemStack toItemStack() {
        ItemStack stack = new ItemStack(getItem());
        if(getPredicateId() != null) stack.setTag(getPredicateInfo().getTag().copy());
        return stack;
    }

    public String toString() {
        return identifier.toString() + (parent != null ? " - parent: " + parent.toString() : "");
    }

    public static class Serializer implements JsonSerializer<NbtMatcher>, JsonDeserializer<NbtMatcher> {
        public static Gson GSON = new GsonBuilder().registerTypeAdapter(NbtMatcher.class, new NbtMatcher.Serializer()).create();

        @Override
        public NbtMatcher deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();

            if(!(object.has("identifier") && object.has("predicate"))) throw new JsonParseException("Nbt matcher is not complete");

            if(object.has("item") && !object.get("item").getAsString().startsWith("#")) {
                // item
                return new NbtItem(
                        Registry.ITEM.get(identifier(object, "item")),
                        identifier(object, "identifier"),
                        identifier(object, "predicate"),
                        object.has("parent") ? identifier(object, "parent") : null,
                        object.has("accepts") ? identifier(object, "accepts") : null
                );
            } else {
                // modifier
                return new NbtModifier(
                        object.has("item") ? identifier(object, "item") : null, // just in case it has a tag
                        identifier(object, "identifier"),
                        identifier(object, "predicate"),
                        object.has("parent") ? identifier(object, "parent") : null,
                        object.has("accepts") ? identifier(object, "accepts") : null
                );
            }
        }

        private Identifier identifier(JsonObject object, String string) { return new Identifier(object.get(string).getAsString().replace("#", "")); }

        @Override
        public JsonElement serialize(NbtMatcher src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();

            object.addProperty("identifier", src.getIdentifier().toString());
            object.addProperty("predicate", src.getPredicateId().toString());

            if(src.getItemId() != null) object.addProperty("item", src.getItemId().toString());

            if(src.hasParent()) object.addProperty("parent", src.getParent().toString());
            if(src.getAcceptsId() != null) object.addProperty("accepts", src.getAcceptsId().toString());
            //TODO: add custom items uwu

            return object;
        }
    }

    // so many gets
    public Identifier getIdentifier() { return identifier; }

    public Identifier getPredicateId() { return predicate; }
    public LootCondition getPredicate() { return Economy2.server.getPredicateManager().get(getPredicateId()); }
    public ConditionInterface getPredicateInfo() { return (ConditionInterface) getPredicate(); }

    public Identifier getAcceptsId() { return accepts; }
    public LootCondition getAccepts() { return Economy2.server.getPredicateManager().get(getPredicateId()); }
    public ConditionInterface getAcceptsInfo() { return (ConditionInterface) getPredicate(); }

    public boolean hasParent() { return parent != null; }
    public Identifier getParent() { return parent; }

    public void addChild(NbtMatcher item) { children.add(item); }
    public List<NbtMatcher> getChildren() { return children; }
    public boolean hasChildren() { return !children.isEmpty(); }

    public abstract Type getType();
}

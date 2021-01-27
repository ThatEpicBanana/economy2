package mods.banana.economy2.itemmodules.items;

import mods.banana.bananaapi.helpers.PredicateHelper;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.itemmodules.interfaces.mixin.ConditionInterface;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public abstract class NbtMatcher {
    private final Identifier identifier;
    private final Identifier predicate;
    private final Identifier parent;
    private List<NbtMatcher> children = new ArrayList<>();

    public enum Type {
        ITEM,
        MODIFIER,
        BOTH // (used for matching)
    }

    // full constructor with parent
    public NbtMatcher(Identifier identifier, Identifier predicate, Identifier parent) {
        this.identifier = identifier;
        this.predicate = predicate;
        this.parent = parent;
    }

    // full constructor with children
    public NbtMatcher(Identifier identifier, Identifier predicate, Identifier parent, List<NbtMatcher> children) {
        this(identifier, predicate, parent);
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
                PredicateHelper.test(getPredicate(), stack); // check if predicate matches
    }

    public boolean typeMatches(Type type) {
        return type == Type.BOTH || type == getType();
    }

    public String toString() {
        return identifier.toString() + (parent != null ? " - parent: " + parent.toString() : "");
    }

    // so many gets
    public Identifier getIdentifier() { return identifier; }
    public Identifier getPredicateId() { return predicate; }
    public LootCondition getPredicate() { return Economy2.server.getPredicateManager().get(getPredicateId()); }
    public ConditionInterface getPredicateInfo() { return (ConditionInterface) getPredicate(); }

    public boolean hasParent() { return parent != null; }
    public Identifier getParent() { return parent; }

    public void addChild(NbtMatcher item) { children.add(item); }
    public List<NbtMatcher> getChildren() { return children; }
    public boolean hasChildren() { return !children.isEmpty(); }

    public abstract Type getType();
}

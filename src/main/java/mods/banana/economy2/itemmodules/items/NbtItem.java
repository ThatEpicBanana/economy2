package mods.banana.economy2.itemmodules.items;

import mods.banana.economy2.Economy2;
import mods.banana.economy2.itemmodules.ItemModule;
import mods.banana.economy2.itemmodules.ItemModuleHandler;
import mods.banana.economy2.itemmodules.interfaces.ConditionInterface;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NbtItem extends BaseNbtItem {
    private final Identifier identifier;
    private final Identifier predicate;
    private final Identifier parent;
    private List<NbtItem> children = new ArrayList<>();

    public enum Type {
        ITEM,
        MODIFIER,
        BOTH // (used for matching)
    }

    // full constructor with parent
    public NbtItem(Item item, Identifier identifier, Identifier predicate, Identifier parent) {
        super(item);
        this.identifier = identifier;
        this.predicate = predicate;
        this.parent = parent;
    }

    // full constructor with children
    public NbtItem(Item item, Identifier identifier, Identifier predicate, Identifier parent, List<NbtItem> children) {
        this(item, identifier, predicate, parent);
        this.children = children;
    }

    public NbtItem(Identifier identifier, Identifier predicate, Identifier parent) {
        // get item from predicate
        super(((ConditionInterface) Economy2.server.getPredicateManager().get(predicate)).getStack().getLeft());
        this.identifier = identifier;
        this.predicate = predicate;
        this.parent = parent;
    }

    public static NbtItem fromStack(ItemStack stack) {
        for(ItemModule module : ItemModuleHandler.activeModules) {
            for(NbtItem current : module.getValues().values()) {
                if(current.matches(stack)) return current;
            }
        }
        return null;
    }

    @Override
    public ItemStack toItemStack() {
        // put item into stack
        ItemStack newStack = new ItemStack(item != null ? item : Items.DIRT);
        // set stack's tag
        newStack.setTag(getTag());

        return newStack;
    }

    public CompoundTag getTag() {
        return getPredicateInfo().getStack().getRight();
    }

    @Override
    public boolean matches(ItemStack stack, Type type) {
        if(softMatches(stack, type)) {
            // make sure children don't match
            for(NbtItem child : children) if(child.softMatches(stack, type)) return false;
            // if none do return yes
            return true;
        }
        return false;
    }

    public boolean softMatches(ItemStack stack, Type type) {
        return typeMatches(type) && // check if type matches
                getPredicate() // check if predicate matches
                .test(new LootContext.Builder(Economy2.server.getWorld(ServerWorld.OVERWORLD)) // have to pass in world to get server
                        .parameter(LootContextParameters.TOOL, stack) // use tool as it's the most simple
                        .build( // pass in builder
                                new LootContextType.Builder()
                                        .require(LootContextParameters.TOOL) // has to specify if parameters are used
                                        .build()
                        )
                );
    }

    public boolean typeMatches(Type type) {
        return type == Type.BOTH || type == getType();
    }

    public String toString() {
        return identifier.toString() + (parent != null ? " - parent: " + parent.toString() : "");
    }

    public NbtItem copy() {
        return new NbtItem(item, identifier, predicate, parent, children);
    }

    // so many gets
    public Identifier getIdentifier() { return identifier; }
    public Identifier getPredicateId() { return predicate; }
    public LootCondition getPredicate() { return Economy2.server.getPredicateManager().get(getPredicateId()); }
    public ConditionInterface getPredicateInfo() { return (ConditionInterface) getPredicate(); }

    public boolean hasParent() { return parent != null; }
    public Identifier getParent() { return parent; }

    public void addChild(NbtItem item) { children.add(item); }
    public List<NbtItem> getChildren() { return children; }
    public boolean hasChildren() { return !children.isEmpty(); }

    public Type getType() { return getItem() == null ? Type.MODIFIER : Type.ITEM; }
}

package mods.banana.economy2.itemmodules.items;

import mods.banana.economy2.itemmodules.ItemModule;
import mods.banana.economy2.itemmodules.ItemModuleHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NbtItem extends BaseNbtItem {
    private final ItemType type;
    private final Identifier identifier;
    private final CompoundTag tag;
    private final Identifier parent;
    private List<NbtItem> children = new ArrayList<>();

    public enum ItemType {
        ITEM,
        MODIFIER
    }

    // full constructor with parent
    public NbtItem(ItemType type, Identifier identifier, CompoundTag tag, Item item, Identifier parent) {
        super(item);
        this.type = type;
        this.identifier = identifier;
        this.tag = tag;
        this.parent = parent;
    }

    // full constructor with children
    public NbtItem(ItemType type, Identifier identifier, CompoundTag tag, @Nullable Item item, Identifier parent, List<NbtItem> children) {
        this(type, identifier, tag, item, parent);
        this.children = children;
    }

    // modifier constructor
    public NbtItem(Identifier identifier, CompoundTag tag) {
        this(ItemType.MODIFIER, identifier, tag, null, null);
    }

    // item constructor
    public NbtItem(Identifier identifier, CompoundTag tag, Item item) {
        this(ItemType.ITEM, identifier, tag, item, null);
    }

    // constructor from stack
    public NbtItem(ItemStack parentStack) {
        super(parentStack.getItem());
        this.identifier = Registry.ITEM.getId(getItem());
        this.tag = parentStack.getTag();
        this.parent = null;
        this.type = ItemType.ITEM;
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
        ItemStack newStack = new ItemStack(type == ItemType.ITEM ? getItem() : Items.DIRT);
        // set stack's tag using the brigadier string reader
        newStack.setTag(tag);

        return newStack;
    }

    @Override
    public boolean matches(ItemStack stack) {
        if(softMatches(stack)) {
            // make sure children don't match
            for(NbtItem child : children) if(child.softMatches(stack)) return false;
            // if none do return yes
            return true;
        }
        return false;
    }

    public boolean softMatches(ItemStack stack) {
        return (type == ItemType.ITEM || stack.getItem().equals(getItem())) && // check item if it's an item
                NbtHelper.matches(getTag(), stack.getTag(), true); // check tag
    }

    public String toString() {
        return identifier.toString() + (parent != null ? " - parent: " + parent.toString() : "");
    }

    public NbtItem copy() {
        return new NbtItem(type, identifier, tag, item, parent, children);
    }

    public Identifier getIdentifier() { return identifier; }
    public CompoundTag getTag() { return tag; }

    public boolean hasParent() { return parent != null; }
    public Identifier getParent() { return parent; }

    public void addChild(NbtItem item) { children.add(item); }
    public List<NbtItem> getChildren() { return children; }
    public boolean hasChildren() { return !children.isEmpty(); }

    public ItemType getType() { return type; }
}

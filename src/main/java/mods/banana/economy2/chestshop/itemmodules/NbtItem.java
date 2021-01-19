package mods.banana.economy2.chestshop.itemmodules;

import mods.banana.economy2.chestshop.ChestShopItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class NbtItem extends ChestShopItem {
    private final Identifier identifier;
    private final CompoundTag tag;
    private final Identifier parent;
    private List<NbtItem> children = new ArrayList<>();

    public NbtItem(Item item, Identifier identifier, Identifier parent, CompoundTag tag, List<NbtItem> children) {
        this(item, identifier, parent, tag);
        this.children = children;
    }

    public NbtItem(Item item, Identifier identifier, Identifier parent, CompoundTag tag) {
        super(item);
        this.identifier = identifier;
        this.tag = tag;
        this.parent = parent;
    }

    public NbtItem(Item item, Identifier identifier, CompoundTag tag) {
        super(item);
        this.identifier = identifier;
        this.tag = tag;
        this.parent = null;
    }

    public NbtItem(ItemStack parentStack) {
        super(parentStack.getItem());
        this.identifier = Registry.ITEM.getId(getItem());
        this.tag = parentStack.getTag();
        this.parent = null;
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
        ItemStack newStack = new ItemStack(getItem());
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
        return stack.getItem().equals(getItem()) && // check item
                NbtHelper.matches(getTag(), stack.getTag(), true); // check tag
    }

    public String toString() {
        return identifier.toString() + (parent != null ? " - parent:" + parent.toString() : "");
    }

    public NbtItem copy() {
        return new NbtItem(item, identifier, parent, tag, children);
    }

    public Identifier getIdentifier() { return identifier; }
    public CompoundTag getTag() { return tag; }

    public boolean hasParent() { return parent != null; }
    public Identifier getParent() { return parent; }

    public void addChild(NbtItem item) { children.add(item); }
    public List<NbtItem> getChildren() { return children; }
    public boolean hasChildren() { return !children.isEmpty(); }
}

package mods.banana.economy2.itemmodules.items;

import mods.banana.economy2.itemmodules.ItemModule;
import mods.banana.economy2.itemmodules.ItemModuleHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

public class NbtItem extends NbtMatcher {
    private final Item item;

    public NbtItem(Item item, Identifier identifier, Identifier predicate, Identifier parent) {
        super(identifier, predicate, parent);
        this.item = item;
    }

    public NbtItem(Item item, Identifier identifier, Identifier predicate, Identifier parent, List<NbtMatcher> children) {
        super(identifier, predicate, parent, children);
        this.item = item;
    }

    public static NbtItem fromStack(ItemStack stack) {
        for(ItemModule module : ItemModuleHandler.activeModules) {
            for(NbtMatcher current : module.getValues().values()) {
                if(current instanceof NbtItem && current.matches(stack, Type.ITEM)) return (NbtItem) current;
            }
        }
        return null;
    }

    @Override
    public boolean matches(ItemStack stack, Type type) {
        if(softMatches(stack, type)) {
            // make sure children don't match
            for(NbtMatcher child : getChildren()) if(child.softMatches(stack, type)) return false;
            // if none do return yes
            return true;
        }
        return false;
    }

    @Override
    public boolean softMatches(ItemStack stack, Type type) {
        return stack.getItem().equals(getItem()) && super.softMatches(stack, type);
    }

    public NbtItem copy() {
        return new NbtItem(getItem(), getIdentifier(), getPredicateId(), getParent(), getChildren());
    }

    @Override
    public Type getType() {
        return Type.ITEM;
    }

    public ItemStack toItemStack() {
        // put item into stack
        ItemStack newStack = new ItemStack(getItem());
        // set stack's tag
        newStack.setTag(getCompoundTag());

        return newStack;
    }

    public Item getItem() {
        return item;
    }
}

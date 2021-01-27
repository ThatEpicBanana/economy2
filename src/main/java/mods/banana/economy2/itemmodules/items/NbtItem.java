package mods.banana.economy2.itemmodules.items;

import mods.banana.bananaapi.helpers.PredicateHelper;
import mods.banana.economy2.itemmodules.ItemModuleHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

public class NbtItem extends NbtMatcher {
    private final Item item;

    public NbtItem(Item item, Identifier identifier, Identifier predicate, Identifier parent, Identifier accepts) {
        super(identifier, predicate, parent, accepts);
        this.item = item;
    }

    public NbtItem(Item item, Identifier identifier, Identifier predicate, Identifier parent, Identifier accepts, List<NbtMatcher> children) {
        super(identifier, predicate, parent, accepts, children);
        this.item = item;
    }

    public static NbtItem fromStack(ItemStack stack) {
        return (NbtItem) ItemModuleHandler.getMatch(stack, Type.ITEM);
    }

    @Override
    public boolean itemMatches(Item item) {
        return getItem().equals(item);
    }

    @Override
    public boolean accepts(ItemStack stack) {
        return stack.getItem().equals(getItem()) && super.accepts(stack);
    }

    public NbtItem copy() {
        return new NbtItem(getItem(), getIdentifier(), getPredicateId(), getParent(), getAcceptsId(), getChildren());
    }

    @Override
    public Type getType() {
        return Type.ITEM;
    }

    public Item getItem() {
        return item;
    }
}

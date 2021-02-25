package mods.banana.economy2.itemmodules.items;

import mods.banana.bananaapi.helpers.PredicateHelper;
import mods.banana.economy2.itemmodules.ItemModuleHandler;
import mods.banana.economy2.itemmodules.display.MatcherDisplay;
import mods.banana.economy2.itemmodules.items.accepts.MatcherAccepts;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class NbtItem extends NbtMatcher {
    private final Item item;

    public NbtItem(Item item) {
        super(Registry.ITEM.getId(item), null, null, null, null);
        this.item = item;
    }

    public NbtItem(Item item, Identifier identifier, Identifier predicate, Identifier parent, MatcherAccepts accepts, MatcherDisplay display) {
        super(identifier, predicate, parent, accepts, display);
        this.item = item;
    }

    public NbtItem(Item item, Identifier identifier, Identifier predicate, Identifier parent, MatcherAccepts accepts, MatcherDisplay display, List<NbtMatcher> children) {
        super(identifier, predicate, parent, accepts, display, children);
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
        return new NbtItem(getItem(), getIdentifier(), getPredicateId(), getParent(), getAccepts(), getDisplay(), getChildren());
    }

    @Override
    public Type getType() {
        return Type.ITEM;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public Identifier getItemId() {
        return Registry.ITEM.getId(item);
    }
}

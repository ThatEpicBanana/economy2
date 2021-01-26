package mods.banana.economy2.itemmodules.items;

import mods.banana.bananaapi.helpers.PredicateHelper;
import mods.banana.economy2.Economy2;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.util.List;

public class NbtModifier extends NbtMatcher {
    private final Identifier tag;

    public NbtModifier(Identifier tag, Identifier identifier, Identifier predicate, Identifier parent) {
        super(identifier, predicate, parent);
        this.tag = tag;
    }

    public NbtModifier(Identifier tag, Identifier identifier, Identifier predicate, Identifier parent, List<NbtMatcher> children) {
        super(identifier, predicate, parent, children);
        this.tag = tag;
    }

    public boolean softMatches(ItemStack stack, Type type) {
        return (getTag() == null || getTag().contains(stack.getItem())) && super.softMatches(stack, type); // check if tag and predicate works
    }

    public NbtModifier copy() {
        return new NbtModifier(getTagId(), getIdentifier(), getPredicateId(), getParent(), getChildren());
    }

    @Override
    public Type getType() {
        return Type.MODIFIER;
    }
    public Tag<Item> getTag() { return Economy2.server.getTagManager().getItems().getTag(getTagId()); }
    public Identifier getTagId() { return tag; }
}

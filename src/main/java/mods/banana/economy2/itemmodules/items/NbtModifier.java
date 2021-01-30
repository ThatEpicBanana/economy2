package mods.banana.economy2.itemmodules.items;

import mods.banana.economy2.Economy2;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.util.List;

public class NbtModifier extends NbtMatcher {
    private final Identifier tag;

    public NbtModifier(Identifier tag, Identifier identifier, Identifier predicate, Identifier parent, Identifier accepts) {
        super(identifier, predicate, parent, accepts);
        this.tag = tag;
    }

    public NbtModifier(Identifier tag, Identifier identifier, Identifier predicate, Identifier parent, Identifier accepts, List<NbtMatcher> children) {
        super(identifier, predicate, parent, accepts, children);
        this.tag = tag;
    }

    @Override
    public boolean itemMatches(Item item) {
        return getTag() == null || getTag().contains(item); // check tag if it's specified
    }

    @Override
    public Item getItem() {
        // TODO: add a custom item and also options in json
        return Items.STICK;
    }

    public NbtModifier copy() {
        return new NbtModifier(getTagId(), getIdentifier(), getPredicateId(), getParent(), getAcceptsId(), getChildren());
    }

    @Override
    public Type getType() {
        return Type.MODIFIER;
    }
    public Tag<Item> getTag() { return Economy2.server.getTagManager().getItems().getTag(getTagId()); }
    public Identifier getTagId() { return tag; }

    @Override
    public Identifier getItemId() { return tag; }
}

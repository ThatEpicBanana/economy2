package mods.banana.economy2.bounties.items;

import mods.banana.bananaapi.helpers.ItemStackHelper;
import mods.banana.economy2.items.GuiItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import java.util.List;

public class MatcherItem extends GuiItem {
    public final boolean activated;

    public MatcherItem(ItemConvertible parent, boolean activated, boolean display) {
        super(parent, display ? new Identifier("bounty", "matcherdisplay") : new Identifier("bounty", "matcher"));
        this.activated = activated;
        setCustomValue("activated", ByteTag.of(activated));
    }

    @Override
    public ItemStack convert(ItemStack stack) {
        setActivated(stack, activated);
        return super.convert(stack);
    }

    public void setValue(ItemStack stack, Identifier identifier) {
        setCustomValue(stack, "value", StringTag.of(identifier.toString()));
        ItemStackHelper.setLore(stack, List.of(new LiteralText(identifier.toString())));
    }

    public Identifier getValue(ItemStack stack) {
        Tag value = getCustomValue(stack, "value");
        return value != null ? new Identifier(value.asString()) : null;
    }

    public boolean isSet(ItemStack stack) {
        return getValue(stack) != null;
    }

    public void setActivated(ItemStack stack, boolean activated) {
        setCustomValue(stack, "activated", ByteTag.of(activated));
    }

    public boolean isActivated(ItemStack stack) {
        Tag activated =  getCustomValue(stack, "activated");
        return activated instanceof ByteTag && ((ByteTag) activated).getByte() > 0;
    }

    public boolean isActivated() {
        return activated;
    }
}

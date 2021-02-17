package mods.banana.economy2.bounties.items;

import mods.banana.bananaapi.helpers.ItemStackHelper;
import mods.banana.bananaapi.itemsv2.CustomItem;
import mods.banana.economy2.items.GuiItem;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class MatcherItem extends CustomItem {
    public final boolean activated;

    public MatcherItem(ItemConvertible parent, boolean activated, boolean display) {
        super(display ? new Identifier("bounty", "matcherdisplay") : new Identifier("bounty", "matcher"), parent.asItem(), null, new ArrayList<>());
        this.activated = activated;
    }

    @Override
    public ItemStack getItemStack() {
        return super.toBuilder().customValue("activated", ByteTag.of(activated)).build();
    }

    @Override
    public ItemStack convertTag(ItemStack stack) {
        setActivated(stack, activated);
        return super.convertTag(stack);
    }

    public ItemStack setValue(ItemStack stack, Identifier identifier) {
        stack = toBuilder(stack, false).customValue("value", StringTag.of(identifier.toString())).build();
        return ItemStackHelper.setLore(stack, List.of(new LiteralText(identifier.toString())));
    }

    public Identifier getValue(ItemStack stack) {
        net.minecraft.nbt.Tag value = toReader(stack).getCustomValue("value", NbtType.STRING);
        return value != null ? new Identifier(value.asString()) : null;
    }

    public boolean isSet(ItemStack stack) {
        return getValue(stack) != null;
    }

    public ItemStack setActivated(ItemStack stack, boolean activated) {
        return toBuilder(stack, false).customValue("activated", ByteTag.of(activated)).build();
    }

    public boolean isActivated(ItemStack stack) {
        net.minecraft.nbt.Tag activated =  toReader(stack).getCustomValue("activated", NbtType.BYTE);
        return activated instanceof ByteTag && ((ByteTag) activated).getByte() > 0;
    }

    public boolean isActivated() {
        return activated;
    }
}

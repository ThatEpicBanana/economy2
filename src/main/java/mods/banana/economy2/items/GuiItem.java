package mods.banana.economy2.items;

import mods.banana.bananaapi.serverItems.ServerItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GuiItem extends ServerItem {
    public GuiItem(ItemConvertible parent, Identifier identifier) {
        super(parent, identifier);
    }

    public GuiItem(ItemConvertible parent, Identifier identifier, Text text) {
        super(parent, identifier, text);
    }

    public GuiItem(ItemConvertible parent, Identifier identifier, int customModelData, boolean preventSteal, Text name) {
        super(parent, identifier, customModelData, preventSteal, name);
        CompoundTag tag = getTag();
        tag.putBoolean("guiItem", true);
        setTag(tag);
    }

    public static boolean isGuiItem(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean("guiItem");
    }
}

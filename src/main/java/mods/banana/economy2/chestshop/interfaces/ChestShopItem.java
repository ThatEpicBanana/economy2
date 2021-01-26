package mods.banana.economy2.chestshop.interfaces;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface ChestShopItem {
    boolean matches(ItemStack itemStack);

    Item getItem();
    ItemStack toItemStack();
}

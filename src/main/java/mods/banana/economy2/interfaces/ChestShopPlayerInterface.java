package mods.banana.economy2.interfaces;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface ChestShopPlayerInterface extends PlayerInterface {
    int countItem(Item item);
    void removeItemStack(ItemStack itemStack);
    void giveStack(ItemStack itemStack);
}

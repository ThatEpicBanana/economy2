package mods.banana.economy2.chestshop.interfaces;

import mods.banana.economy2.balance.PlayerInterface;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface ChestShopPlayerInterface extends PlayerInterface {
    @Deprecated
    int countItem(Item item);
    int countItemStack(ItemStack input);
    void removeItemStack(ItemStack itemStack);
    void giveStack(ItemStack itemStack);
}

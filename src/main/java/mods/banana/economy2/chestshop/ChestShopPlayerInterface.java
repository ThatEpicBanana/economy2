package mods.banana.economy2.chestshop;

import mods.banana.economy2.balance.PlayerInterface;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface ChestShopPlayerInterface extends PlayerInterface {
    int countItem(Item item);
    void removeItemStack(ItemStack itemStack);
    void giveStack(ItemStack itemStack);
}

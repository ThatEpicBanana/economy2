package mods.banana.economy2.chestshop.interfaces;

import mods.banana.economy2.balance.PlayerInterface;
import mods.banana.economy2.chestshop.ChestShopItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface ChestShopPlayerInterface extends PlayerInterface {
    @Deprecated
    int countItem(Item item);
    @Deprecated
    int countItemStack(ItemStack input);
    int countItem(ChestShopItem item);

    @Deprecated
    void removeItemStack(ItemStack itemStack);
    List<ItemStack> removeItem(ChestShopItem item, int count);

    void giveStack(ItemStack itemStack);
    void giveStacks(List<ItemStack> inputStacks);
}

package mods.banana.economy2.chestshop.interfaces.mixin;

import mods.banana.economy2.balance.PlayerInterface;
import mods.banana.economy2.chestshop.BaseItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface ChestShopPlayerInterface extends PlayerInterface {
    @Deprecated
    int countItem(Item item);
    @Deprecated
    int countItemStack(ItemStack input);
    int countItem(BaseItem item);

    @Deprecated
    void removeItemStack(ItemStack itemStack);
    List<ItemStack> removeItem(BaseItem item, int count);

    void giveStack(ItemStack itemStack);
    void giveStacks(List<ItemStack> inputStacks);
}

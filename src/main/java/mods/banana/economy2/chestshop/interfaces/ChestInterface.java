package mods.banana.economy2.chestshop.interfaces;

import mods.banana.economy2.itemmodules.items.BaseNbtItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface ChestInterface extends ChestShopPart {
//    UUID getParent();
    void setLimit(int index);

    void create(ServerPlayerEntity player, BlockPos sign);

    void insertStack(ItemStack inputStack);
    void insertStacks(List<ItemStack> stacks);

    @Deprecated
    void removeItemStack(ItemStack inputStack);
    List<ItemStack> removeItem(BaseNbtItem item, int count);

    @Deprecated
    int countItem(Item item);
    @Deprecated
    int countItemStack(ItemStack input);
    int countItem(BaseNbtItem input);

    @Deprecated
    int countSpace(Item item);
    int countSpaceForStack(ItemStack input);
//    int countSpaceForItem(ChestShopItem input);
}

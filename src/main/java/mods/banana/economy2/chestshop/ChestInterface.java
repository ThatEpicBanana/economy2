package mods.banana.economy2.chestshop;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public interface ChestInterface extends ChestShopPart {
//    UUID getParent();
    void setLimit(int index);

    void create(ServerPlayerEntity player, BlockPos sign);

    void insertItemStack(ItemStack inputStack);
    void removeItemStack(ItemStack inputStack);
    int countItem(Item item);
    int countSpace(Item item);
}

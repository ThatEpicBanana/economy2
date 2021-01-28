package mods.banana.economy2.chestshop.interfaces.mixin;

import mods.banana.economy2.itemmodules.items.NbtItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.UUID;

public interface HopperInterface {
    void setAutoSell(boolean autoSell);
    boolean isAutoSell();

    void setChestShop(BlockPos chestShop);
    BlockPos getChestShopPos();
    SignInterface getChestShop();

    UUID getParent();
    void setParent(UUID parent);

    int countItem(NbtItem input);
    List<ItemStack> removeItem(NbtItem item, int count);
}

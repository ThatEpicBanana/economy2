package mods.banana.economy2.chestshop.interfaces;

import mods.banana.economy2.chestshop.ChestShopItem;
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

    int countItem(ChestShopItem input);
    List<ItemStack> removeItem(ChestShopItem item, int count);
}

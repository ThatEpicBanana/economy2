package mods.banana.economy2.chestshop.interfaces;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public interface SignInterface extends ChestShopPart {
    long getBuy();
    long getSell();
    int getAmount();

    Item getItem();
    ItemStack getItemStack();
    ItemStack getItemStack(int count);

    void onBuy(PlayerEntity player);
    void onSell(PlayerEntity player);

    void create(ServerPlayerEntity player, BlockPos chest);
}
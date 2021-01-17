package mods.banana.economy2.mixins;

import mods.banana.economy2.chestshop.interfaces.ChestInterface;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public class HopperEntityMixin {
    @Inject(method = {"transfer(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/item/ItemStack;ILnet/minecraft/util/math/Direction;)Lnet/minecraft/item/ItemStack;"}, at = { @At("HEAD") }, cancellable = true)
    private static void onExtract(Inventory from, Inventory to, ItemStack stack, int slot, Direction direction, CallbackInfoReturnable<ItemStack> cir) {
        if(from instanceof ChestBlockEntity && ((ChestInterface)from).isChestShop()) cir.setReturnValue(stack);
    }
}

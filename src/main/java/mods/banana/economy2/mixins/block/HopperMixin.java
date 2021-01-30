package mods.banana.economy2.mixins.block;

import mods.banana.economy2.Economy2;
import mods.banana.economy2.EconomyItems;
import mods.banana.economy2.chestshop.interfaces.mixin.HopperInterface;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HopperBlock.class)
public class HopperMixin {
    @Inject(method = "onPlaced", at = @At("TAIL"))
    private void onPlace(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci) {
        if(world.isClient) return;
        if(Economy2.CONFIG.getValue("chestShop.autosellHoppers", Boolean.class) && EconomyItems.AUTOSELL.matches(itemStack) && placer instanceof ServerPlayerEntity) {
            // auto sell hopper
            HopperInterface hopper = (HopperInterface) world.getBlockEntity(pos);

            hopper.setAutoSell(true);
            hopper.setChestShop(EconomyItems.AUTOSELL.getShop(itemStack));
            hopper.setParent(placer.getUuid());

        }
    }
}

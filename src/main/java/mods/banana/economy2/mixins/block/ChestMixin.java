package mods.banana.economy2.mixins.block;

import mods.banana.economy2.chestshop.interfaces.mixin.ChestInterface;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public class ChestMixin {
    @Inject(method = "onUse", at = {@At("HEAD")}, cancellable = true)
    private void open(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        ChestInterface chest = (ChestInterface) world.getBlockEntity(pos);
        if(chest.isChestShop() && !chest.getParent().equals(player.getUuid())) cir.setReturnValue(ActionResult.FAIL);
    }
}

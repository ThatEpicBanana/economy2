package mods.banana.economy2.mixins.network;

import mods.banana.economy2.chestshop.interfaces.ChestShopPart;
import mods.banana.economy2.chestshop.interfaces.SignInterface;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerInteractionManager.class)
public class PlayerInteractionHandlerMixin {
    @Shadow public ServerWorld world;
    @Shadow public ServerPlayerEntity player;

    @Inject(method = "processBlockBreakingAction", at = {@At("HEAD")}, cancellable = true)
    private void onBreak(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, CallbackInfo ci) {
        if(world.isClient) return;
        if(action == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK) {
            if(pos != null) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if(blockEntity instanceof ChestShopPart && ((ChestShopPart) blockEntity).isChestShop()) {
                    if(((ChestShopPart) blockEntity).getParent().equals(player.getUuid())) {
                        ((ChestShopPart) blockEntity).destroy(true);
                    } else {
                        this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, this.world.getBlockState(pos), action, false, "destroyed"));
                        ci.cancel();
                    }
                }
            }
        } else if(action == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof SignBlockEntity) {
                if(((SignInterface)blockEntity).isChestShop()) ((SignInterface)blockEntity).onSell(player);
            }
        }
    }
}

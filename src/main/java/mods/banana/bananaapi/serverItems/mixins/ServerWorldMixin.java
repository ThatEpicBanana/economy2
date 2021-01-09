package mods.banana.bananaapi.serverItems.mixins;

import mods.banana.bananaapi.serverItems.ServerItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(method = "spawnEntity", at = {@At("HEAD")}, cancellable = true)
    private void onSpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if(entity instanceof ItemEntity) {
            for(ServerItem serverItem : ServerItem.items) {
                ItemStack stack = ((ItemEntity)entity).getStack();
                if(serverItem.sameIdentifierAs(stack)) {
                    if(serverItem.onItemEntitySpawn(stack)) cir.setReturnValue(false);
                }
            }
        }
    }
}

package mods.banana.economy2.mixins.client;

import mods.banana.economy2.Economy2;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {
    @Inject(method = "setupServer", at = @At("RETURN"))
    private void onLoad(CallbackInfoReturnable<Boolean> cir) {
//        Economy2.server = (MinecraftServer)this;
    }
}

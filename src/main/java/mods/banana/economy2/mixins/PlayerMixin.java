package mods.banana.economy2.mixins;

import com.mojang.authlib.GameProfile;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.interfaces.PlayerInterface;
import mods.banana.economy2.interfaces.ServerInterface;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerMixin extends EntityMixin implements PlayerInterface {
    private long bal;

    @Override
    public long getBal() { return bal; }

    @Override
    public void setBal(long value) { bal = value; save(); }

    @Override
    public void addBal(long amount) { bal += amount; save(); }

    @Override
    public String getBalString() {
        return String.valueOf(bal).replaceAll("(\\d+)", Economy2.currencyRegex);
    }

    @Override
    public void save() {
        Economy2.BalanceJson.remove(getUuidAsString());
        Economy2.BalanceJson.addProperty(getUuidAsString(), bal);
        System.out.println("Saving player " + getEntityName());
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConnect(MinecraftServer server, ServerWorld world, GameProfile profile, ServerPlayerInteractionManager interactionManager, CallbackInfo ci) {
        if(!Economy2.BalanceJson.has(getUuidAsString())) {
            Economy2.BalanceJson.addProperty(getUuidAsString(), Economy2.startingBalance);
            System.out.println("Added player " + profile.getName() + " to balance file");
        }

        bal = Economy2.BalanceJson.get(getUuidAsString()).getAsLong();
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void onDisconnect(CallbackInfo ci) { save(); }
}

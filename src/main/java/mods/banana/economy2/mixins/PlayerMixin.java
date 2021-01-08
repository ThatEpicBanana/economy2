package mods.banana.economy2.mixins;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.interfaces.PlayerInterface;
import mods.banana.economy2.interfaces.ServerInterface;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import org.lwjgl.system.CallbackI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerMixin extends EntityMixin implements PlayerInterface {
    private long bal;
    private String playerName;

    @Override
    public long getBal() { return bal; }

    @Override
    public void setBal(long value) { bal = value; save(); }

    @Override
    public void addBal(long amount) { bal += amount; save(); }

    @Override
    public String getBalString() {
        return Economy2.addCurrencySign(bal);
    }

    @Override
    public void save() {
        JsonObject player = new JsonObject();
        player.addProperty("playerName", getEntityName());
        player.addProperty("bal", bal);

        Economy2.BalanceJson.remove(getUuidAsString());
//        Economy2.BalanceJson.addProperty(getUuidAsString(), bal);
        Economy2.BalanceJson.add(getUuidAsString(), player);

        System.out.println("Saving player " + getEntityName());
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConnect(MinecraftServer server, ServerWorld world, GameProfile profile, ServerPlayerInteractionManager interactionManager, CallbackInfo ci) {
        if(!Economy2.BalanceJson.has(getUuidAsString())) {
            bal = Economy2.startingBalance;
            playerName = profile.getName();
            save();
            System.out.println("Added player " + profile.getName() + " to balance file");
        } else {
            JsonObject player = Economy2.BalanceJson.get(getUuidAsString()).getAsJsonObject();

            bal = player.get("bal").getAsLong();
            playerName = player.get("playerName").getAsString();
        }
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void onDisconnect(CallbackInfo ci) { save(); }
}

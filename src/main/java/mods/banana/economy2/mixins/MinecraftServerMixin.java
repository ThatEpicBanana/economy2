package mods.banana.economy2.mixins;

import mods.banana.economy2.Economy2;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.CallbackI;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Shadow @Final private Map<RegistryKey<World>, ServerWorld> worlds;

    @Shadow @Final private static Logger LOGGER;

    @Inject(method = { "save" }, at = { @At("HEAD") })
    private void save(boolean suppressLogs, boolean bl2, boolean bl3, CallbackInfoReturnable<Boolean> callbackInfo) {
        if(Economy2.server == null) Economy2.server = worlds.get(ServerWorld.OVERWORLD).getServer();

        // check that it is actually supposed to save by iterating through worlds
        boolean saving = false;
        for(ServerWorld world : worlds.values())
            if (!world.savingDisabled) { saving = true; break; }

        // if it's actually saving
        if(saving) {
            LOGGER.info("Saving balances...");
            try {
                //open file
                FileWriter file = new FileWriter(Economy2.CONFIG.getValue("file.saveDirectory", String.class) + "/balJson.json");

                //write json to file
                file.write(Economy2.BalanceJson.toString());

                //close file
                file.flush();
                file.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            LOGGER.info("Balance file saved");
        }
    }
}

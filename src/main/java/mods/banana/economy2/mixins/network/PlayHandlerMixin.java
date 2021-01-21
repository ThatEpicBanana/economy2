package mods.banana.economy2.mixins.network;

import mods.banana.economy2.itemmodules.ItemModuleHandler;
import mods.banana.economy2.chestshop.interfaces.ChestInterface;
import mods.banana.economy2.chestshop.interfaces.SignInterface;
import net.minecraft.block.*;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Locale;

@Mixin(ServerPlayNetworkHandler.class)
public class PlayHandlerMixin {
    @Shadow public ServerPlayerEntity player;
    @Shadow @Final private MinecraftServer server;

    @Inject(method = { "method_31282" }, at = { @At("HEAD") })
    private void createSign(UpdateSignC2SPacket updateSignC2SPacket, List<String> list, CallbackInfo callbackInfo) {
        if(validateSignBase(list, player)) {
            if(list.get(0).equals("Admin") && player.hasPermissionLevel(3)) {
                ((SignInterface) player.world.getBlockEntity(updateSignC2SPacket.getPos())).create(player, null);
            } else {
                BlockPos chest = checkSign(player.world, updateSignC2SPacket.getPos());
                if(chest != null) {
                    ChestBlockEntity chestEntity = (ChestBlockEntity) player.world.getBlockEntity(chest);
                    if(validateSign(list, player, chestEntity)) {
                        ((SignInterface) player.world.getBlockEntity(updateSignC2SPacket.getPos())).create(player, chest);
                        ((ChestInterface) player.world.getBlockEntity(chest)).create(player, updateSignC2SPacket.getPos());
                    }
                }
            }
        }
    }

    private boolean validateSign(List<String> lines, ServerPlayerEntity player, ChestBlockEntity chest) {
        if(lines.get(0).equals("")) lines.set(0, player.getEntityName());

        if(lines.get(3).equals("")) {
            for(int i = 0; i < 27; i++)
                if(!chest.getStack(i).isEmpty()) {
                    Identifier nbtStack = ItemModuleHandler.getMatch(chest.getStack(i));
                    if(nbtStack != null) {
                        lines.set(3, nbtStack.toString()); // if it's an nbt stack, set the identifier to that stack
                    } else {
                        lines.set(3, Registry.ITEM.getId(chest.getStack(i).getItem()).getPath()); // if it's not, set it to the item
                    }
                    break;
                }
        }

        return (lines.get(0).equals(player.getEntityName()) || (lines.get(0).equals("Admin") && player.hasPermissionLevel(3))) &&
                lines.get(1).matches("\\d+") &&
                (lines.get(2).toLowerCase().matches("^b (\\d+) : (\\d+) s$") || lines.get(2).toLowerCase().matches("^[bs] (\\d+)$")) &&
                validItem(lines.get(3).toLowerCase(Locale.ROOT));
    }

    private boolean validateSignBase(List<String> lines, ServerPlayerEntity player) {
        return (lines.get(0).equals(player.getEntityName()) || (lines.get(0).equals("Admin") && player.hasPermissionLevel(3)) || lines.get(0).equals("")) &&
                lines.get(1).matches("\\d+") &&
                (lines.get(2).toLowerCase().matches("^b (\\d+) : (\\d+) s$") || lines.get(2).toLowerCase().matches("^[bs] (\\d+)$")) &&
                (validItem(lines.get(3).toLowerCase(Locale.ROOT)) || lines.get(3).equals(""));
    }

    public boolean validItem(String string) {
        return(
                ItemModuleHandler.getActiveItem(new Identifier(string)) != null ||
                        Registry.ITEM.getOrEmpty(new Identifier(string)).isPresent()
                );
    }

    private @Nullable BlockPos checkSign(World world, BlockPos blockPos) {

        //check x-axis sides for chests
        for(int i = -1; i < 2; i += 2) {
            BlockState blockState = world.getBlockState(blockPos.add(i, 0, 0));
            if (blockState.isOf(Blocks.CHEST) && ChestBlock.getDoubleBlockType(blockState).equals(DoubleBlockProperties.Type.SINGLE) && !((ChestInterface) world.getBlockEntity(blockPos.add(i, 0, 0))).isChestShop())
                return blockPos.add(i, 0, 0);
        }

        //check y-axis sides for chests
        for(int i = -1; i < 2; i += 2) {
            BlockState blockState = world.getBlockState(blockPos.add(0, i, 0));
            if (blockState.isOf(Blocks.CHEST) && ChestBlock.getDoubleBlockType(blockState).equals(DoubleBlockProperties.Type.SINGLE) && !((ChestInterface) world.getBlockEntity(blockPos.add(0, i, 0))).isChestShop())
                return blockPos.add(0, i, 0);
        }

        //check z-axis sides for chests
        for(int i = -1; i < 2; i += 2) {
            BlockState blockState = world.getBlockState(blockPos.add(0, 0, i));
            if (blockState.isOf(Blocks.CHEST) && ChestBlock.getDoubleBlockType(blockState).equals(DoubleBlockProperties.Type.SINGLE) && !((ChestInterface) world.getBlockEntity(blockPos.add(0, 0, i))).isChestShop())
                return blockPos.add(0, 0, i);
        }

        return null;
    }
}

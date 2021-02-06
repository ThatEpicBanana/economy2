package mods.banana.economy2.dev.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.gui.mixin.GuiPlayer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class OpenSign {
    public static int run(ServerPlayerEntity  player) {
        ((GuiPlayer)player).openSignGui();
        return 1;
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        return CommandManager
                .literal("opensign")
                .executes(context -> run(context.getSource().getPlayer()))
                .build();
    }
}

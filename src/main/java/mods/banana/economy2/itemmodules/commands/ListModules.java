package mods.banana.economy2.itemmodules.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.gui.mixin.GuiPlayer;
import mods.banana.economy2.itemmodules.gui.ModulesScreen;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class ListModules {
    public static int run(ServerPlayerEntity player) {
        ((GuiPlayer)player).openScreen(new ModulesScreen(NbtMatcher.Type.BOTH, true));
        return 1;
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        return CommandManager
                .literal("modules")
                .executes(context -> run(context.getSource().getPlayer()))
                .build();
    }
}

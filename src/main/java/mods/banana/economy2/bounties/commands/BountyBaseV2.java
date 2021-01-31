package mods.banana.economy2.bounties.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.bounties.gui.BPage1;
import mods.banana.economy2.gui.GuiPlayer;
import mods.banana.economy2.itemmodules.gui.ModulesScreen;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class BountyBaseV2 {
    public static int run(ServerPlayerEntity player) {
//        player.openHandledScreen(new BPage1());
        ((GuiPlayer)player).openScreen(new ModulesScreen());
        return 1;
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        return CommandManager
                .literal("bounties")
                .executes(context ->
                        run(context.getSource().getPlayer())
                )
                .build();
    }
}

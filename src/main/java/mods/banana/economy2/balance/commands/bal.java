package mods.banana.economy2.balance.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.balance.OfflinePlayer;
import mods.banana.economy2.balance.PlayerInterface;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class bal {
    public static int run(ServerPlayerEntity source, ServerPlayerEntity target) {
        source.sendSystemMessage(new LiteralText(target.getName().getString() + "'s balance:").formatted(Formatting.GREEN), UUID.randomUUID());
        source.sendSystemMessage(new LiteralText(" - " + ((PlayerInterface) target).getBalAsString()), UUID.randomUUID());
        return 1;
    }

    public static int run(ServerPlayerEntity source, String target) {
        PlayerInterface playerInterface = OfflinePlayer.fromString(target);
        if(playerInterface != null) {
            source.sendSystemMessage(new LiteralText(target + "'s balance:").formatted(Formatting.GREEN), UUID.randomUUID());
            source.sendSystemMessage(new LiteralText(" - " + playerInterface.getBalAsString()), UUID.randomUUID());
            return 1;
        } else return 0;
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        return CommandManager
                .literal("bal")
                .executes(context -> run(context.getSource().getPlayer(), context.getSource().getPlayer()))
                .then(
                        CommandManager.argument("player", StringArgumentType.string())
                                .suggests(new OfflinePlayer.PlayerSuggestion())
                                .executes(context -> run(context.getSource().getPlayer(), StringArgumentType.getString(context, "player")))
                )
                .build();
    }
}

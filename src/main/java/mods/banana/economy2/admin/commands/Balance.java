package mods.banana.economy2.admin.commands;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.balance.OfflinePlayer;
import mods.banana.economy2.balance.PlayerInterface;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class Balance {
    public static LiteralCommandNode<ServerCommandSource> build() {

        LiteralCommandNode<ServerCommandSource> set = CommandManager
                .literal("set")
                .then(
                        CommandManager.argument("value", LongArgumentType.longArg())
                        .executes(context -> set(StringArgumentType.getString(context, "player"), LongArgumentType.getLong(context, "value")))
                )
                .build();

        LiteralCommandNode<ServerCommandSource> add = CommandManager
                .literal("add")
                .then(
                        CommandManager.argument("amount", LongArgumentType.longArg())
                                .executes(context -> add(StringArgumentType.getString(context, "player"), LongArgumentType.getLong(context, "amount")))
                )
                .build();

        LiteralCommandNode<ServerCommandSource> reset = CommandManager
                .literal("reset")
                .executes(context -> reset(StringArgumentType.getString(context, "player")))
                .build();

        RequiredArgumentBuilder<ServerCommandSource, String> player = CommandManager.argument("player", StringArgumentType.string());

        player.then(set);
        player.then(add);
        player.then(reset);

        LiteralCommandNode<ServerCommandSource> base = CommandManager
                .literal("balance")
                .then(player)
                .build();

        return base;
    }

    public static int set(String player, long value) {
        PlayerInterface playerInterface = OfflinePlayer.fromString(player);
        if(playerInterface != null) {
            playerInterface.setBal(value);
            return 1;
        } else return 0;
    }

    public static int add(String player, long amount) {
        PlayerInterface playerInterface = OfflinePlayer.fromString(player);
        if(playerInterface != null) {
            playerInterface.addBal(amount);
            return 1;
        } else return 0;
    }

    public static int reset(String player) {
        PlayerInterface playerInterface = OfflinePlayer.fromString(player);
        if(playerInterface != null) {
            playerInterface.reset(player);
            return 1;
        } else return 0;
    }
}

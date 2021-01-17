package mods.banana.economy2.trade.commands;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.trade.TradeHandler;
import mods.banana.economy2.trade.TradeInstance;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class Request {
    public static int run(ServerPlayerEntity source, ServerPlayerEntity target) {
        if(source.equals(target)) return 0;
        TradeInstance tradeInstance = new TradeInstance(source, target);
        tradeInstance.sendRequestMessage();
        TradeHandler.requests.add(tradeInstance);
        return 1;
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        return CommandManager
                .literal("request")
                .then(
                        CommandManager.argument("player", EntityArgumentType.player())
                                .executes(commandContext -> run(commandContext.getSource().getPlayer(), EntityArgumentType.getPlayer(commandContext, "player")))
                )
                .build();
    }

    public static RequiredArgumentBuilder<ServerCommandSource, EntitySelector> buildAsArgument() {
        return CommandManager.argument("player", EntityArgumentType.player())
                .executes(commandContext -> run(commandContext.getSource().getPlayer(), EntityArgumentType.getPlayer(commandContext, "player")));
    }
}
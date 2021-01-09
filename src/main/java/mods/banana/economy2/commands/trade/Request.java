package mods.banana.economy2.commands.trade;

import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.Economy2;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class Request {
    public static int run(ServerPlayerEntity source, ServerPlayerEntity target) {
        TradeInstance tradeInstance = new TradeInstance(source, target, Economy2.tradeHandler);
        tradeInstance.sendRequestMessage();
        Economy2.tradeHandler.requests.add(tradeInstance);
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
}
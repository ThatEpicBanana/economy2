package mods.banana.economy2.commands.trade;

import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.interfaces.PlayerInterface;
import mods.banana.economy2.interfaces.TradePlayerInterface;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class Accept {
    public static int run(ServerPlayerEntity source, ServerPlayerEntity target) {
        for(TradeInstance tradeInstance : Economy2.tradeHandler.requests) {
            if(tradeInstance.getSource().equals(target) && tradeInstance.getTarget().equals(source)) {
                ((TradePlayerInterface)source).setTrade(tradeInstance);
                ((TradePlayerInterface)target).setTrade(tradeInstance);
                tradeInstance.sendUpdateMessage(true);
                Economy2.tradeHandler.trades.add(tradeInstance);
                Economy2.tradeHandler.requests.remove(tradeInstance);
                return 1;
            }
        }
        return 0;
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        return CommandManager
                .literal("accept")
                .then(
                        CommandManager.argument("player", EntityArgumentType.player())
                                .executes(commandContext -> run(commandContext.getSource().getPlayer(), EntityArgumentType.getPlayer(commandContext, "player")))
                )
                .build();
    }
}
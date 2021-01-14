package mods.banana.economy2.trade.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.trade.TradePlayerInterface;
import mods.banana.economy2.trade.TradeInstance;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class Confirm {
    public static int run(ServerPlayerEntity source) {
        // get source interface and trade
        TradePlayerInterface sourceInterface = (TradePlayerInterface) source;
        TradeInstance trade = sourceInterface.getTrade();

        if(trade != null && trade.timer >= TradeInstance.confirmTime) {
            // get target and target interface
            ServerPlayerEntity target;
            if(trade.getSource().equals(source)) target = trade.getTarget(); else target = trade.getSource();
            TradePlayerInterface targetInterface = (TradePlayerInterface) target;

            // mark source as accepted
            sourceInterface.setAccepted(true);

            source.sendSystemMessage(new LiteralText("Trade confirmed!").fillStyle(Style.EMPTY.withFormatting(Formatting.GREEN)), UUID.randomUUID());
            target.sendSystemMessage(new LiteralText(source.getName().getString() + " has confirmed!").formatted(Formatting.GREEN), UUID.randomUUID());

            if(sourceInterface.getAccepted() && targetInterface.getAccepted()) {
                trade.complete();
            }
        }

        return 1;
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        return CommandManager
                .literal("confirm")
                .executes(commandContext -> run(commandContext.getSource().getPlayer()))
                .build();
    }
}
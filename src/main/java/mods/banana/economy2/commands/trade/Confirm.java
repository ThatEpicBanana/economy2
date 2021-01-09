package mods.banana.economy2.commands.trade;

import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.interfaces.PlayerInterface;
import net.minecraft.command.argument.EntityArgumentType;
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
        PlayerInterface sourceInterface = (PlayerInterface) source;
        TradeInstance trade = sourceInterface.getTrade();

        if(trade != null && trade.timer >= TradeInstance.confirmTime) {
            // get target and target interface
            ServerPlayerEntity target;
            if(trade.getSource().equals(source)) target = trade.getTarget(); else target = trade.getSource();
            PlayerInterface targetInterface = (PlayerInterface) target;

            // mark source as accepted
            sourceInterface.setAccepted(true);

            source.sendSystemMessage(new LiteralText("Trade confirmed!").fillStyle(Style.EMPTY.withFormatting(Formatting.GREEN)), UUID.randomUUID());

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
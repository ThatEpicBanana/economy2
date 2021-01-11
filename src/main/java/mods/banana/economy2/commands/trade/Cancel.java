package mods.banana.economy2.commands.trade;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.interfaces.PlayerInterface;
import mods.banana.economy2.interfaces.TradePlayerInterface;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class Cancel {
    public static int run(ServerPlayerEntity target) throws CommandSyntaxException {
        TradeInstance trade = ((TradePlayerInterface)target).getTrade();
        if(trade != null) {
            trade.complete(false);
            trade.getTarget().sendSystemMessage(new LiteralText("Trade cancelled...").formatted(Formatting.RED), UUID.randomUUID());
            trade.getSource().sendSystemMessage(new LiteralText("Trade cancelled...").formatted(Formatting.RED), UUID.randomUUID());
            return 1;
        } else throw new CommandSyntaxException(new SimpleCommandExceptionType(new LiteralText("You are not in a trade!")), new LiteralText("You are not in a trade!"));
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        return CommandManager
                .literal("cancel")
                .executes(context -> run(context.getSource().getPlayer()))
                .build();
    }
}

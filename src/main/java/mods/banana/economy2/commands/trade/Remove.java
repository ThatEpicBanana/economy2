package mods.banana.economy2.commands.trade;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.BuiltInExceptions;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.interfaces.PlayerInterface;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.util.UUID;

public class Remove {
    public static int run(ServerPlayerEntity target, int index) throws CommandSyntaxException {
        PlayerInterface playerInterface = ((PlayerInterface)target);
        TradeInstance trade = playerInterface.getTrade();
        if(trade != null) {
            if(index <= playerInterface.getTradeItems().size()) {
                target.giveItemStack(playerInterface.getTradeItems().get(index));
                playerInterface.getTradeItems().remove(index);
                trade.sendUpdateMessage(true);
                return 1;
            }else throw new CommandSyntaxException(new SimpleCommandExceptionType(new LiteralText("Index out of bounds!")), new LiteralText("Index out of bounds!"));
        } else throw new CommandSyntaxException(new SimpleCommandExceptionType(new LiteralText("You are not in a trade!")), new LiteralText("You are not in a trade!"));
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        return CommandManager
                .literal("remove")
                .then(
                        CommandManager.argument("index", IntegerArgumentType.integer(0))
                                .executes(commandContext -> run(commandContext.getSource().getPlayer(), IntegerArgumentType.getInteger(commandContext, "index")))
                )
                .build();
    }
}

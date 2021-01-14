package mods.banana.economy2.balance.commands;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.balance.PlayerInterface;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class exchange {
    public static int run(ServerPlayerEntity source, ServerPlayerEntity target, long amount) {
        PlayerInterface sourceInterface = (PlayerInterface) source;
        PlayerInterface targetInterface = (PlayerInterface) target;

        if(sourceInterface.getBal() > amount) {
            sourceInterface.addBal(-amount);
            targetInterface.addBal(amount);
            return 1;
        } else source.sendSystemMessage(new LiteralText("You do not have enough " + Economy2.CONFIG.getValue("currency.name", String.class) + " for that action!").formatted(Formatting.RED), UUID.randomUUID());
        return 0;
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        return CommandManager
                .literal("exchange")
                .then(
                        CommandManager.argument("player", EntityArgumentType.player())
                                .then(
                                        CommandManager.argument("amount", LongArgumentType.longArg(0))
                                                .executes(commandContext -> run(commandContext.getSource().getPlayer(), EntityArgumentType.getPlayer(commandContext, "player"), LongArgumentType.getLong(commandContext, "amount")))
                                )
                )
                .build();
    }
}

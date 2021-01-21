package mods.banana.economy2.bounties.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.itemmodules.ItemModuleHandler;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class BountyBase {
    public static int request(ServerPlayerEntity player, Identifier identifier, int amount) {
        return 1;
    }

    public static int list(ServerPlayerEntity player) {
        return 1;
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        LiteralCommandNode<ServerCommandSource> requestNode = CommandManager
                .literal("request")
                .then(
                        CommandManager.argument("identifier", IdentifierArgumentType.identifier()).suggests(new ItemModuleHandler.ItemModuleSuggestionProvider())
                        .then(
                                CommandManager.argument("amount", IntegerArgumentType.integer(0))
                                .executes(context -> request(context.getSource().getPlayer(), IdentifierArgumentType.getIdentifier(context, "identifier"), IntegerArgumentType.getInteger(context, "amount")))
                        )
                )
                .build();

        LiteralCommandNode<ServerCommandSource> listNode = CommandManager
                .literal("list")
                .executes(context -> list(context.getSource().getPlayer()))
                .build();

        LiteralCommandNode<ServerCommandSource> baseNode = CommandManager
                .literal("bounty")
                .build();

        baseNode.addChild(requestNode);
        baseNode.addChild(listNode);

        return baseNode;
    }
}

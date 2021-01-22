package mods.banana.economy2.bounties.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.itemmodules.ItemModuleHandler;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class BountyBase {
    public static int request(ServerPlayerEntity player, List<Identifier> identifiers, int amount) {
        // have to add an amount to sell for
        for(Identifier identifier : identifiers) System.out.println(identifier);
        return 1;
    }

    public static int list(ServerPlayerEntity player) {
        return 1;
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        LiteralCommandNode<ServerCommandSource> requestNode = CommandManager
                .literal("request")
                .then(
                        getIdentifierArgument(List.of("identifier1"))
                                .then(
                                        getIdentifierArgument(List.of("identifier1", "identifier2"))
                                                .then(
                                                        getIdentifierArgument(List.of("identifier1", "identifier2", "identifier3"))
                                                )
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

    private static RequiredArgumentBuilder<ServerCommandSource, Identifier> getIdentifierArgument(List<String> arguments) {
        return CommandManager
                .argument(arguments.get(arguments.size() - 1), IdentifierArgumentType.identifier()).suggests(new ItemModuleHandler.ItemModuleSuggestionProvider())
                .then(
                        CommandManager.argument("amount", IntegerArgumentType.integer(0))
                                .executes(context -> request(
                                        context.getSource().getPlayer(),
                                        getIdentifiers(context, arguments),
                                        IntegerArgumentType.getInteger(context, "amount"))
                                )
                );
    }

    private static List<Identifier> getIdentifiers(CommandContext<ServerCommandSource> context, List<String> arguments) {
        ArrayList<Identifier> identifiers = new ArrayList<>();
        for(String argument : arguments) identifiers.add(IdentifierArgumentType.getIdentifier(context, argument));
        return identifiers;
    }
}

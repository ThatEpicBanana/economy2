package mods.banana.economy2.bounties.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.bounties.Bounty;
import mods.banana.economy2.bounties.BountyHandler;
import mods.banana.economy2.bounties.gui.BountyList;
import mods.banana.economy2.itemmodules.ItemModuleHandler;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class BountyBase {
    public static int request(ServerPlayerEntity player, List<Identifier> identifiers, int amount, long price) {
        ArrayList<Identifier> mustMatch = new ArrayList<>();
        ArrayList<Identifier> cannotMatch = new ArrayList<>();

        for(int i = 1; i < identifiers.size(); i++) {
            Identifier identifier = identifiers.get(i);
            if(identifier.getNamespace().startsWith("-")) cannotMatch.add(identifier);
            else mustMatch.add(identifier);
        }

        BountyHandler.add(new Bounty(player.getUuid(), identifiers.get(0), mustMatch, cannotMatch, amount, price));

        return 1;
    }

    public static int list(ServerPlayerEntity player) {
        player.openHandledScreen(new BountyList());

        return 1;
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        //bounty request 1 dirt 1000
        LiteralCommandNode<ServerCommandSource> requestNode = CommandManager
                .literal("request")
                .then(
                        CommandManager.argument("price", LongArgumentType.longArg(0))
                                .then(
                                        CommandManager.argument("amount", IntegerArgumentType.integer(0))
                                                .then(
                                                        getIdentifierArguments(List.of("baseItem", "i2", "i3", "i4", "i5"))
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

    private static RequiredArgumentBuilder<ServerCommandSource, Identifier> getIdentifierArguments(List<String> arguments) {
        RequiredArgumentBuilder<ServerCommandSource, Identifier> currentArg = null;

        for(int i = arguments.size() - 1; i >= 0; i--) {
            String argument = arguments.get(i);
            if(currentArg == null) {
                currentArg = getIdentifierArgument(List.of(argument), i == 0);
            } else {
                RequiredArgumentBuilder<ServerCommandSource, Identifier> newArg = getIdentifierArgument(arguments.subList(0, i + 1), i == 0);
                newArg.then(currentArg);
                currentArg = newArg;
            }
        }

        return currentArg;
    }

    private static RequiredArgumentBuilder<ServerCommandSource, Identifier> getIdentifierArgument(List<String> arguments, boolean first) {
        return CommandManager
                .argument(arguments.get(arguments.size() - 1), IdentifierArgumentType.identifier())
                .suggests(first ? new ItemModuleHandler.ItemModuleSuggestionProvider() : new ItemModuleHandler.ItemModuleSuggestionProvider(true, NbtMatcher.Type.BOTH))
                            .executes(context -> request(
                                    context.getSource().getPlayer(),
                                    getIdentifiers(context, arguments),
                                    IntegerArgumentType.getInteger(context, "amount"),
                                    LongArgumentType.getLong(context, "price")
                            )
            );
    }

    private static List<Identifier> getIdentifiers(CommandContext<ServerCommandSource> context, List<String> arguments) {
        ArrayList<Identifier> identifiers = new ArrayList<>();
        for(String argument : arguments) identifiers.add(IdentifierArgumentType.getIdentifier(context, argument));
        return identifiers;
    }
}

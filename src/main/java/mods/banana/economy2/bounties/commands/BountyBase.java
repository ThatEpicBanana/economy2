package mods.banana.economy2.bounties.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.bounties.Bounty;
import mods.banana.economy2.bounties.BountyHandler;
import mods.banana.economy2.bounties.gui.BountyList;
import mods.banana.economy2.itemmodules.ItemModuleHandler;
import mods.banana.economy2.itemmodules.items.NbtItem;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class BountyBase {
    private static final DynamicCommandExceptionType NBT_MATCHER_NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(matcher -> new LiteralText(matcher + " was not found in database."));
    private static final Dynamic2CommandExceptionType ITEMS_DO_NOT_MATCH_EXCEPTION = new Dynamic2CommandExceptionType((matcher1, matcher2) -> new LiteralText(matcher1 + " does not match with " + matcher2 + "'s item."));
    private static final Dynamic2CommandExceptionType MATCHER_DOES_NOT_ACCEPT_EXCEPTION = new Dynamic2CommandExceptionType((matcher1, matcher2) -> new LiteralText(matcher1 +  " can not combine with " + matcher2 +  "."));

    public static int request(ServerPlayerEntity player, Identifier baseItemId, List<Identifier> identifiers, int amount, long price) throws CommandSyntaxException {
        // initialize arrays
        ArrayList<NbtMatcher> mustMatch = new ArrayList<>();
        ArrayList<NbtMatcher> cannotMatch = new ArrayList<>();

        // get base item from item modules
        NbtItem baseItem = (NbtItem) ItemModuleHandler.getActiveMatcher(baseItemId, NbtMatcher.Type.ITEM);
        // if item modules do not contain the item check normal minecraft items
        if(baseItem == null) baseItem = new NbtItem(Registry.ITEM.getOrEmpty(baseItemId).orElse(null));
        // if it's still not found, throw
        if(baseItem.getItem() == null) throw NBT_MATCHER_NOT_FOUND_EXCEPTION.create(baseItemId);

        // for each identifier
        for(Identifier identifier : identifiers) {
            // get matcher
            NbtMatcher matcher =  ItemModuleHandler.getActiveMatcher(removeNegation(identifier), NbtMatcher.Type.MODIFIER);

            // make sure matcher exists
            if(matcher == null) throw NBT_MATCHER_NOT_FOUND_EXCEPTION.create(identifier);

            // add matcher to either list
            if(identifier.getNamespace().startsWith("-")) cannotMatch.add(matcher);
            else mustMatch.add(matcher);
        }

        // TODO: check if cannot matchers conflict with must matchers
        for(NbtMatcher i : mustMatch) {
            if(!i.itemMatches(baseItem.getItem())) throw ITEMS_DO_NOT_MATCH_EXCEPTION.create(i, baseItemId);
            for(NbtMatcher j : mustMatch) {
                if(!i.accepts(j, baseItem.getItem())) throw MATCHER_DOES_NOT_ACCEPT_EXCEPTION.create(i, j);
            }
        }

        // add bounty to bounty handler
        BountyHandler.add(new Bounty(player.getUuid(), baseItem, mustMatch, cannotMatch, amount, price));

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

    private static Identifier removeNegation(Identifier identifier) {
        return new Identifier(identifier.getNamespace().replace("-", ""), identifier.getPath());
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
                            .executes(context -> {
                                List<Identifier> identifiers = getIdentifiers(context, arguments);
                                return request(
                                    context.getSource().getPlayer(),
                                    identifiers.get(0),
                                    identifiers.subList(1, identifiers.size()),
                                    IntegerArgumentType.getInteger(context, "amount"),
                                    LongArgumentType.getLong(context, "price")
                                );
                            }
            );
    }

    private static List<Identifier> getIdentifiers(CommandContext<ServerCommandSource> context, List<String> arguments) {
        ArrayList<Identifier> identifiers = new ArrayList<>();
        for(String argument : arguments) identifiers.add(IdentifierArgumentType.getIdentifier(context, argument));
        return identifiers;
    }
}

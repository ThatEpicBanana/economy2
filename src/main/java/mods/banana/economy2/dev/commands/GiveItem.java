package mods.banana.economy2.dev.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.itemmodules.ItemModuleHandler;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class GiveItem {
    private static int run(ServerPlayerEntity player, Identifier identifier) {
        player.giveItemStack(ItemModuleHandler.getActiveMatcher(identifier).toItemStack());
        return 1;
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        return CommandManager
                .literal("giveItem")
                .then(
                        CommandManager.argument("identifier", IdentifierArgumentType.identifier())
                                .suggests(new ItemModuleHandler.ItemModuleSuggestionProvider(false, NbtMatcher.Type.BOTH))
                                .executes(context -> run(context.getSource().getPlayer(), IdentifierArgumentType.getIdentifier(context, "identifier")))
                )
                .build();
    }
}

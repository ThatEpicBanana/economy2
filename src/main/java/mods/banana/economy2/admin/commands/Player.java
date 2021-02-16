package mods.banana.economy2.admin.commands;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.balance.OfflinePlayer;
import mods.banana.economy2.balance.PlayerInterface;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class Player {
    public static int remove(ServerPlayerEntity playerEntity) {
        Economy2.BalanceJson.remove(playerEntity.getGameProfile().getId().toString());
        ((PlayerInterface)playerEntity).reset(null);
        return 1;
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        LiteralCommandNode<ServerCommandSource> remove = CommandManager
                .literal("remove")
                        .executes(context -> remove(EntityArgumentType.getPlayer(context, "player")))
                .build();

        RequiredArgumentBuilder<ServerCommandSource, EntitySelector> player = CommandManager.argument("player", EntityArgumentType.player());

        player.then(remove);

        return CommandManager.literal("player").then(player).build();
    }
}

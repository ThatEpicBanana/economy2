package mods.banana.economy2.commands.trade;

import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.interfaces.PlayerInterface;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.util.UUID;

public class Root {
    public static int run(ServerPlayerEntity target) {
        target.sendSystemMessage(new LiteralText(((PlayerInterface) target).getBalString()), UUID.randomUUID());
        return 1;
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        LiteralCommandNode<ServerCommandSource> root = CommandManager.literal("trade").build();

        root.addChild(Request.build());
        root.addChild(Accept.build());
        root.addChild(Confirm.build());
        root.addChild(Cancel.build());
        root.addChild(Remove.build());

        return root;
    }
}
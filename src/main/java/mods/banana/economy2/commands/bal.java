package mods.banana.economy2.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.interfaces.PlayerInterface;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.util.UUID;

public class bal {
    public static int run(ServerPlayerEntity target) {
        target.sendSystemMessage(new LiteralText(((PlayerInterface) target).getBalString()), UUID.randomUUID());
        return 1;
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        return CommandManager
                .literal("bal")
                .executes(context -> run(context.getSource().getPlayer()))
                .build();
    }
}

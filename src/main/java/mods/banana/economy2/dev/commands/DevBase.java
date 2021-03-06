package mods.banana.economy2.dev.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class DevBase {
    public static LiteralCommandNode<ServerCommandSource> build() {
        LiteralCommandNode<ServerCommandSource> base = CommandManager
                .literal("dev")
                .requires(source -> source.hasPermissionLevel(4))
                .build();

        base.addChild(OpenSign.build());
        base.addChild(GiveItem.build());

        return base;
    }
}

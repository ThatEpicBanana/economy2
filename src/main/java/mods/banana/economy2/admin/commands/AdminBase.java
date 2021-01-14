package mods.banana.economy2.admin.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class AdminBase {
    public static LiteralCommandNode<ServerCommandSource> build() {
        LiteralCommandNode<ServerCommandSource> base = CommandManager
                .literal("admin")
                .build();

        base.addChild(Balance.build());
        base.addChild(Player.build());

        return base;
    }
}

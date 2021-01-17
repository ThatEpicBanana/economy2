package mods.banana.economy2.admin.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.Economy2;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class AdminBase {
    public static int clean(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ArrayList<String> toRemove = new ArrayList<>();

        for(Map.Entry<String, JsonElement> player : Economy2.BalanceJson.entrySet()) {
            if(player.getValue().getAsJsonObject().get("bal").getAsLong() == Economy2.CONFIG.getValue("player.startingBalance", Integer.class).longValue()) {
                toRemove.add(player.getKey());
            }
        }

        for(String string : toRemove) Economy2.BalanceJson.remove(string);

        context.getSource().getPlayer().sendSystemMessage(new LiteralText("Cleaned balances"), UUID.randomUUID());

        return 1;
    }

    public static int removeAll(int i, ServerPlayerEntity player) {
        if(i == 12345) {
            Economy2.BalanceJson = new JsonObject();
            player.sendSystemMessage(new LiteralText("Cleared balance Json."), UUID.randomUUID());
            return 1;
        } else return 0;
    }

    public static int removeAll(ServerPlayerEntity player) {
        player.sendSystemMessage(new LiteralText("Are you sure about this? Click here to confirm.").formatted(Formatting.RED).fillStyle(Style.EMPTY
                .withClickEvent(
                        new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/admin removeAll 12345"
                        )
                )
        ), UUID.randomUUID());
        return 0;
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        LiteralCommandNode<ServerCommandSource> base = CommandManager
                .literal("admin")
                .requires(source -> source.hasPermissionLevel(3)) // admin
                .build();

        LiteralCommandNode<ServerCommandSource> clean = CommandManager
                .literal("clean")
                .executes(AdminBase::clean)
                .build();

        LiteralCommandNode<ServerCommandSource> removeAll = CommandManager
                .literal("removeAll")
                .requires(source -> source.hasPermissionLevel(4)) // server owner
                .executes(context -> removeAll(context.getSource().getPlayer()))
                .then(
                        CommandManager.argument("number", IntegerArgumentType.integer())
                        .executes(context -> removeAll(IntegerArgumentType.getInteger(context, "number"), context.getSource().getPlayer()))
                )
                .build();

        base.addChild(Balance.build());
        base.addChild(Player.build());
        base.addChild(clean);
        base.addChild(removeAll);

        return base;
    }
}

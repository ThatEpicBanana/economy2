package mods.banana.economy2.chestshop.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.datafixers.kinds.IdF;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class HelpCommand {
    public static int help(ServerPlayerEntity player) {
        MutableText m = new LiteralText("");

        m.append(t("                  - Chestshops -                   \n").formatted(Formatting.GREEN));
        m.append(t("Chestshops are blocks that allow you to sell items \n"));
        m.append(t("to other players with ease. A chest shop is created\n"));
        m.append(t("when a player places a sign on a chest in this     \n"));
        m.append(t("pattern:                                         \n\n"));

        m.append(t("                   PlayerName                      \n").formatted(Formatting.YELLOW));
        m.append(t("                        64                         \n").formatted(Formatting.YELLOW));
        m.append(t("                   B 10 : 10 S                     \n").formatted(Formatting.YELLOW));
        m.append(t("                     diamond                     \n\n").formatted(Formatting.YELLOW));

        m.append(new LiteralText(" - ").formatted(Formatting.YELLOW));
        m.append(t(   "The first line designates the player name - if  \n"));
        m.append(t("you don't specify it, it automatically gets filled.\n"));
        m.append(new LiteralText(" - ").formatted(Formatting.YELLOW));
        m.append(t(   "The second line designates the amount bought or \n"));
        m.append(t("sold at a time.                                    \n"));
        m.append(new LiteralText(" - ").formatted(Formatting.YELLOW));
        m.append(t(   "The third line designates the buy and sell      \n"));
        m.append(t("values. You can designate a shop as buy or sell only\n"));
        m.append(t("by omitting either side, ex: B 20, S 20            \n"));
        m.append(new LiteralText(" - ").formatted(Formatting.YELLOW));
        m.append(t(   "The fourth line designates the item sold. If you\n"));
        m.append(t("have an item already in the chest, it automatically\n"));
        m.append(t("generates the id from it. You can also sell various\n"));
        m.append(t("other items such as enchantments or potions - get  \n"));
        m.append(t("these values by using the /aboutitem command.      \n"));

        player.sendSystemMessage(m, UUID.randomUUID());

        return 1;
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        return CommandManager
                .literal("chestshop")
                .then(
                        CommandManager.literal("help")
                        .executes(context -> help(context.getSource().getPlayer()))
                )
                .build();
    }

    private static MutableText t(String text) {
        return new LiteralText(text).formatted(Formatting.RESET);
    }
}

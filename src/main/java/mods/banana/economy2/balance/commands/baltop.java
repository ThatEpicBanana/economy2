package mods.banana.economy2.balance.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.Economy2;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

public class baltop {
    public static int run(ServerPlayerEntity target) {
        List<JsonElement> players = new ArrayList<>();

        for (Map.Entry<String, JsonElement> element : Economy2.BalanceJson.entrySet() ) {
            players.add(element.getValue());
        }

        players.sort(new PlayerComparator());

        target.sendSystemMessage(new LiteralText("        - Baltop -").formatted(Formatting.YELLOW), UUID.randomUUID());

        for(int i = 0; i < Math.min(10, players.size()); i++) {
            JsonArray messageJson = new JsonArray();
            //add text (Example: 1. 100000¥ - Player)
            messageJson.add(Text.Serializer.toJsonTree(new LiteralText((i + 1) + ((i == 9) ? ". " : ".  ")).formatted(Formatting.GRAY)));
            messageJson.add(Text.Serializer.toJsonTree(new LiteralText(Economy2.addCurrencySign(players.get(i).getAsJsonObject().get("bal").getAsLong())).formatted(Formatting.GREEN)));
            messageJson.add(Text.Serializer.toJsonTree(new LiteralText(" - ").formatted(Formatting.DARK_GRAY)));
            messageJson.add(Text.Serializer.toJsonTree(new LiteralText(players.get(i).getAsJsonObject().get("playerName").getAsString()).formatted(Formatting.GRAY)));
            //serialize
            Text message = Text.Serializer.fromJson(messageJson);

            target.sendSystemMessage(message, UUID.randomUUID());
        }

        return 1;
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        return CommandManager
                .literal("baltop")
                .executes(context -> run(context.getSource().getPlayer()))
                .build();
    }

    public static class PlayerComparator implements Comparator<JsonElement> {
        @Override
        public int compare(JsonElement o1, JsonElement o2) {
            return (int)(o2.getAsJsonObject().get("bal").getAsLong() - o1.getAsJsonObject().get("bal").getAsLong());
        }
    }
}

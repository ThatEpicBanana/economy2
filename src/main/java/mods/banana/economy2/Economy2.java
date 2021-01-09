package mods.banana.economy2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.bananaapi.serverItems.SimpleItem;
import mods.banana.economy2.commands.bal;
import mods.banana.economy2.commands.baltop;
import mods.banana.economy2.commands.banknote;
import mods.banana.economy2.commands.exchange;
import mods.banana.economy2.commands.trade.Root;
import mods.banana.economy2.commands.trade.TradeHandler;
import mods.banana.economy2.items.EconomyItems;
import mods.banana.economy2.mixins.MinecraftServerMixin;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.world.WorldTickCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;

import java.io.*;

public class Economy2 implements ModInitializer {
    public static JsonObject BalanceJson;
    public static String balFileName = "economy/balJson.json";

    public static long startingBalance = 10000;

    public static String currencyRegex = "$1¥";
    public static String currencyName = "yen";

    public static MinecraftServer server = null;
    public static TradeHandler tradeHandler = new TradeHandler();

    @Override
    public void onInitialize() {
        // setup balance file
        File balFile = new File(balFileName);
        try {
            //create directory
            File directory = new File("economy");
            if(directory.mkdir()) {
                System.out.println("Directory created");
            } else {
                System.out.println("Directory already exists");
            }

            // add file
            if (balFile.createNewFile()) {
                System.out.println("File created: " + balFile.getName());
            } else {
                System.out.println("File already exists.");
            }

            //check if balFile is empty
            if(balFile.length() == 0) {
                //write basic json
                FileWriter writer = new FileWriter(balFile);
                writer.write("{}");
                writer.close();
            }

            JsonParser parser = new JsonParser();
            BalanceJson = parser.parse(new BufferedReader(new FileReader(balFile))).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // setup commands

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.getRoot().addChild(bal.build());
            dispatcher.getRoot().addChild(exchange.build());
            dispatcher.getRoot().addChild(baltop.build());
            dispatcher.getRoot().addChild(banknote.build());
            dispatcher.getRoot().addChild(Root.build());
        });

        tradeHandler.onLoad();
        // setup items
        EconomyItems.onInit();
    }

    public static String addCurrencySign(long amount) {
        return (amount + "").replaceAll("(\\d+)", currencyRegex);
    }
}

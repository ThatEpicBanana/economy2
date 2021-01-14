package mods.banana.economy2;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.oroarmor.config.Config;
import com.oroarmor.config.command.ConfigCommand;
import mods.banana.economy2.balance.commands.bal;
import mods.banana.economy2.balance.commands.baltop;
import mods.banana.economy2.balance.commands.exchange;
import mods.banana.economy2.banknote.commands.banknote;
import mods.banana.economy2.admin.commands.AdminBase;
import mods.banana.economy2.trade.commands.TradeBase;
import mods.banana.economy2.trade.TradeHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class Economy2 implements ModInitializer {
    public static JsonObject BalanceJson;
    public static String balFileName = "economy/balJson.json";

    public static final Config CONFIG = new EconomyConfig();
    public static final Logger LOGGER = LogManager.getLogger();

    public static MinecraftServer server = null;
    public static TradeHandler tradeHandler = new TradeHandler();

    @Override
    public void onInitialize() {
        CONFIG.readConfigFromFile();
        CONFIG.saveConfigToFile();

        // setup balance file
        File balFile = new File(balFileName);
        try {
            //create directory
            File directory = new File("economy");
            if(directory.mkdir()) {
                LOGGER.info("economy directory created");
            }

            // add file
            if (balFile.createNewFile()) {
                LOGGER.info("Balance file created: " + balFile.getName());
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
            dispatcher.getRoot().addChild(TradeBase.build());
            dispatcher.getRoot().addChild(AdminBase.build());
//            dispatcher.getRoot().addChild(OpenGui.build());
        });

        CommandRegistrationCallback.EVENT.register(new ConfigCommand(CONFIG));

        ServerLifecycleEvents.SERVER_STARTED.register(server1 -> {
            server = server1;
        });

        tradeHandler.onLoad();
        // setup items
        EconomyItems.onInit();
    }

    public static String addCurrencySign(long amount) {
        return (amount + "").replaceAll("(\\d+)", CONFIG.getValue("currency.regex", String.class));
    }
}

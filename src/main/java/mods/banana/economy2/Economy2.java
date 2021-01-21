package mods.banana.economy2;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.oroarmor.config.Config;
import com.oroarmor.config.ConfigItem;
import com.oroarmor.config.command.ConfigCommand;
import mods.banana.economy2.balance.commands.bal;
import mods.banana.economy2.balance.commands.baltop;
import mods.banana.economy2.balance.commands.exchange;
import mods.banana.economy2.banknote.commands.banknote;
import mods.banana.economy2.admin.commands.AdminBase;
import mods.banana.economy2.bounties.commands.BountyBase;
import mods.banana.economy2.chestshop.commands.AboutItem;
import mods.banana.economy2.itemmodules.ItemModules;
import mods.banana.economy2.trade.commands.TradeBase;
import mods.banana.economy2.trade.TradeHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Economy2 implements ModInitializer {
    public static JsonObject BalanceJson;
    private static boolean initializing;

    public static final Config CONFIG = new EconomyConfig();
    public static final Logger LOGGER = LogManager.getLogger();

    public static String previousSaveDirectory;

    public static MinecraftServer server = null;

    @Override
    public void onInitialize() {
        initializing = true;

        // load config
        CONFIG.readConfigFromFile();
        CONFIG.saveConfigToFile();

        // save previous directory for reference when moving it
        previousSaveDirectory = CONFIG.getValue("file.saveDirectory", String.class);

        ServerLifecycleEvents.SERVER_STARTED.register(server1 -> server = server1);

        loadBalJson();
        registerCommands();
        TradeHandler.onInit();
        EconomyItems.onInit();
//        CreatePotions.onInit();
        ItemModules.onInit();

        initializing = false;
    }

    public static void loadBalJson() {
        // setup balance file
        Path path = Paths.get(CONFIG.getValue("file.saveDirectory", String.class));
        try {
            //create directory
            Files.createDirectories(path);

            File balFile = new File(path.toString() + "/balJson.json");

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
    }

    public static void registerCommands() {
        // setup commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.getRoot().addChild(bal.build()); //bal <player>(optional)
            dispatcher.getRoot().addChild(exchange.build()); //exchange <player> <amount>
            dispatcher.getRoot().addChild(baltop.build()); //baltop
            dispatcher.getRoot().addChild(banknote.build()); //banknote <amount>
            dispatcher.getRoot().addChild(TradeBase.build()); //trade <player>
            dispatcher.getRoot().addChild(AdminBase.build()); //admin [clean|removeALl|balance|player]
            dispatcher.getRoot().addChild(AboutItem.build()); //aboutitem
            dispatcher.getRoot().addChild(BountyBase.build()); //bounty [request|list]
        });

        // setup config command
        CommandRegistrationCallback.EVENT.register(new ConfigCommand(CONFIG));
    }

    public static String addCurrencySign(long amount) {
        return (amount + "").replaceAll("(\\d+)", CONFIG.getValue("currency.regex", String.class));
    }

    public static void onSaveFileChange(ConfigItem<String> item) {
        if(initializing) return;

        if(item.getValue().contains(".")) {
            // set value recalls this function, running the lower code
            item.setValue(item.getValue().replace('.', '/'));
        } else {
            System.out.println(item.getValue());

            try {
                LOGGER.info("Moving balance Json...");

                Path newPath = Paths.get(item.getValue());
                Path oldPath = Paths.get(previousSaveDirectory);

                Files.createDirectories(newPath);
                Files.createDirectories(oldPath);

                Files.copy(Paths.get(oldPath.toString(), "/balJson.json"), Paths.get(newPath.toString(), "/balJson.json"));

                Files.delete(Paths.get(oldPath.toString(), "/balJson.json"));

                previousSaveDirectory = newPath.toString();

                LOGGER.info("Moved balance Json");
            } catch (IOException e) { e.printStackTrace(); }
        }
    }
}

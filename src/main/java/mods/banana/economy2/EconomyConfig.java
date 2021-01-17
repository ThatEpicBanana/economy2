package mods.banana.economy2;

import com.oroarmor.config.Config;
import com.oroarmor.config.ConfigItem;
import com.oroarmor.config.ConfigItemGroup;
import mods.banana.economy2.chestshop.ItemModules;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class EconomyConfig extends Config {
    public static final ConfigItemGroup currencyGroup = new CurrencySettings();
    public static final ConfigItemGroup playerGroup = new PlayerSettings();
    public static final ConfigItemGroup chestShopGroup = new ChestShopSettings();
    public static final ConfigItemGroup fileSettings = new FileSettings();

    public static final List<ConfigItemGroup> configs = List.of(currencyGroup, playerGroup, chestShopGroup, fileSettings);

    public EconomyConfig() {
        super(configs, new File(FabricLoader.getInstance().getConfigDir().toFile(), "economy.json"), "economy");
    }

    public static class CurrencySettings extends ConfigItemGroup {
        public static final ConfigItem<String> regexItem = new ConfigItem<>("regex", "$1¥", "Handles how balances are shown, \"$1\" represents the number. ex: \"$1¥\" \"$$1\"");
        public static final ConfigItem<String> nameItem = new ConfigItem<>("name", "yen", "The currency's name. ex: \"yen\" \"dollars\" ");

        public CurrencySettings() {
            super(List.of(regexItem, nameItem), "currency");
        }
    }

    public static class PlayerSettings extends ConfigItemGroup {
        public static final ConfigItem<Integer> startingBalanceItem = new ConfigItem<>("startingBalance", 10000, "Player's default balance.");

        public PlayerSettings() {
            super(List.of(startingBalanceItem), "player");
        }
    }

    public static class ChestShopSettings extends ConfigItemGroup {
        public static final ConfigItem<Boolean> enabledItem = new ConfigItem<>("enabled", true, "Setting to enable/disable chest shops in their entirety");
        public static final ConfigItem<Boolean> adminShopsEnabledItem = new ConfigItem<>("adminshops", true, "Setting to enable/disable admin shops");

        public ChestShopSettings() {
            super(List.of(enabledItem, adminShopsEnabledItem, new NbtItemModuleSettings()), "chestShop");
        }

        public static class NbtItemModuleSettings extends ConfigItemGroup {
            public static final ConfigItem<Boolean> heads = new ConfigItem<>("heads", false, "Adds support for custom heads from VanillaTweaks in chest shops", ItemModules::onChange);

            public NbtItemModuleSettings() { super(List.of(heads), "nbtItemModule"); }
        }
    }

    public static class FileSettings extends ConfigItemGroup {
        public static final ConfigItem<String> saveFileDirectory = new ConfigItem<>("saveDirectory", "economy", "Determines where the balance json is saved", Economy2::onSaveFileChange);

        public FileSettings() {
            super(List.of(saveFileDirectory), "file");
        }
    }
}

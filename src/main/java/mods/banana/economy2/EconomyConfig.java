package mods.banana.economy2;

import com.oroarmor.config.Config;
import com.oroarmor.config.ConfigItem;
import com.oroarmor.config.ConfigItemGroup;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.util.List;

public class EconomyConfig extends Config {
    public static final ConfigItemGroup currencyGroup = new CurrencySettings();
    public static final ConfigItemGroup playerGroup = new PlayerSettings();
    public static final ConfigItemGroup chestShopGroup = new ChestShopSettings();

    public static final List<ConfigItemGroup> configs = List.of(currencyGroup, playerGroup, chestShopGroup);

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
            super(List.of(enabledItem, adminShopsEnabledItem), "chestShop");
        }
    }
}

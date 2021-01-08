package mods.banana.economy2.items;

import mods.banana.bananaapi.serverItems.ServerItem;
import net.minecraft.util.Identifier;

public class EconomyItems {
    public static ServerItem BANKNOTE = new BanknoteItem(new Identifier("economy", "banknote"));
    public static void onInit() {
        ServerItem.items.add(BANKNOTE);
    }
}

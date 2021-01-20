package mods.banana.economy2;

import mods.banana.bananaapi.serverItems.ServerItem;
import mods.banana.bananaapi.serverItems.SimpleItem;
import mods.banana.economy2.banknote.items.BanknoteItem;
import mods.banana.economy2.chestshop.items.AutoSellItem;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class EconomyItems {
    public static BanknoteItem BANKNOTE = new BanknoteItem(new Identifier("economy", "banknote"));
    public static ServerItem LIMIT = new SimpleItem(Items.NETHER_STAR, new Identifier("economy", "limit")).withCustomModelData(1).withName(new LiteralText("Limit")).setDropPrevention(true).setStealPrevention(true);
    public static ServerItem LIMITED = new SimpleItem(Items.GRAY_STAINED_GLASS_PANE, new Identifier("economy", "limited")).withCustomModelData(1).withName(new LiteralText("")).setDropPrevention(true).setStealPrevention(true);
    public static AutoSellItem AUTOSELL = new AutoSellItem(new Identifier("economy", "autosell"));

    public static void onInit() {
        ServerItem.items.add(BANKNOTE);
        ServerItem.items.add(LIMIT);
        ServerItem.items.add(LIMITED);
    }
}

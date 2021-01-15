package mods.banana.economy2;

import mods.banana.bananaapi.serverItems.ServerItem;
import mods.banana.bananaapi.serverItems.SimpleItem;
import mods.banana.economy2.banknote.items.BanknoteItem;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class EconomyItems {
    public static ServerItem BANKNOTE = new BanknoteItem(new Identifier("economy", "banknote"));
    public static ServerItem LIMIT = new SimpleItem(Items.NETHER_STAR, new Identifier("economy", "limit")).withCustomModelData(1).withName(new LiteralText("Limit")).setDropPrevention(true).setStealPrevention(true);
    public static ServerItem LIMITED = new SimpleItem(Items.GRAY_STAINED_GLASS_PANE, new Identifier("economy", "limited")).withCustomModelData(1).withName(new LiteralText("")).setDropPrevention(true).setStealPrevention(true);
//    public static ServerItem BLANK = new SimpleItem(Items.GRAY_STAINED_GLASS_PANE, new Identifier("economy", "blank")).withCustomModelData(1).withName(new LiteralText("")).setDropPrevention(true).setStealPrevention(true);
//    public static ServerItem TRADE_REQUEST = new SimpleItem(Items.NETHER_STAR, new Identifier("economy", "traderequest")).withName(new LiteralText("Request trade")).setDropPrevention(true).setStealPrevention(true);

    public static void onInit() {
        ServerItem.items.add(BANKNOTE);
        ServerItem.items.add(LIMIT);
        ServerItem.items.add(LIMITED);
//        ServerItem.items.add(BLANK);
//        ServerItem.items.add(TRADE_REQUEST);
    }
}

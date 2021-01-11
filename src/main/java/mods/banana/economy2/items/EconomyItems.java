package mods.banana.economy2.items;

import mods.banana.bananaapi.serverItems.ServerItem;
import mods.banana.bananaapi.serverItems.SimpleItem;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class EconomyItems {
    public static ServerItem BANKNOTE = new BanknoteItem(new Identifier("economy", "banknote"));
    public static ServerItem LIMIT = new SimpleItem(Items.NETHER_STAR, new Identifier("economy", "limit")).withCustomModelData(1).withName(new LiteralText("Limit")).setDropPrevention(true);
    public static ServerItem LIMITED = new SimpleItem(Items.GRAY_STAINED_GLASS_PANE, new Identifier("economy", "limited")).withCustomModelData(1).withName(new LiteralText("")).setDropPrevention(true);

    public static void onInit() {
        ServerItem.items.add(BANKNOTE);
        ServerItem.items.add(LIMIT);
        ServerItem.items.add(LIMITED);
    }
}

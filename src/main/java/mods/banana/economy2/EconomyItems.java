package mods.banana.economy2;

import mods.banana.bananaapi.serverItems.ServerItem;
import mods.banana.bananaapi.serverItems.ServerItemHandler;
import mods.banana.economy2.banknote.items.BanknoteItem;
import mods.banana.economy2.chestshop.items.AutoSellItem;
import mods.banana.economy2.items.GuiItem;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class EconomyItems {
    public static BanknoteItem BANKNOTE = new BanknoteItem(new Identifier("economy", "banknote"));

    public static ServerItem LIMIT = new GuiItem(Items.NETHER_STAR, new Identifier("economy", "limit"), new LiteralText("Limit"));
    public static ServerItem LIMITED = new GuiItem(Items.GRAY_STAINED_GLASS_PANE, new Identifier("economy", "limited"), new LiteralText(""));

    public static AutoSellItem AUTOSELL = new AutoSellItem(new Identifier("economy", "autosell"));

    public static ServerItem EMPTY = new GuiItem(Items.GRAY_STAINED_GLASS_PANE, new Identifier("gui", "empty"), new LiteralText(""));
    public static ServerItem NEXT = new GuiItem(Items.ARROW, new Identifier("gui", "next"), new LiteralText("Next"));
    public static ServerItem PREVIOUS = new GuiItem(Items.ARROW, new Identifier("gui", "previous"), new LiteralText("Previous"));
    public static ServerItem RETURN = new GuiItem(Items.ARROW, new Identifier("gui", "exit"), new LiteralText("Return"));
    public static ServerItem SEARCH = new GuiItem(Items.OAK_SIGN, new Identifier("gui", "search"), new LiteralText("Search"));
    
    public static ServerItem VIEW_ALL = new GuiItem(Items.GOLD_BLOCK, new Identifier("bounty", "view_all"), new LiteralText("View all"));
    public static ServerItem VIEW_SELF = new GuiItem(Items.GOLDEN_CARROT, new Identifier("bounty", "view_self"), new LiteralText("View your bounties"));

    public static ServerItem MOD_SELECTED = new GuiItem(Items.GRAY_STAINED_GLASS_PANE, new Identifier("module", "selected"), 2, true, new LiteralText(""));
    public static ServerItem MOD_UNSELECTED = new GuiItem(Items.GRAY_STAINED_GLASS_PANE, new Identifier("module", "unselected"), 3, true, new LiteralText(""));
    public static ServerItem MOD_EMPTY_TOP = new GuiItem(Items.GRAY_STAINED_GLASS_PANE, new Identifier("module", "emptyt"), 4, true, new LiteralText(""));
    public static ServerItem MOD_EMPTY_MID = new GuiItem(Items.GRAY_STAINED_GLASS_PANE, new Identifier("module", "emptym"), 5, true, new LiteralText(""));
    public static ServerItem MOD_EMPTY_BOT = new GuiItem(Items.GRAY_STAINED_GLASS_PANE, new Identifier("module", "emptyb"), 6, true, new LiteralText(""));

    public static ServerItem PROTECTED_ITEM = new GuiItem(null, new Identifier("gui", "protected"), 0, true, null);

    public static ServerItem MATCHER_ITEM = new GuiItem(null, new Identifier("module", "matcher"), null);

    static {
        ServerItemHandler.register(BANKNOTE);

        ServerItemHandler.register(LIMIT);
        ServerItemHandler.register(LIMITED);

        ServerItemHandler.register(AUTOSELL);

        ServerItemHandler.register(EMPTY);
        ServerItemHandler.register(NEXT);
        ServerItemHandler.register(PREVIOUS);
        ServerItemHandler.register(RETURN);
        ServerItemHandler.register(SEARCH);

        ServerItemHandler.register(VIEW_ALL);
        ServerItemHandler.register(VIEW_SELF);

        ServerItemHandler.register(MOD_SELECTED);
        ServerItemHandler.register(MOD_UNSELECTED);

        ServerItemHandler.register(MOD_EMPTY_TOP);
        ServerItemHandler.register(MOD_EMPTY_MID);
        ServerItemHandler.register(MOD_EMPTY_BOT);

        ServerItemHandler.register(PROTECTED_ITEM);

        ServerItemHandler.register(MATCHER_ITEM);
    }
}

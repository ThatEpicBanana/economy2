package mods.banana.economy2;

import mods.banana.bananaapi.serverItems.ServerItem;
import mods.banana.bananaapi.serverItems.ServerItemHandler;
import mods.banana.economy2.banknote.items.BanknoteItem;
import mods.banana.economy2.bounties.items.MatcherItem;
import mods.banana.economy2.chestshop.items.AutoSellItem;
import mods.banana.economy2.items.GuiItem;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class EconomyItems {
    public static class Gui {
        public static ServerItem EMPTY = new GuiItem(Items.GRAY_STAINED_GLASS_PANE, new Identifier("gui", "empty"), new LiteralText(""));

        public static ServerItem NEXT = new GuiItem(Items.ARROW, new Identifier("gui", "next"), 1, true, new LiteralText("Next"));
        public static ServerItem PREVIOUS = new GuiItem(Items.ARROW, new Identifier("gui", "previous"), 2, true, new LiteralText("Previous"));
        public static ServerItem SEARCH = new GuiItem(Items.OAK_SIGN, new Identifier("gui", "search"), 1, true, new LiteralText("Search"));

        public static ServerItem RETURN = new GuiItem(Items.ARROW, new Identifier("gui", "return"), 3, true, new LiteralText("Return"));
        public static ServerItem EXIT = new GuiItem(Items.BARRIER, new Identifier("gui", "exit"), 1, true, new LiteralText("Exit"));

        public static ServerItem CONFIRM = new GuiItem(Items.BAMBOO, new Identifier("gui", "confirm"), new LiteralText("Confirm"));
    }

    public static class ChestShop {
        public static ServerItem LIMIT = new GuiItem(Items.NETHER_STAR, new Identifier("economy", "limit"), new LiteralText("Limit"));
        public static ServerItem LIMITED = new GuiItem(Items.GRAY_STAINED_GLASS_PANE, new Identifier("economy", "limited"), new LiteralText(""));
        public static AutoSellItem AUTOSELL = new AutoSellItem(new Identifier("economy", "autosell"));
    }

    public static class ModulesScreen {
        public static ServerItem SELECTED = new GuiItem(Items.GRAY_STAINED_GLASS_PANE, new Identifier("module", "selected"), 2, true, new LiteralText(""));
        public static ServerItem UNSELECTED = new GuiItem(Items.GRAY_STAINED_GLASS_PANE, new Identifier("module", "unselected"), 3, true, new LiteralText(""));

        public static ServerItem EMPTY_TOP = new GuiItem(Items.GRAY_STAINED_GLASS_PANE, new Identifier("module", "emptyt"), 4, true, new LiteralText(""));
        public static ServerItem EMPTY_MID = new GuiItem(Items.GRAY_STAINED_GLASS_PANE, new Identifier("module", "emptym"), 5, true, new LiteralText(""));
        public static ServerItem EMPTY_BOT = new GuiItem(Items.GRAY_STAINED_GLASS_PANE, new Identifier("module", "emptyb"), 6, true, new LiteralText(""));

        public static ServerItem MATCHER = new GuiItem(null, new Identifier("module", "matcher"), 0, true, null);
        public static ServerItem MODIFIER = new GuiItem(null, new Identifier("module", "matcher"), null);
    }

    public static class Bounties {
        public static ServerItem VIEW_ALL = new GuiItem(Items.GOLD_BLOCK, new Identifier("bounty", "view_all"), new LiteralText("View bounties"));
        public static ServerItem VIEW_SELF = new GuiItem(Items.GOLDEN_CARROT, new Identifier("bounty", "view_self"), new LiteralText("Manage bounties"));

        public static ServerItem ADD_BOUNTY = new GuiItem(Items.GOLDEN_HORSE_ARMOR, new Identifier("bounty", "add"), new LiteralText("Request bounty"));

        public static ServerItem SET_ITEM = new GuiItem(Items.STONE_BUTTON, new Identifier("bounty", "set-item"), new LiteralText("Set item"));
        public static ServerItem ALLOW_CUSTOM_TAGS = new GuiItem(Items.STICK, new Identifier("bounty", "allowtags"), 0, true, new LiteralText("Set custom data"));

        public static MatcherItem UNSET_MATCHER = new MatcherItem(Items.BLACK_STAINED_GLASS_PANE, true, true);
        public static MatcherItem REQUIRED_MATCHER = new MatcherItem(Items.LIME_STAINED_GLASS_PANE, true, true);
        public static MatcherItem DENIED_MATCHER = new MatcherItem(Items.RED_STAINED_GLASS_PANE, false, true);

        public static MatcherItem UNSET_MATCHER_DISPLAY = new MatcherItem(Items.BLACK_STAINED_GLASS_PANE, true, false);
        public static MatcherItem REQUIRED_MATCHER_DISPLAY = new MatcherItem(Items.LIME_STAINED_GLASS_PANE, true, false);
        public static MatcherItem DENIED_MATCHER_DISPLAY = new MatcherItem(Items.RED_STAINED_GLASS_PANE, false, false);

        public static ServerItem AMOUNT = new GuiItem(Items.PAPER, new Identifier("bounty", "amount"), 2, true, new LiteralText("Amount"));
        public static ServerItem PRICE = new GuiItem(Items.PAPER, new Identifier("bounty", "price"), 3, true, new LiteralText("Price"));
    }

    public static class Banknote {
        public static BanknoteItem BANKNOTE = new BanknoteItem(new Identifier("economy", "banknote"));
    }

    public static ServerItem PROTECTED_ITEM = new GuiItem(null, new Identifier("gui", "protected"), 0, true, null);

    static {
        ServerItemHandler.register(Banknote.BANKNOTE);

        ServerItemHandler.register(ChestShop.LIMIT);
        ServerItemHandler.register(ChestShop.LIMITED);

        ServerItemHandler.register(ChestShop.AUTOSELL);

        ServerItemHandler.register(Gui.EMPTY);
        ServerItemHandler.register(Gui.NEXT);
        ServerItemHandler.register(Gui.PREVIOUS);
        ServerItemHandler.register(Gui.RETURN);
        ServerItemHandler.register(Gui.SEARCH);
        ServerItemHandler.register(Gui.EXIT);
        ServerItemHandler.register(Gui.CONFIRM);

        ServerItemHandler.register(Bounties.VIEW_ALL);
        ServerItemHandler.register(Bounties.VIEW_SELF);
        ServerItemHandler.register(Bounties.SET_ITEM);
        ServerItemHandler.register(Bounties.ALLOW_CUSTOM_TAGS);
        ServerItemHandler.register(Bounties.UNSET_MATCHER);
        ServerItemHandler.register(Bounties.REQUIRED_MATCHER);
        ServerItemHandler.register(Bounties.DENIED_MATCHER);
        ServerItemHandler.register(Bounties.UNSET_MATCHER_DISPLAY);
        ServerItemHandler.register(Bounties.REQUIRED_MATCHER_DISPLAY);
        ServerItemHandler.register(Bounties.DENIED_MATCHER_DISPLAY);

        ServerItemHandler.register(ModulesScreen.SELECTED);
        ServerItemHandler.register(ModulesScreen.UNSELECTED);

        ServerItemHandler.register(ModulesScreen.EMPTY_TOP);
        ServerItemHandler.register(ModulesScreen.EMPTY_MID);
        ServerItemHandler.register(ModulesScreen.EMPTY_BOT);

        ServerItemHandler.register(PROTECTED_ITEM);

        ServerItemHandler.register(ModulesScreen.MATCHER);
    }
}

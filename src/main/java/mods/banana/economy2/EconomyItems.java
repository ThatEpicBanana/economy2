package mods.banana.economy2;

import mods.banana.bananaapi.itemsv2.CustomItem;
import mods.banana.bananaapi.itemsv2.ItemHandler;
import mods.banana.bananaapi.serverItems.ServerItem;
import mods.banana.bananaapi.serverItems.ServerItemHandler;
import mods.banana.economy2.banknote.items.BanknoteItem;
import mods.banana.economy2.bounties.items.BountyItem;
import mods.banana.economy2.bounties.items.MatcherItem;
import mods.banana.economy2.chestshop.items.AutoSellItem;
import mods.banana.economy2.items.GuiItem;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class EconomyItems {
    public static class Gui {
        public static CustomItem EMPTY = new CustomItem.Builder().item(Items.GRAY_STAINED_GLASS_PANE).id("gui", "empty").name("").build();

        public static CustomItem NEXT = new CustomItem.Builder().item(Items.ARROW).id("gui", "next").name("Next").build();
        public static CustomItem PREVIOUS = new CustomItem.Builder().item(Items.ARROW).id("gui", "previous").name("Previous").customModelData(2).build();
        public static CustomItem SEARCH = new CustomItem.Builder().item(Items.OAK_SIGN).id("gui", "search").name("Search").build();

        public static CustomItem RETURN = new CustomItem.Builder().item(Items.ARROW).id("gui", "return").name("Return").customModelData(3).build();
        public static CustomItem EXIT = new CustomItem.Builder().item(Items.BARRIER).id("gui", "exit").name("Exit").build();

        public static CustomItem CONFIRM = new CustomItem.Builder().item(Items.BAMBOO).id("gui", "confirm").name("Confirm").build();
    }

    public static class ChestShop {
        public static CustomItem LIMIT = new CustomItem.Builder().item(Items.NETHER_STAR).id("economy", "limit").name("Limit").build();
        public static CustomItem LIMITED = new CustomItem.Builder().item(Items.GRAY_STAINED_GLASS_PANE).id("economy", "limited").name("").build();

        public static AutoSellItem AUTOSELL = new AutoSellItem(new Identifier("economy", "autosell"));
    }

    public static class ModulesScreen {
        public static CustomItem SELECTED = new CustomItem.Builder().item(Items.GRAY_STAINED_GLASS_PANE).id("module", "selected").name("").customModelData(2).build();
        public static CustomItem UNSELECTED = new CustomItem.Builder().item(Items.GRAY_STAINED_GLASS_PANE).id("module", "unselected").name("").customModelData(3).build();

        public static CustomItem EMPTY_TOP = new CustomItem.Builder().item(Items.GRAY_STAINED_GLASS_PANE).id("module", "emptyt").customModelData(4).name("").build();
        public static CustomItem EMPTY_MID = new CustomItem.Builder().item(Items.GRAY_STAINED_GLASS_PANE).id("module", "emptym").customModelData(5).name("").build();
        public static CustomItem EMPTY_BOT = new CustomItem.Builder().item(Items.GRAY_STAINED_GLASS_PANE).id("module", "emptyb").customModelData(6).name("").build();

        public static CustomItem MATCHER = new CustomItem.Builder().id("module", "matcher").customModelData(0).build();
        public static CustomItem MODIFIER = new CustomItem.Builder().id("module", "matcher").build();
    }

    public static class Bounties {
        public static class Create {
            public static CustomItem SET_ITEM = new CustomItem.Builder().item(Items.STONE_BUTTON).id("bounty", "set-item").name("Set item").build();
            public static CustomItem ALLOW_CUSTOM_TAGS = new CustomItem.Builder().item(Items.STICK).id("bounty", "allow-tags").name("Set custom data").build();

            public static MatcherItem UNSET_MATCHER = new MatcherItem(Items.BLACK_STAINED_GLASS_PANE, true, false);
            public static MatcherItem REQUIRED_MATCHER = new MatcherItem(Items.LIME_STAINED_GLASS_PANE, true, false);
            public static MatcherItem DENIED_MATCHER = new MatcherItem(Items.RED_STAINED_GLASS_PANE, false, false);

            public static MatcherItem UNSET_MATCHER_DISPLAY = new MatcherItem(Items.BLACK_STAINED_GLASS_PANE, true, true);
            public static MatcherItem REQUIRED_MATCHER_DISPLAY = new MatcherItem(Items.LIME_STAINED_GLASS_PANE, true, true);
            public static MatcherItem DENIED_MATCHER_DISPLAY = new MatcherItem(Items.RED_STAINED_GLASS_PANE, false, true);
        }

        public static class BaseScreen {
            public static CustomItem VIEW_ALL = new CustomItem.Builder().item(Items.GOLD_BLOCK).id("bounty", "view_all").name("View bounties").build();
            public static CustomItem VIEW_SELF = new CustomItem.Builder().item(Items.GOLDEN_CARROT).id("bounty", "view_self").name("Manage bounties").build();
        }

        public static class Edit {
            public static CustomItem DELETE = new CustomItem.Builder().item(Items.BARRIER).id("bounty", "delete").customModelData(2).name(new LiteralText("Delete").formatted(Formatting.RED)).build();
        }

        public static class View {
            public static CustomItem REDEEM = new CustomItem.Builder().item(Items.GOLD_NUGGET).id("bounty", "redeem").name("Redeem").build();
            public static CustomItem CANNOT_MATCH = new CustomItem.Builder().item(Items.BARRIER).id("bounty", "cannot_match").name("Uncombinable").build();
        }

        public static CustomItem ADD_BOUNTY = new CustomItem.Builder().item(Items.GOLDEN_HORSE_ARMOR).id("bounty", "add").name("Request bounty").build();

        public static CustomItem AMOUNT = new CustomItem.Builder().item(Items.PAPER).id("bounty", "amount").name("Amount").build();
        public static CustomItem PRICE = new CustomItem.Builder().item(Items.PAPER).id("bounty", "price").name("Price").build();

        public static CustomItem BOUNTY = new CustomItem.Builder().id("bounty", "bounty").build();
    }

    public static class Banknote {
        public static BanknoteItem BANKNOTE = new BanknoteItem(new Identifier("economy", "banknote"));
    }

    public static CustomItem PROTECTED_ITEM = new CustomItem.Builder().id("gui", "protected").customModelData(0).build();

    static {
        ItemHandler.register(Banknote.BANKNOTE);

        ItemHandler.register(ChestShop.LIMIT);
        ItemHandler.register(ChestShop.LIMITED);

        ItemHandler.register(ChestShop.AUTOSELL);

        ItemHandler.register(Gui.EMPTY);
        ItemHandler.register(Gui.NEXT);
        ItemHandler.register(Gui.PREVIOUS);
        ItemHandler.register(Gui.RETURN);
        ItemHandler.register(Gui.SEARCH);
        ItemHandler.register(Gui.EXIT);
        ItemHandler.register(Gui.CONFIRM);

        ItemHandler.register(Bounties.BaseScreen.VIEW_ALL);
        ItemHandler.register(Bounties.BaseScreen.VIEW_SELF);
        ItemHandler.register(Bounties.Create.SET_ITEM);
        ItemHandler.register(Bounties.Create.ALLOW_CUSTOM_TAGS);
        ItemHandler.register(Bounties.Create.UNSET_MATCHER);
        ItemHandler.register(Bounties.Create.REQUIRED_MATCHER);
        ItemHandler.register(Bounties.Create.DENIED_MATCHER);
        ItemHandler.register(Bounties.Create.UNSET_MATCHER_DISPLAY);
        ItemHandler.register(Bounties.Create.REQUIRED_MATCHER_DISPLAY);
        ItemHandler.register(Bounties.Create.DENIED_MATCHER_DISPLAY);
        ItemHandler.register(Bounties.Edit.DELETE);
        ItemHandler.register(Bounties.BOUNTY);

        ItemHandler.register(ModulesScreen.SELECTED);
        ItemHandler.register(ModulesScreen.UNSELECTED);

        ItemHandler.register(ModulesScreen.EMPTY_TOP);
        ItemHandler.register(ModulesScreen.EMPTY_MID);
        ItemHandler.register(ModulesScreen.EMPTY_BOT);

        ItemHandler.register(PROTECTED_ITEM);

        ItemHandler.register(ModulesScreen.MATCHER);
    }
}

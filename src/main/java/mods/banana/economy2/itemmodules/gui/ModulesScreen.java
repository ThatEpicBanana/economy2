package mods.banana.economy2.itemmodules.gui;

import com.ibm.icu.text.UTF16;
import mods.banana.bananaapi.helpers.ItemStackHelper;
import mods.banana.economy2.EconomyItems;
import mods.banana.economy2.gui.*;
import mods.banana.economy2.itemmodules.ItemModule;
import mods.banana.economy2.itemmodules.ItemModuleHandler;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import mods.banana.economy2.items.GuiItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringTag;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ModulesScreen extends ListGui {
    private int tab = 0;
    private List<NbtMatcher> values;
    private final List<ItemModule> itemModules;

    private Identifier returnValue = null;

    private static final int startingRow = 2;
    private static final int sizeInPage = 7 * 5;

    public ModulesScreen() {
        this(0, new PlayerInventory(null), false);
    }

    public ModulesScreen(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, true);
    }

    private ModulesScreen(int syncId, PlayerInventory playerInventory, boolean updateState) {
        super(syncId, playerInventory, ScreenHandlerType.GENERIC_9X6, 6);

        itemModules = new ArrayList<>(ItemModuleHandler.activeModules);

        updateModule();
        if(updateState) updateState();
    }

    @Override
    public GuiReturnValue<?> getReturnValue() {
        return returnValue != null ? new GuiReturnValue<>(returnValue, this) : null;
    }

    private void updateTabs() {
        // set column background
        for(int tab = 0; tab < getRows() * 9; tab += 9) {
            setStackInSlot(tab, ItemStack.EMPTY);
            if(tab == 0) setStackInSlot(tab + 1, EconomyItems.ModulesScreen.EMPTY_TOP.getItemStack());
            else if(tab == 9 * (getRows() - 1)) setStackInSlot(tab + 1, EconomyItems.ModulesScreen.EMPTY_BOT.getItemStack());
            else setStackInSlot(tab + 1, EconomyItems.ModulesScreen.EMPTY_MID.getItemStack());
        }

        // set the selected tab to the selected tab item
        setStackInSlot(9 * startingRow + 1, EconomyItems.ModulesScreen.SELECTED.getItemStack());

        // for each tab, set it's tab item and stack
        for(int tab = -Math.min(this.tab, startingRow); tab < Math.min(itemModules.size() - this.tab, 6 - startingRow); tab++) {
            // get row
            int row = (tab + startingRow) * 9;
            // get module
            ItemModule module = itemModules.get(tab + this.tab);
            // if tab isn't the currently selected tab, set the tab to unselected
            if(tab != 0) setStackInSlot(row + 1, EconomyItems.ModulesScreen.UNSELECTED.getItemStack());
            // set the stack
            setStackInSlot(row, EconomyItems.PROTECTED_ITEM.convert(module.getItemStack()));
        }
    }

    private void updateItems() {
        for(int i = 0; i < sizeInPage; i++) {
            int adjusted = i + ((Math.floorDiv(i, 7) + 1) * 2); // adjust for the two slots taken up by the left
            int index = i + sizeInPage * getPage(); // get the index

            if(index < values.size()) {
                NbtMatcher item = values.get(index);

                ItemStack stack = ItemStackHelper.setLore( // set lore of item
                        item.toItemStack(), // get item from matcher
                        List.of(new LiteralText(item.getIdentifier().toString())) // set lore to identifier
                );

                if(item.typeMatches(NbtMatcher.Type.MODIFIER)) EconomyItems.ModulesScreen.MODIFIER.convert(stack);
                else EconomyItems.ModulesScreen.MATCHER.convert(stack);

                EconomyItems.ModulesScreen.MATCHER.setCustomValue(stack, "id", StringTag.of(item.getIdentifier().toString()));

                setStackInSlot(adjusted, stack);
            } else {
                setStackInSlot(adjusted, ItemStack.EMPTY); // clear stack
            }
        }
    }

    private void updateNavi() {
        if(getPage() != 0) setStackInSlot(5 * 9 + 2, EconomyItems.Gui.PREVIOUS.getItemStack());
        else setStackInSlot(5 * 9 + 2, EconomyItems.Gui.EMPTY.getItemStack());

        for(int i = 3; i < 8; i++) {
            if(i == 5) setStackInSlot(5 * 9 + i, EconomyItems.Gui.SEARCH.getItemStack());
            else setStackInSlot(5 * 9 + i, EconomyItems.Gui.EMPTY.getItemStack());
        }

        if(hasMoreItems()) setStackInSlot(5 * 9 + 8, EconomyItems.Gui.NEXT.getItemStack());
        else setStackInSlot(5 * 9 + 8, EconomyItems.Gui.EMPTY.getItemStack());
    }

    public void updateState() {
        updateModule();

        updateTabs();
        updateItems();
        updateNavi();

        // send content updates to player
        sendContentUpdates();
    }

    @Override
    public ItemStack onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
        if(i > 0 && !playerEntity.world.isClient) {
            ItemStack stack = getSlot(i).getStack();

            // update tab
            int row = (i / 9) - startingRow; // gets a slot centered at row 3
            int newRow = this.tab + row;
            if(i % 9 == 0 && newRow >= 0 && newRow < itemModules.size()) {
                this.tab = newRow;
                updateModule();
            }

            // select item
            if(EconomyItems.ModulesScreen.MATCHER.matches(stack)) {
                returnValue = new Identifier(EconomyItems.ModulesScreen.MATCHER.getCustomTag(stack).getString("id"));
                ((GuiPlayer)playerEntity).closeScreen();
            }
        }

        return super.onSlotClick(i, j, actionType, playerEntity);
    }

    @Override
    public Text getName() {
        return new LiteralText("Bounties");
    }

    public void updateModule() {
        values = new ArrayList<>(itemModules.get(tab).getValues().values());

        // if there is a search, go through each matcher and see if it's identifier matches the search
        if(getSearch() != null) {
            List<NbtMatcher> searchedValues = new ArrayList<>();
            for(NbtMatcher matcher : values) {
                if(matcher.getIdentifier().toString().contains(getSearch().replace(" ", "_"))) searchedValues.add(matcher);
            }
            values = searchedValues;
        }

        // sort values alphabetically
        values.sort((o1, o2) ->
                new UTF16.StringComparator().compare(
                        o1.getIdentifier().toString(),
                        o2.getIdentifier().toString()
                )
        );
    }

    public boolean hasMoreItems() {
        // check next page to see if it has items in it
        return sizeInPage * (getPage() + 1) < values.size();
    }

    @Override
    public void close(PlayerEntity player) {
        for(int i = 0; i < getInventory().size(); i++) {
            ItemStack stack = getSlot(i).getStack();
            if(!GuiItem.isGuiItem(stack))
                player.dropItem(stack, false);
        }
        super.close(player);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }

    private ModulesScreen(int syncId, PlayerInventory playerInventory, Inventory inventory, int tab, String search) {
        super(syncId, playerInventory, inventory, ScreenHandlerType.GENERIC_9X6, 6);

        itemModules = new ArrayList<>(ItemModuleHandler.activeModules);
        this.tab = tab;
        setSearch(search);

        updateModule();
    }

    public GuiScreen copy(int syncId, PlayerInventory playerInventory) {
        return new ModulesScreen(syncId, playerInventory, getInventory(), tab, getSearch());
    }


}

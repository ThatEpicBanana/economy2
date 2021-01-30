package mods.banana.economy2.itemmodules.gui;

import com.ibm.icu.text.UTF16;
import mods.banana.bananaapi.helpers.ItemStackHelper;
import mods.banana.economy2.EconomyItems;
import mods.banana.economy2.itemmodules.ItemModule;
import mods.banana.economy2.itemmodules.ItemModuleHandler;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import mods.banana.economy2.items.GuiItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModulesScreen extends GenericContainerScreenHandler {
    private int tab = 0;
    private int page = 0;
    private List<NbtMatcher> values;
    private ItemModule currentModule;
    private final List<ItemModule> itemModules;

    private static final int startingRow = 2;
    private static final int sizeInPage = 7 * 5;

    protected ModulesScreen(int syncId, PlayerInventory playerInventory) {
        super(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, new SimpleInventory(9 * 6), 6);

        itemModules = new ArrayList<>(ItemModuleHandler.activeModules);

        updateModule();
        updateState();
    }

    @Override
    public ItemStack onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
        // update tab
        int row = (i / 9) - startingRow; // gets a slot centered at row 3
        int newRow = this.tab + row;
        if(i % 9 == 0 && newRow >= 0 && newRow < itemModules.size()) {
            this.tab = newRow;
            updateModule();
        }

        if(i == 9 * 5 + 2 && page != 0) page--; // previous page arrow
        else if(i == 9 * 5 + 8 && hasMoreItems()) page++; // next page arrow


        // make sure the player actually has the correct info
        if(actionType == SlotActionType.QUICK_MOVE && playerEntity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) playerEntity;
            for(int slot = 0; slot < slots.size(); slot++) {
                player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(syncId, slot, getSlot(slot).getStack()));
            }
        }

        updateState();

        return ItemStack.EMPTY;
    }

    private void updateState() {
        updateTabs();
        updateItems();
        updateNavi();

        // send content updates to player
        sendContentUpdates();
    }

    private void updateTabs() {
        // set column background
        for(int tab = 0; tab < getRows() * 9; tab += 9) {
            setStackInSlot(tab, ItemStack.EMPTY);
            if(tab == 0) setStackInSlot(tab + 1, EconomyItems.MOD_EMPTY_TOP.getItemStack());
            else if(tab == 9 * (getRows() - 1)) setStackInSlot(tab + 1, EconomyItems.MOD_EMPTY_BOT.getItemStack());
            else setStackInSlot(tab + 1, EconomyItems.MOD_EMPTY_MID.getItemStack());
        }

        // set the selected tab to the selected tab item
        setStackInSlot(9 * startingRow + 1, EconomyItems.MOD_SELECTED.getItemStack());

        // for each tab, set it's tab item and stack
        for(int tab = -Math.min(this.tab, startingRow); tab < Math.min(itemModules.size() - this.tab, 6 - startingRow); tab++) {
            // get row
            int row = (tab + startingRow) * 9;
            // get module
            ItemModule module = itemModules.get(tab + this.tab);
            // if tab isn't the currently selected tab, set the tab to unselected
            if(tab != 0) setStackInSlot(row + 1, EconomyItems.MOD_UNSELECTED.getItemStack());
            // set the stack
            setStackInSlot(row, EconomyItems.PROTECTED_ITEM.convert(module.getItemStack()));
        }
    }

    private void updateItems() {
        for(int i = 0; i < sizeInPage; i++) {
            int adjusted = i + ((Math.floorDiv(i, 7) + 1) * 2); // adjust for the two slots taken up by the left
            int index = i + sizeInPage * page; // get the index

            if(index < values.size()) {
                NbtMatcher item = values.get(index);
                setStackInSlot(adjusted, EconomyItems.PROTECTED_ITEM.convert(
                        ItemStackHelper.setLore( // set lore of item
                                item.toItemStack(), // get item from matcher
                                List.of(new LiteralText(item.getIdentifier().toString())) // set lore to identifier
                        )
                ));
            } else {
                setStackInSlot(adjusted, ItemStack.EMPTY); // clear stack
            }
        }
    }

    private void updateNavi() {
        if(page != 0) setStackInSlot(5 * 9 + 2, EconomyItems.PREVIOUS.getItemStack());
        else setStackInSlot(5 * 9 + 2, EconomyItems.EMPTY.getItemStack());

        for(int i = 3; i < 8; i++) {
            setStackInSlot(5 * 9 + i, EconomyItems.EMPTY.getItemStack());
        }

        if(hasMoreItems()) setStackInSlot(5 * 9 + 8, EconomyItems.NEXT.getItemStack());
        else setStackInSlot(5 * 9 + 8, EconomyItems.EMPTY.getItemStack());
    }

    public void updateModule() {
        currentModule = itemModules.get(tab);
        values = new ArrayList<>(currentModule.getValues().values());
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
        return sizeInPage * (page + 1) < currentModule.getValues().size();
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
}

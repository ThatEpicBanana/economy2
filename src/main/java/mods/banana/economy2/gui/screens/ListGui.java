package mods.banana.economy2.gui.screens;

import mods.banana.economy2.EconomyItems;
import mods.banana.economy2.gui.mixin.GuiPlayer;
import mods.banana.economy2.gui.GuiReturnValue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public abstract class ListGui extends GuiScreen {
    private int page = 0;
    private String search;

    public ListGui(int syncId, PlayerInventory playerInventory, Inventory inventory, int rows, Identifier id) {
        super(syncId, playerInventory, inventory, rows, id);
    }

    public ListGui(int syncId, PlayerInventory playerInventory, int rows, Identifier id) {
        super(syncId, playerInventory, rows, id);
    }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public void withReturnValue(GuiReturnValue<?> value) {
        // should be search sign
        if(value != null && value.getParent() instanceof SignGui) {
            this.search = (String) value.getValue();
            System.out.println(search);
        }

//        updateState();
    }

    @Override
    public ItemStack onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
        if(i >= 0) {
            ItemStack stack = getSlot(i).getStack();
            if(hasMoreItems() && EconomyItems.Gui.NEXT.matches(stack)) page++;
            if(page != 0 && EconomyItems.Gui.PREVIOUS.matches(stack)) page--;
            if(EconomyItems.Gui.SEARCH.matches(stack) && playerEntity instanceof ServerPlayerEntity) {
                ((GuiPlayer)playerEntity).openSignGui(new Identifier("list", "search")); // open sign for search
                return ItemStack.EMPTY;
            }
        }

        return super.onSlotClick(i, j, actionType, playerEntity);
    }

    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }

    public abstract boolean hasMoreItems();
}

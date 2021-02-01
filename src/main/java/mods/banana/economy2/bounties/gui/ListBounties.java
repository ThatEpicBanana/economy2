package mods.banana.economy2.bounties.gui;

import mods.banana.economy2.Economy2;
import mods.banana.economy2.EconomyItems;
import mods.banana.economy2.bounties.Bounty;
import mods.banana.economy2.gui.GuiReturnValue;
import mods.banana.economy2.gui.GuiScreen;
import mods.banana.economy2.gui.ListGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ListBounties extends ListGui {
    private final List<Bounty> bounties = new ArrayList<>(Economy2.bountyHandler.getBounties());

    public ListBounties() { this(0, new PlayerInventory(null)); }

    public ListBounties(int syncId, PlayerInventory playerInventory) {
        super(syncId, playerInventory, ScreenHandlerType.GENERIC_9X6, 6);
    }

    @Override
    public GuiReturnValue<?> getReturnValue() {
        return new GuiReturnValue<>("aaaa", this);
    }

    @Override
    public Text getName() {
        return new LiteralText("Bounties List");
    }

    @Override
    public void updateState() {
        // setup top row
        for(int i = 0; i < 9; i++) {
            setStackInSlot(i, EconomyItems.Gui.EMPTY.getItemStack());
        }

        // setup sides
        for(int i = 0; i < 5; i++) {
            setStackInSlot(i * 9, EconomyItems.Gui.EMPTY.getItemStack());
            setStackInSlot(i * 9 + 8, EconomyItems.Gui.EMPTY.getItemStack());
        }

        // setup bottom row
        for(int i = 0; i < 9; i++) {
            setStackInSlot(5 * 9 + i, EconomyItems.Gui.EMPTY.getItemStack());
        }

        if(getPage() != 0) setStackInSlot(5 * 9, EconomyItems.Gui.PREVIOUS.getItemStack());
        if(hasMoreItems()) setStackInSlot(5 * 9 + 8, EconomyItems.Gui.NEXT.getItemStack());

        setStackInSlot(5 * 9 + 4, EconomyItems.Gui.RETURN.getItemStack());

        // setup bounties
        for(int i = 0; i < bounties.size(); i++) {
            Bounty bounty = bounties.get(i);
            setStackInSlot(i + 10 + Math.floorDiv(i, 7) * 2, bounty.toItemStack());
        }
    }

    @Override
    public boolean hasMoreItems() {
        return bounties.size() > (getPage() + 1) * (7 * 3);
    }

    // not running. why? idk
    @Override
    public ItemStack onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
        if(playerEntity.world.isClient) return ItemStack.EMPTY;
        System.out.println("onslotclick");
//        return ItemStack.EMPTY;
        return super.onSlotClick(i, j, actionType, playerEntity);
    }

    private ListBounties(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(syncId, playerInventory, inventory, ScreenHandlerType.GENERIC_9X6, 6);
    }

    @Override
    public GuiScreen copy(int syncId, PlayerInventory inventory) {
        return new ListBounties(syncId, inventory, getInventory());
    }
}

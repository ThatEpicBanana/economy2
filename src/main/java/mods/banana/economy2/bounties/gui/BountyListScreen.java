package mods.banana.economy2.bounties.gui;

import mods.banana.economy2.Economy2;
import mods.banana.economy2.EconomyItems;
import mods.banana.economy2.bounties.Bounty;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;
import java.util.List;

public class BountyListScreen extends GenericContainerScreenHandler {
    private int page = 0;
    private final List<Bounty> bounties = new ArrayList<>(Economy2.bountyHandler.getBounties());

    protected BountyListScreen(int syncId, PlayerInventory playerInventory) {
        super(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, new SimpleInventory(9 * 6), 6);
        updateState();
    }

    private void updateState() {
        //####b####
        //#.......#
        //#.......#
        //#.......#
        //#.......#
        //p###e###n

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
            if(i == 0 && page != 0) setStackInSlot(5 * 9 + i, EconomyItems.Gui.PREVIOUS.getItemStack());
            else if(i == 4) setStackInSlot(5 * 9 + i, EconomyItems.Gui.RETURN.getItemStack());
            else if(i == 8 && bounties.size() > (page + 1) * (7 * 3)) setStackInSlot(5 * 9 + i, EconomyItems.Gui.NEXT.getItemStack());
            else setStackInSlot(5 * 9 + i, EconomyItems.Gui.EMPTY.getItemStack());
        }

        // setup bounties
        for(int i = 0; i < bounties.size(); i++) {
            Bounty bounty = bounties.get(i);
            setStackInSlot(i + 10 + Math.floorDiv(i, 7) * 2, bounty.toItemStack());
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) { return true; }

    @Override
    public ItemStack onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
        return super.onSlotClick(i, j, actionType, playerEntity);
    }
}

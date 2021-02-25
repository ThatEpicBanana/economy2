package mods.banana.economy2.bounties.gui;

import mods.banana.economy2.Economy2;
import mods.banana.economy2.EconomyItems;
import mods.banana.economy2.bounties.Bounty;
import mods.banana.economy2.gui.mixin.GuiPlayer;
import mods.banana.economy2.gui.screens.GuiScreen;
import mods.banana.economy2.gui.screens.ListGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static mods.banana.economy2.EconomyItems.Bounties.*;
import static mods.banana.economy2.EconomyItems.Gui.*;

import java.util.List;

public class ManageBounties extends ListGui {
    private List<Bounty> bounties;
    private final PlayerEntity playerEntity;

    public ManageBounties(PlayerEntity playerEntity) { this(0, new PlayerInventory(null), playerEntity); }

    public ManageBounties(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        super(syncId, playerInventory, getRows(playerEntity), new Identifier("bounty", "manage"));
        updateBounties();
        this.playerEntity = playerEntity;
    }

    @Override
    public Text getDisplayName() {
        return new LiteralText("Manage Bounties");
    }

    @Override
    public void updateState() {
        updateBounties();

        // top / bottom
        for(int i = 0; i < 9; i++) {
            setStackInSlot(i, EMPTY.getItemStack());
            setStackInSlot(i + ((serverGetRows() - 1) * 9), EMPTY.getItemStack());
        }
        // sides
        for(int i = 1; i < serverGetRows() - 1; i++) {
            setStackInSlot(9 * i, EMPTY.getItemStack());
            setStackInSlot(9 * i + 8, EMPTY.getItemStack());
        }

        int i;
        // add all bounties
        for(i = 0; i < Math.min(bounties.size(), (serverGetRows() - 2) * 7); i++) {
            int slot = i + 10 + (Math.floorDiv(i, 7) * 2);
            setStackInSlot(slot, BOUNTY.convertTag(bounties.get(i).toItemStack().copy()));
        }
        // clear all empty stacks
        for(; i < (serverGetRows() - 2) * 7; i++) {
            int slot = i + 10 + (Math.floorDiv(i, 7) * 2);
            setStackInSlot(slot, ItemStack.EMPTY);
        }

        setStackInSlot((serverGetRows() - 1) * 9 + 4, RETURN.getItemStack());
        setStackInSlot((serverGetRows() - 1) * 9 + 5, ADD_BOUNTY.getItemStack());
    }

    @Override
    public ItemStack onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
        if(i >= 0 && !playerEntity.world.isClient) {
            ItemStack stack = getSlot(i).getStack();
            if(ADD_BOUNTY.matches(stack)) ((GuiPlayer)playerEntity).openScreen(new CreateBounty());
            if(BOUNTY.matches(stack)) ((GuiPlayer)playerEntity).openScreen(
                    new EditBounty(bounties.get(
                            i // start with starting value
                                    - (Math.floorDiv(i - 9, 9) * 2) // correct for the two slots taken up by the sides
                                    - (9 + 1) // correct since the list starts on the second line
                    ))
            );
        }

        return super.onSlotClick(i, j, actionType, playerEntity);
    }

    public static int getRows(PlayerEntity player) {
        if(player == null) {
//            System.out.println("player null");
            return 3;
        }
        List<Bounty> bounties = Economy2.bountyHandler.getBounties(player.getUuid());
        return Math.min(Math.max(Math.floorDiv(bounties.size() - 1, 7), 0), 3) + 3; // 3-6 lines
    }

    public static Inventory update(Inventory inventory, int rows) {
        Inventory newInventory = new SimpleInventory(9 * rows);

        // set all stacks from previous inventory until we get to either inventory's limit
        for(int i = 0; i < Math.min(inventory.size(), 9 * rows); i++)
            newInventory.setStack(i, inventory.getStack(i));

        return newInventory;
    }

    public void updateBounties() {
        if(getPlayerInventory().player != null) this.bounties = Economy2.bountyHandler.getBounties(getPlayerInventory().player.getUuid());
    }

//    public ManageBounties(int syncId, PlayerInventory playerInventory, Inventory inventory, PlayerEntity playerEntity) {
//        super(syncId, playerInventory, update(inventory, getRows(playerEntity)), getRows(playerEntity), new Identifier("bounty", "manage"));
//        updateBounties();
//    }
//
//    @Override
//    public GuiScreen copy(int syncId, PlayerInventory inventory, PlayerEntity player) {
//        return new ManageBounties(syncId, inventory, getInventory(), player);
//    }


    @Override
    public int getNewRows() {
        return getRows(playerEntity);
    }

    @Override
    public boolean hasMoreItems() {
        return false;
    }
}

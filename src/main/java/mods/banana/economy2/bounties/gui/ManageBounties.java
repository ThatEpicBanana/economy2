package mods.banana.economy2.bounties.gui;

import mods.banana.economy2.Economy2;
import mods.banana.economy2.EconomyItems;
import mods.banana.economy2.bounties.Bounty;
import mods.banana.economy2.gui.GuiScreen;
import mods.banana.economy2.gui.ListGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ManageBounties extends ListGui {
    private List<Bounty> bounties;

    public ManageBounties() { this(0, new PlayerInventory(null), null); }

    public ManageBounties(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        super(syncId, playerInventory, getRows(null));
        updateBounties();
    }

    @Override
    public Text getName() {
        return new LiteralText("Manage Bounties");
    }

    @Override
    public void updateState() {
        // top / bottom
        for(int i = 0; i < 9; i++) {
            setStackInSlot(i, EconomyItems.Gui.EMPTY.getItemStack());
            setStackInSlot(i + ((getRows() - 1) * 9), EconomyItems.Gui.EMPTY.getItemStack());
        }
        // sides
        for(int i = 1; i < getRows() - 1; i++) {
            setStackInSlot(9 * i, EconomyItems.Gui.EMPTY.getItemStack());
            setStackInSlot(9 * i + 8, EconomyItems.Gui.EMPTY.getItemStack());
        }

        // add all bounties
        for(int i = 0; i < Math.min(bounties.size(), (getRows() - 2) * 7); i++) {
            int slot = i + 10 + (Math.floorDiv(i, 7) * 2);
            setStackInSlot(slot, bounties.get(i).toItemStack());
        }

        setStackInSlot((getRows() - 1) * 9 + 4, EconomyItems.Gui.RETURN.getItemStack());
        setStackInSlot((getRows() - 1) * 9 + 5, EconomyItems.Bounties.ADD_BOUNTY.getItemStack());
    }

    public static int getRows(PlayerEntity player) {
        if(player == null) {
//            System.out.println("player null");
            return 3;
        }
        List<Bounty> bounties = Economy2.bountyHandler.getBounties(player.getUuid());
        return Math.min(Math.floorDiv(bounties.size(), 7), 3) + 3; // 3-6 lines
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

    public ManageBounties(int syncId, PlayerInventory playerInventory, Inventory inventory, PlayerEntity playerEntity) {
        super(syncId, playerInventory, update(inventory, getRows(playerEntity)), getRows(playerEntity));
        updateBounties();
    }

    @Override
    public GuiScreen copy(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new ManageBounties(syncId, inventory, getInventory(), player);
    }

    @Override
    public boolean hasMoreItems() {
        return false;
    }
}

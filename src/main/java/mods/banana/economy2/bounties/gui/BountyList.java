package mods.banana.economy2.bounties.gui;

import mods.banana.economy2.Economy2;
import mods.banana.economy2.EconomyItems;
import mods.banana.economy2.bounties.Bounty;
import mods.banana.economy2.gui.GuiScreen;
import mods.banana.economy2.gui.ListGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class BountyList extends ListGui {
    private final List<Bounty> bounties = new ArrayList<>(Economy2.bountyHandler.getBounties());

    public static int sizeInScreen = 7 * 4;

    public BountyList() { this(0, new PlayerInventory(null)); }

    public BountyList(int syncId, PlayerInventory playerInventory) {
        super(syncId, playerInventory, 6, new Identifier("bounty", "list"));
    }

//    @Override
//    public GuiReturnValue<?> getReturnValue() {
//        return new GuiReturnValue<>("aaaa", this);
//    }

    @Override
    public Text getName() {
        return new LiteralText("Bounties List");
    }

    @Override
    public void updateState() {
        // setup top and bottom rows
        for(int i = 0; i < 9; i++) {
            setStackInSlot(i, EconomyItems.Gui.EMPTY.getItemStack());
            setStackInSlot(5 * 9 + i, EconomyItems.Gui.EMPTY.getItemStack());
        }

        // setup sides
        for(int i = 0; i < 5; i++) {
            setStackInSlot(i * 9, EconomyItems.Gui.EMPTY.getItemStack());
            setStackInSlot(i * 9 + 8, EconomyItems.Gui.EMPTY.getItemStack());
        }

        if(getPage() != 0) setStackInSlot(5 * 9, EconomyItems.Gui.PREVIOUS.getItemStack());
        if(hasMoreItems()) setStackInSlot(5 * 9 + 8, EconomyItems.Gui.NEXT.getItemStack());

        setStackInSlot(5 * 9 + 4, EconomyItems.Gui.RETURN.getItemStack());

        for(int i = 0; i < sizeInScreen; i++) {
            int adjusted = i + 10 + (Math.floorDiv(i, 7) * 2); // adjust for the two slots taken up by the left
            int index = i + sizeInScreen * getPage(); // get the index

            if(index < bounties.size()) setStackInSlot(adjusted, bounties.get(index).toItemStack());
            else setStackInSlot(adjusted, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean hasMoreItems() {
        return bounties.size() > (getPage() + 1) * sizeInScreen;
    }

    private BountyList(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(syncId, playerInventory, inventory, 6, new Identifier("bounty", "list"));
    }

    @Override
    public GuiScreen copy(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new BountyList(syncId, inventory, getInventory());
    }
}

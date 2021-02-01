package mods.banana.economy2.bounties.gui;

import mods.banana.economy2.EconomyItems;
import mods.banana.economy2.gui.GuiPlayer;
import mods.banana.economy2.gui.GuiReturnValue;
import mods.banana.economy2.gui.GuiScreen;
import mods.banana.economy2.itemmodules.gui.ModulesScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class BPage1 extends GuiScreen {
    public BPage1() { this(0, new PlayerInventory(null)); }

    public BPage1(int syncId, PlayerInventory playerInventory) {
        super(syncId, playerInventory, ScreenHandlerType.GENERIC_9X4, 4);
    }

    @Override
    public Text getName() {
        return new LiteralText("Bounties");
    }

    @Override
    public void updateState() {
        // clear inventory
        ItemStack emptyStack = EconomyItems.Gui.EMPTY.getItemStack();
        for(int i = 0; i < 4 * 9; i++) setStackInSlot(i, emptyStack);

        setStackInSlot(9 + 3, EconomyItems.Bounties.VIEW_ALL.getItemStack());
        setStackInSlot(9 + 5, EconomyItems.Bounties.VIEW_SELF.getItemStack());
        setStackInSlot(9 * 3 + 4, EconomyItems.Gui.EXIT.getItemStack());
    }

    @Override
    public ItemStack onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
        if(i > 0 && playerEntity instanceof ServerPlayerEntity) {
            ItemStack stack = getSlot(i).getStack();

            if(EconomyItems.Bounties.VIEW_ALL.matches(stack))
                ((GuiPlayer)playerEntity).openScreen(new ListBounties());
        }

        return super.onSlotClick(i, j, actionType, playerEntity);
    }

    private BPage1(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(syncId, playerInventory, inventory, ScreenHandlerType.GENERIC_9X4, 4);
    }

    @Override
    public GuiScreen copy(int syncId, PlayerInventory inventory) {
        return new BPage1(syncId, inventory, getInventory());
    }
}

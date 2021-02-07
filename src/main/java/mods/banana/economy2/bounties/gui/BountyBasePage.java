package mods.banana.economy2.bounties.gui;

import mods.banana.economy2.EconomyItems;
import mods.banana.economy2.gui.mixin.GuiPlayer;
import mods.banana.economy2.gui.screens.GuiScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BountyBasePage extends GuiScreen {
    public BountyBasePage() { this(0, new PlayerInventory(null)); }

    public BountyBasePage(int syncId, PlayerInventory playerInventory) {
        super(syncId, playerInventory, ScreenHandlerType.GENERIC_9X4, 4, new Identifier("bounty", "base"));
    }

    @Override
    public Text getDisplayName() {
        return new LiteralText("Bounties");
    }

    @Override
    public void updateState() {
        // clear inventory
        ItemStack emptyStack = EconomyItems.Gui.EMPTY.getItemStack();
        for(int i = 0; i < 4 * 9; i++) setStackInSlot(i, emptyStack);

        setStackInSlot(9 + 3, EconomyItems.Bounties.BaseScreen.VIEW_ALL.getItemStack());
        setStackInSlot(9 + 5, EconomyItems.Bounties.BaseScreen.VIEW_SELF.getItemStack());
        setStackInSlot(9 * 3 + 4, EconomyItems.Gui.EXIT.getItemStack());
    }

    @Override
    public ItemStack onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
        if(i > 0 && playerEntity instanceof ServerPlayerEntity) {
            ItemStack stack = getSlot(i).getStack();

            if(EconomyItems.Bounties.BaseScreen.VIEW_ALL.matches(stack))
                ((GuiPlayer)playerEntity).openScreen(new BountyList());
            if(EconomyItems.Bounties.BaseScreen.VIEW_SELF.matches(stack))
                ((GuiPlayer)playerEntity).openScreen(new ManageBounties());
        }

        return super.onSlotClick(i, j, actionType, playerEntity);
    }

    private BountyBasePage(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(syncId, playerInventory, inventory, ScreenHandlerType.GENERIC_9X4, 4, new Identifier("bounty", "base"));
    }

//    @Override
//    public GuiScreen copy(int syncId, PlayerInventory inventory, PlayerEntity player) {
//        return new BountyBasePage(syncId, inventory, getInventory());
//    }
}

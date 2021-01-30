package mods.banana.economy2.bounties.gui;

import mods.banana.economy2.EconomyItems;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

import java.util.Arrays;

public class BPage1Screen extends GenericContainerScreenHandler {
    protected BPage1Screen(int syncId, PlayerInventory playerInventory) {
        super(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, new SimpleInventory(9*3), 6);
        // clear inventory
        ItemStack emptyStack = EconomyItems.EMPTY.getItemStack();
        for(int i = 0; i < 3 * 9; i++) setStackInSlot(i, emptyStack);

        setStackInSlot(9 + 3, EconomyItems.VIEW_ALL.getItemStack());
    }

    private static ItemStack[] getBaseItems() {
        ItemStack[] list = new ItemStack[9*3];
        Arrays.fill(list, EconomyItems.EMPTY.getItemStack());
        return list;
    }
}

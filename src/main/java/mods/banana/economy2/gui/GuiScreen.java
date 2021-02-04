package mods.banana.economy2.gui;

import mods.banana.economy2.EconomyItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public abstract class GuiScreen extends GenericContainerScreenHandler implements CustomGui {
    private final ScreenHandlerType<GenericContainerScreenHandler> size;
    private final PlayerInventory playerInventory;

    public GuiScreen(int syncId, PlayerInventory playerInventory, Inventory inventory, ScreenHandlerType<GenericContainerScreenHandler> size, int rows) {
        super(size, syncId, playerInventory, inventory, rows);
        this.playerInventory = playerInventory;
        this.size = size;
    }

    public GuiScreen(int syncId, PlayerInventory playerInventory, Inventory inventory, int rows) {
        this(syncId, playerInventory, inventory, getScreenHandlerType(rows), rows);
    }

    public GuiScreen(int syncId, PlayerInventory playerInventory, ScreenHandlerType<GenericContainerScreenHandler> size, int rows) {
        super(size, syncId, playerInventory, new SimpleInventory(9 * rows), rows);
        this.playerInventory = playerInventory;
        this.size = size;
    }

    public GuiScreen(int syncId, PlayerInventory playerInventory, int rows) {
        this(syncId, playerInventory, getScreenHandlerType(rows), rows);
    }

    public abstract Text getName();

    public abstract void updateState();

    @Override
    public ItemStack onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
        // make sure the player actually has the correct info
        if(i >= 0) {
            if(playerEntity instanceof ServerPlayerEntity) {
                if(actionType == SlotActionType.QUICK_MOVE) {
                    ServerPlayerEntity player = (ServerPlayerEntity) playerEntity;
                    for(int slot = 0; slot < slots.size(); slot++) {
                        player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(syncId, slot, getSlot(slot).getStack()));
                    }
                }

                ItemStack stack = getSlot(i).getStack();

                if(EconomyItems.Gui.EXIT.matches(stack)) ((GuiPlayer)playerEntity).exitScreen();
                if(EconomyItems.Gui.RETURN.matches(stack)) ((GuiPlayer)playerEntity).closeScreen();
            }

            updateState();
        }

        return ItemStack.EMPTY;
    }

    public NamedScreenHandlerFactory toFactory() {
        return new Factory(getName(), this);
    }

    public GuiReturnValue<?> getReturnValue() {
        return GuiReturnValue.EMPTY;
    }

    public void withReturnValue(GuiReturnValue<?> value) {}

    public abstract GuiScreen copy(int syncId, PlayerInventory inventory, PlayerEntity player);

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    // if the play handler sees that the items mismatch, it adds the player to the restricted list
    // so this is here to bypass that list
    @Override
    public boolean isNotRestricted(PlayerEntity player) {
        return true;
    }

    /**
     * gets a screen handler type from a number of rows, default 3.
     * @return ScreenHandlerType<GenericContainerScreenHandler> screen handler type
     */
    public static ScreenHandlerType<GenericContainerScreenHandler> getScreenHandlerType(int rows) {
        switch(rows) {
            case 1:
                return ScreenHandlerType.GENERIC_9X1;
            case 2:
                return ScreenHandlerType.GENERIC_9X2;
            case 4:
                return ScreenHandlerType.GENERIC_9X4;
            case 5:
                return ScreenHandlerType.GENERIC_9X5;
            case 6:
                return ScreenHandlerType.GENERIC_9X6;
            default: // default is 9x3 so don't have to put in the case for a row size of 3
                return ScreenHandlerType.GENERIC_9X3;
        }
    }

    // used for cloning
    public ScreenHandlerType<GenericContainerScreenHandler> getSize() { return size; }
    public PlayerInventory getPlayerInventory() { return playerInventory; }

    public static class Factory implements NamedScreenHandlerFactory {
        private final Text name;
        private final GuiScreen screen;

        public Factory(Text name, GuiScreen screen) {
            this.name = name;
            this.screen = screen;
        }

        @Override
        public Text getDisplayName() {
            return name;
        }

        @Nullable
        @Override
        public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
            return screen.copy(syncId, inv, player);
        }
    }
}

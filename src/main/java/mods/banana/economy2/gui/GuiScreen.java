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
import net.minecraft.text.LiteralText;
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

    public GuiScreen(int syncId, PlayerInventory playerInventory, ScreenHandlerType<GenericContainerScreenHandler> size, int rows) {
        super(size, syncId, playerInventory, new SimpleInventory(9 * rows), rows);
        this.playerInventory = playerInventory;
        this.size = size;
//        updateState();
    }

    public abstract Text getName();

    public abstract void updateState();

    @Override
    public ItemStack onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
        // make sure the player actually has the correct info
        if(i > 0) {
            if(actionType == SlotActionType.QUICK_MOVE && playerEntity instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) playerEntity;
                for(int slot = 0; slot < slots.size(); slot++) {
                    player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(syncId, slot, getSlot(slot).getStack()));
                }
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

    public abstract GuiScreen copy(int syncId, PlayerInventory inventory);

    @Override
    public void close(PlayerEntity player) {
        if(player instanceof ServerPlayerEntity) {
            // close screen and prevent infinite loop
            if(!((GuiPlayer)player).isClosingGuiScreen()) ((GuiPlayer)player).closeScreen(false);
        }
        super.close(player);
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
            return screen.copy(syncId, inv);
        }
    }
}
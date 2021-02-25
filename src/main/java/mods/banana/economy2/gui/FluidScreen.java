package mods.banana.economy2.gui;

import mods.banana.economy2.gui.mixin.ScreenHandlerInterface;
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
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public abstract class FluidScreen extends GenericContainerScreenHandler implements NamedScreenHandlerFactory, Cloneable {
    // override screen handler's final syncId
    // if this gets converted to a regular screen handler, it will still show the original syncId however
    public int syncId;
    public Inventory inv;
    public PlayerInventory playerInventory;
    public int rows;
    public ScreenHandlerType<?> type;

    public FluidScreen(PlayerInventory playerInventory, int syncId, int rows) {
        this(playerInventory, new SimpleInventory(9 * rows), syncId, rows);
    }

    public FluidScreen(PlayerInventory playerInventory, Inventory inventory, int syncId, int rows) {
        // prevent a sync id of zero as it designates the player screen handler
        super(getType(rows), syncId == 0 ? 1 : syncId, playerInventory, inventory, rows);
        this.syncId = syncId == 0 ? 1 : syncId;
        this.inv = inventory;
        this.playerInventory = playerInventory;
        this.rows = rows;
        this.type = getType(rows);
    }

    @Override
    public boolean canUse(PlayerEntity player) { return true; }

    // if the play handler sees that the items mismatch, it adds the player to the restricted list
    // so this is here to bypass that list
    @Override
    public boolean isNotRestricted(PlayerEntity player) {
        return true;
    }

    public static ScreenHandlerType<GenericContainerScreenHandler> getType(int rows) {
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

    public void updatePlayerInventory(PlayerInventory playerInventory) {
        // basically just directly copied from GenericScreenHandler
        int i = (getRows() - 4) * 18;
        int invOffset = inv.size();

        int n;
        int m;

        for(n = 0; n < 3; ++n) {
            for(m = 0; m < 9; ++m) {
                setSlot(
                        invOffset + m + n * 9,
                        new Slot(playerInventory, m + n * 9 + 9, 8 + m * 18, 103 + n * 18 + i)
                );
            }
        }

        int mainOffset = playerInventory.main.size() - 9;

        for(n = 0; n < 9; ++n) {
            setSlot(
                    invOffset + mainOffset + n,
                    new Slot(playerInventory, n, 8 + n * 18, 161 + i)
            );
        }

        this.playerInventory = playerInventory;
    }

    public void updateSlots() {
        asScreenHandlerInterface().clearSlots();

        int i = (this.serverGetRows() - 4) * 18;

        int n;
        int m;
        for(n = 0; n < this.serverGetRows(); ++n) {
            for(m = 0; m < 9; ++m) {
                this.addSlot(new Slot(getInventory(), m + n * 9, 8 + m * 18, 18 + n * 18));
            }
        }

        for(n = 0; n < 3; ++n) {
            for(m = 0; m < 9; ++m) {
                this.addSlot(new Slot(playerInventory, m + n * 9 + 9, 8 + m * 18, 103 + n * 18 + i));
            }
        }

        for(n = 0; n < 9; ++n) {
            this.addSlot(new Slot(playerInventory, n, 8 + n * 18, 161 + i));
        }
    }

    public void setRows(int rows) {
        this.rows = rows;
        this.type = getType(rows);

        Inventory newInventory = new SimpleInventory(9 * rows);
        Inventory oldInventory = getInventory();

        for(int i = 0; i < Math.min(oldInventory.size(), newInventory.size()); i++) {
            ItemStack stack = oldInventory.getStack(i);
            newInventory.setStack(i, stack);
        }

        this.inv = newInventory;

        updateSlots();
    }

    public void forceStackUpdates(ServerPlayNetworkHandler networkHandler) {
        for(int slot = 0; slot < slots.size(); slot++) {
            networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(((ScreenHandler)this).syncId, slot, getSlot(slot).getStack()));
            networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(syncId, slot, getSlot(slot).getStack()));
        }
    }

    public abstract Text getDisplayName();

    public void setSlot(int i, Slot slot) {
        asScreenHandlerInterface().overrideSlot(i, slot);
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.inv.onClose(player);
    }

    @Override
    public Inventory getInventory() {
        return this.inv;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        this.syncId = syncId;
        updatePlayerInventory(inv);
        return this;
    }

    public ScreenHandlerInterface asScreenHandlerInterface() {
        return (ScreenHandlerInterface) this;
    }

    public int serverGetRows() {
        return rows;
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return type;
    }
}

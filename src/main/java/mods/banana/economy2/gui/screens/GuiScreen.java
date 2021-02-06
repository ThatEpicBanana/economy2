package mods.banana.economy2.gui.screens;

import mods.banana.economy2.EconomyItems;
import mods.banana.economy2.gui.CustomGui;
import mods.banana.economy2.gui.FluidScreen;
import mods.banana.economy2.gui.mixin.GuiPlayer;
import mods.banana.economy2.gui.GuiReturnValue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public abstract class GuiScreen extends FluidScreen implements CustomGui {
    private final ScreenHandlerType<GenericContainerScreenHandler> size;
    private PlayerInventory playerInventory;
    private final Identifier id;
//    public int syncId;

    public GuiScreen(int syncId, PlayerInventory playerInventory, Inventory inventory, ScreenHandlerType<GenericContainerScreenHandler> size, int rows, Identifier id) {
        super(playerInventory, inventory, syncId, rows);
        this.playerInventory = playerInventory;
        this.size = size;
        this.id = id;
    }

    public GuiScreen(int syncId, PlayerInventory playerInventory, Inventory inventory, int rows, Identifier id) {
        this(syncId, playerInventory, inventory, FluidScreen.getType(rows), rows, id);
    }

    public GuiScreen(int syncId, PlayerInventory playerInventory, ScreenHandlerType<GenericContainerScreenHandler> size, int rows, Identifier id) {
        super(playerInventory, syncId, rows);
        this.playerInventory = playerInventory;
        this.size = size;
        this.id = id;
    }

    public GuiScreen(int syncId, PlayerInventory playerInventory, int rows, Identifier id) {
        this(syncId, playerInventory, FluidScreen.getType(rows), rows, id);
    }

//    public abstract Text getName();

    public Identifier getId() { return id; }

    public abstract void updateState();

    public void clear() {
        for(int i = 0; i < getInventory().size(); i++) {
            setStackInSlot(i, EconomyItems.Gui.EMPTY.getItemStack());
        }
    }

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

    public GuiReturnValue<?> getReturnValue() {
        return GuiReturnValue.EMPTY;
    }
    public void withReturnValue(GuiReturnValue<?> value) {}

    public abstract GuiScreen copy(int syncId, PlayerInventory inventory, PlayerEntity player);

    // used for cloning
    public ScreenHandlerType<GenericContainerScreenHandler> getSize() { return size; }
    public PlayerInventory getPlayerInventory() { return playerInventory; }

    @Override
    public void updatePlayerInventory(PlayerInventory inv) {
        playerInventory = inv;
        super.updatePlayerInventory(inv);
    }
}

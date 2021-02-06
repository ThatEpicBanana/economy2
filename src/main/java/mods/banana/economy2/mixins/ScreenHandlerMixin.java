package mods.banana.economy2.mixins;

import mods.banana.economy2.chestshop.interfaces.mixin.ChestInterface;
import mods.banana.economy2.EconomyItems;
import mods.banana.economy2.gui.mixin.ScreenHandlerInterface;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin implements ScreenHandlerInterface {
    @Final
    @Shadow
    public List<Slot> slots;

    @Shadow public abstract Slot getSlot(int index);

    @Shadow public abstract void setStackInSlot(int slot, ItemStack stack);

    @Shadow @Final private DefaultedList<ItemStack> trackedStacks;

    @Inject(method = "onSlotClick", at = {@At("HEAD")})
    private void checkLimitedItem(int i, int j, SlotActionType actionType, PlayerEntity playerEntity, CallbackInfoReturnable<ItemStack> cir) {
        if(i < 0) return;
        if(EconomyItems.ChestShop.LIMIT.matches(playerEntity.inventory.getCursorStack())) {
            if(EconomyItems.ChestShop.LIMITED.matches(getSlot(i).getStack())) setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Inject(method = "onSlotClick", at = {@At("TAIL")}, cancellable = true)
    private void onClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity, CallbackInfoReturnable<ItemStack> cir) {
        // disregard chest close call
        if(i < 0) return;

        // check if slot dropped is a limit item
        if(EconomyItems.ChestShop.LIMIT.matches(getSlot(i).getStack())) {
            // check if screen handler is a container
            if (playerEntity.currentScreenHandler instanceof GenericContainerScreenHandler) {
                // get inventory from screen handler
                Inventory inventory = ((GenericContainerScreenHandler) playerEntity.currentScreenHandler).getInventory();
                // check inventory is from a chest block
                if (inventory instanceof ChestBlockEntity) {
                    // get chest shop from chest block
                    ChestInterface chestshop = (ChestInterface) inventory;
                    // make sure chest shop is a shop
                    if (chestshop.isChestShop()) {
                        // set chest shop limit to index
                        chestshop.setLimit(i);

                        // remove limited item if player picked it up
                        if(EconomyItems.ChestShop.LIMITED.matches(playerEntity.inventory.getCursorStack())) playerEntity.inventory.setCursorStack(ItemStack.EMPTY);
                        if(playerEntity instanceof ServerPlayerEntity) ((ServerPlayerEntity) playerEntity).updateCursorStack();

                        // update player's screen
                        playerEntity.currentScreenHandler.sendContentUpdates();
                    }
                }
            }
        }
    }

    public void overrideSlot(int i, Slot slot) {
        this.slots.set(i, slot);
        trackedStacks.set(i, slot.getStack());
        slot.id = i;
    }
}

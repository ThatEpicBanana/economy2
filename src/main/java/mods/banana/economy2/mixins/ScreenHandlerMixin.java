package mods.banana.economy2.mixins;

import mods.banana.economy2.chestshop.ChestInterface;
import mods.banana.economy2.EconomyItems;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
    @Final
    @Shadow
    public List<Slot> slots;

    @Inject(method = "onSlotClick", at = {@At("HEAD")}, cancellable = true)
    private void onClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity, CallbackInfoReturnable<ItemStack> cir) {
        // disregard chest close call
        if(i < 0) return;

        // check if slot dropped is a limit item
        if(EconomyItems.LIMIT.sameIdentifierAs(playerEntity.inventory.getCursorStack())) {
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

                        // weird hack to make it so the limit item doesn't duplicate itself
                        playerEntity.currentScreenHandler.setStackInSlot(i, ItemStack.EMPTY);
                        playerEntity.inventory.setCursorStack(EconomyItems.LIMIT.getItemStack());
                        // update player's screen
                        playerEntity.currentScreenHandler.sendContentUpdates();
                    }
                }
            }
        }
    }
}

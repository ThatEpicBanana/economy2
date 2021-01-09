package mods.banana.economy2.mixins;

import mods.banana.economy2.interfaces.ChestInterface;
import mods.banana.economy2.items.EconomyItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ChestBlockEntity.class)
public class ChestEntityMixin extends BlockEntity implements ChestInterface {
//    @Shadow private DefaultedList<ItemStack> inventory;
    private boolean chestShop = false;
    private UUID parent;

    public ChestEntityMixin(BlockEntityType<?> type) { super(type); }

    public boolean isChestShop() { return chestShop; }
    public UUID getParent() { return parent; }

    public void setLimit(int index) {
        Inventory inventory = (Inventory) this;

        //return if index is out of bounds
        if(index < 0 || index > inventory.size()) return;

        //remove all limited items
        for(int i = 0; i < inventory.size(); i++) {
            if(EconomyItems.LIMITED.sameIdentifierAs(inventory.getStack(i))) inventory.setStack(i, ItemStack.EMPTY);
        }

        //if index slot isn't already the limit item, set it to it.
        inventory.setStack(index, EconomyItems.LIMIT.getItemStack());

        //add all of the limited item
        for(int i = inventory.size(); i > index; i--) {
            if(inventory.getStack(i) != ItemStack.EMPTY && !EconomyItems.LIMITED.sameIdentifierAs(inventory.getStack(i))) insertItemStack(inventory.getStack(i));
            inventory.setStack(i, EconomyItems.LIMITED.getItemStack());
        }

        inventory.markDirty();
    }

    public Integer getLimit() {
        Inventory inventory = (Inventory) this;
        for(int i = 0; i < inventory.size(); i++)
            if(EconomyItems.LIMIT.sameIdentifierAs(inventory.getStack(i))) return i;
            return null;
    }

    public void insertItemStack(ItemStack input) {
        Inventory inventory = (Inventory) this;
        Item item = input.getItem();

        // for each slot
        for(int i = 0; i < inventory.size() && input.getCount() > 0; i++) {
            ItemStack stack = inventory.getStack(i);
            if(
                    stack.isEmpty() || // if slot is empty
                            (stack.getItem() == item && stack.getCount() < stack.getMaxCount()) // or the slot is the same item and has space
            ) {
                // get count
                int count = Math.min(input.getCount() + stack.getCount(), input.getMaxCount()) - stack.getCount();
                // add count to item stack
                ItemStack newStack = input.copy();
                newStack.setCount(count + stack.getCount());
                inventory.setStack(i, newStack);
                // remove count from input
                input.setCount(input.getCount() - count);
            }
        }

        if(input.getCount() > 0) this.world.spawnEntity(new ItemEntity(world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), input));
    }

    @Inject(method = "onClose", at = {@At("HEAD")})
    private void close(PlayerEntity player, CallbackInfo ci) {
        // remove all limit and limited items from player
        for(int i = 0; i < 41; i++) {
            if(EconomyItems.LIMIT.sameIdentifierAs(player.inventory.getStack(i))) player.inventory.removeStack(i);
            if(EconomyItems.LIMITED.sameIdentifierAs(player.inventory.getStack(i))) player.inventory.removeStack(i);
        }

        // if the inventory doesn't have a limiter, reset it
        if(chestShop && getLimit() == null) {setLimit(26); System.out.println("resetting limiter");}
    }

    // bro you could just save data to the block itself
    // i am *massive* brain
    @Inject(method = "toTag", at = {@At("HEAD")})
    private void save(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        if(chestShop) {
            CompoundTag chestShopTag = new CompoundTag();
            chestShopTag.putUuid("parent", parent);
            tag.put("chestshop", chestShopTag);
        }
    }

    @Inject(method = "fromTag", at = {@At("HEAD")})
    private void load(BlockState state, CompoundTag tag, CallbackInfo ci) {
        if(tag.contains("chestshop")) {
            chestShop = true;
            CompoundTag chestShopTag = tag.getCompound("chestshop");
            parent = chestShopTag.getUuid("parent");
        }
    }
}
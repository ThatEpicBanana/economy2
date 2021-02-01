package mods.banana.economy2.mixins.block;

import mods.banana.bananaapi.helpers.ItemStackHelper;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.chestshop.interfaces.mixin.ChestInterface;
import mods.banana.economy2.chestshop.interfaces.mixin.SignInterface;
import mods.banana.economy2.EconomyItems;
import mods.banana.economy2.itemmodules.items.NbtItem;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(ChestBlockEntity.class)
public abstract class ChestEntityMixin extends LootableContainerBlockEntity implements ChestInterface {
    @Shadow public abstract int size();

    @Shadow private DefaultedList<ItemStack> inventory;
    private boolean chestShop = false;
    private BlockPos sign;
    private UUID parent;

    public void create(ServerPlayerEntity player, BlockPos sign) {
        this.parent = player.getUuid();
        this.sign = sign;
        chestShop = true;
        setLimit(size() - 1);
    }

    public void destroy(boolean destroyOther) {
        this.chestShop = false;
        this.parent = null;
        if(destroyOther) ((SignInterface)world.getBlockEntity(sign)).destroy(false);
        this.sign = null;

        for(int i = 0; i < size(); i++) {
            if(EconomyItems.ChestShop.LIMITED.matches(getStack(i))) setStack(i, ItemStack.EMPTY);
            if(EconomyItems.ChestShop.LIMIT.matches(getStack(i))) setStack(i, ItemStack.EMPTY);
        }
    }

    public ChestEntityMixin(BlockEntityType<?> itemType) { super(itemType); }

    public boolean isChestShop() { return chestShop && Economy2.CONFIG.getValue("chestShop.enabled", Boolean.class); }
    public UUID getParent() { return parent; }
    public SignInterface getSign() { return (SignInterface) world.getBlockEntity(sign); }

    public void setLimit(int index) {
        //return if index is out of bounds
        if(index < 0 || index > size()) return;

        //remove all limited items
        for(int i = 0; i < size(); i++) {
            if(EconomyItems.ChestShop.LIMITED.matches(getStack(i))) setStack(i, ItemStack.EMPTY);
        }

        //if index slot isn't already the limit item, set it to it.
        setStack(index, EconomyItems.ChestShop.LIMIT.getItemStack());

        //add all of the limited item
        for(int i = inventory.size() - 1; i > index; i--) {
            if(getStack(i) != ItemStack.EMPTY && !EconomyItems.ChestShop.LIMITED.matches(getStack(i))) insertStack(getStack(i));
            setStack(i, EconomyItems.ChestShop.LIMITED.getItemStack());
        }

        markDirty();
    }

    public Integer getLimit() {
//        Inventory inventory = (Inventory) this;
        for(int i = 0; i < size(); i++) {
            if(EconomyItems.ChestShop.LIMIT.matches(getStack(i))) return i;
        }
        setLimit(size() - 1);
        return getLimit();
    }

    public void insertStack(ItemStack input) {
        int amountLeft = input.getCount();
        // for each slot
        for(int i = 0; i < size() && amountLeft > 0; i++) {
            ItemStack stack = getStack(i);
            if(stack.isEmpty()) { // if the slot is empty:
                // the count to add is either the current input count or the maximum
                int count = Math.min(amountLeft, input.getMaxCount());
                // set slot to the new stack
                setStack(i, ItemStackHelper.setCount(input.copy(), count));
                // remove count from input
                amountLeft -= count;
            } else if (ScreenHandler.canStacksCombine(stack, input) && stack.getCount() < stack.getMaxCount()) { // if the two items can combine
                // get the amount to set to; either the stacks put together or the max stack count
                int count = Math.min(stack.getCount() + amountLeft, input.getMaxCount());
                // remove count from input
                amountLeft -= (count - stack.getCount());
                // set count of stack to new count
                stack.setCount(count);
            }
        }

        if(amountLeft > 0) {
            // copy input
            ItemStack newItem = input.copy();
            // set it's count to the new count
            newItem.setCount(amountLeft);
            // spawn the entity
            world.spawnEntity(new ItemEntity(world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), newItem));
        }
    }

    public void insertStacks(List<ItemStack> stacks) {
        for(ItemStack stack : stacks) insertStack(stack);
    }

    public void removeItemStack(ItemStack inputStack) {
        for(int i = 0; i < size() && inputStack.getCount() > 0; i++) {
            ItemStack currentStack = getStack(i);
            // if the items are the same
            if(ScreenHandler.canStacksCombine(currentStack, inputStack)) {
                // the amount to remove is either the entirety of the slot or the rest of the input amount
                int amount = Math.min(currentStack.getCount(), inputStack.getCount());
                // remove the amount from the current stack
                currentStack.setCount(currentStack.getCount() - amount);
                // update slot
                setStack(i, currentStack.getCount() == 0 ? ItemStack.EMPTY : currentStack);
                // and update the input stack
                inputStack.setCount(inputStack.getCount() - amount);
            }
        }
    }

    @Override
    public List<ItemStack> removeItem(NbtItem item, int count) {
        ArrayList<ItemStack> itemsRemoved = new ArrayList<>();
        for(int i = 0; i < size() && count > 0; i++) {
            ItemStack currentStack = getStack(i);
            // if the items are the same
            if(item.matches(currentStack, NbtMatcher.Type.ITEM)) {
                // the amount to remove is either the entirety of the slot or the rest of the input amount
                int amount = Math.min(currentStack.getCount(), count);

                // add item removed to list
                itemsRemoved.add(ItemStackHelper.setCount(currentStack.copy(), amount));
                // remove the amount from the current stack
                currentStack.setCount(currentStack.getCount() - amount);

                // update the count
                count -= amount;
            }
        }
        return itemsRemoved;
    }

    @Deprecated
    public int countItem(Item item) {
        int amount = 0;
        for(int i = 0; i < getLimit(); i++) {
            ItemStack currentStack = getStack(i);
            if(currentStack.getItem().equals(item)) amount += currentStack.getCount();
        }
        return amount;
    }

    @Override
    public int countItemStack(ItemStack input) {
        int amount = 0;
        for(int i = 0; i < getLimit(); i++) {
            ItemStack currentStack = getStack(i);
            if(!currentStack.isEmpty() && ScreenHandler.canStacksCombine(currentStack, input)) amount += currentStack.getCount();
        }
        return amount;
    }

    @Override
    public int countItem(NbtItem input) {
        int amount = 0;
        for(int i = 0; i < getLimit(); i++) {
            ItemStack currentStack = getStack(i);
            if(!currentStack.isEmpty() && input.matches(currentStack, NbtMatcher.Type.ITEM)) amount += currentStack.getCount();
        }
        return amount;
    }

    @Deprecated
    public int countSpace(Item item) {
        int amount = 0;
        for(int i = 0; i < getLimit(); i++) {
            ItemStack stack = getStack(i);
            if(stack.isEmpty()) amount += item.getMaxCount(); // if slot is empty, add max count
            else if(stack.getItem().equals(item)) amount += item.getMaxCount() - stack.getCount(); // if items are the same, add items left in stack
        }
        return amount;
    }

    @Override
    public int countSpaceForStack(ItemStack input) {
        int amount = 0;
        for(int i = 0; i < getLimit(); i++) {
            ItemStack stack = getStack(i);
            if(stack.isEmpty()) amount += input.getMaxCount(); // if slot is empty, add max count
            else if(ScreenHandler.canStacksCombine(stack, input)) amount += input.getMaxCount() - stack.getCount(); // if items are the same, add items left in stack
        }
        return amount;
    }

    @Inject(method = "onClose", at = {@At("HEAD")})
    private void close(PlayerEntity player, CallbackInfo ci) {
        // remove all limit and limited items from player
        for(int i = 0; i < 41; i++) {
            if(EconomyItems.ChestShop.LIMIT.matches(player.inventory.getStack(i))) player.inventory.removeStack(i);
            if(EconomyItems.ChestShop.LIMITED.matches(player.inventory.getStack(i))) player.inventory.removeStack(i);
        }

        // if the inventory doesn't have a limiter, reset it
        if(isChestShop() && getLimit() == null) setLimit(size() - 1);
    }

    // bro you could just save data to the block itself
    // i am *massive* brain
    @Inject(method = "toTag", at = {@At("HEAD")})
    private void save(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        if(isChestShop()) {
            CompoundTag chestShopTag = new CompoundTag();

            CompoundTag signTag = new CompoundTag();
            signTag.putInt("x", sign.getX());
            signTag.putInt("y", sign.getY());
            signTag.putInt("z", sign.getZ());
            chestShopTag.put("sign", signTag);

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

            CompoundTag signTag = chestShopTag.getCompound("sign");
            sign = new BlockPos(signTag.getInt("x"), signTag.getInt("y"), signTag.getInt("z"));
        }
    }
}
package mods.banana.economy2.mixins.block;

import mods.banana.economy2.chestshop.ChestInterface;
import mods.banana.economy2.chestshop.SignInterface;
import mods.banana.economy2.EconomyItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ChestBlockEntity.class)
public abstract class ChestEntityMixin extends LootableContainerBlockEntity implements ChestInterface {
    @Shadow public abstract int size();
//    @Shadow private DefaultedList<ItemStack> inventory;

    //    @Shadow private DefaultedList<ItemStack> inventory;
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
            if(EconomyItems.LIMITED.sameIdentifierAs(getStack(i))) setStack(i, ItemStack.EMPTY);
            if(EconomyItems.LIMIT.sameIdentifierAs(getStack(i))) setStack(i, ItemStack.EMPTY);
        }
    }

    public ChestEntityMixin(BlockEntityType<?> type) { super(type); }

    public boolean isChestShop() { return chestShop; }
    public UUID getParent() { return parent; }
    public SignInterface getSign() { return (SignInterface) world.getBlockEntity(sign); }

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
        for(int i = inventory.size() - 1; i > index; i--) {
            if(inventory.getStack(i) != ItemStack.EMPTY && !EconomyItems.LIMITED.sameIdentifierAs(inventory.getStack(i))) insertItemStack(inventory.getStack(i));
            inventory.setStack(i, EconomyItems.LIMITED.getItemStack());
        }

        inventory.markDirty();
    }

    public Integer getLimit() {
//        Inventory inventory = (Inventory) this;
        for(int i = 0; i < size(); i++) {
            if(EconomyItems.LIMIT.sameIdentifierAs(getStack(i))) return i;
        }
        setLimit(size() - 1);
        return getLimit();
    }

    public void insertItemStack(ItemStack input) {
        Item item = input.getItem();

        // for each slot
        for(int i = 0; i < size() && input.getCount() > 0; i++) {
            ItemStack stack = getStack(i);
            if(
                    stack.isEmpty() || // if slot is empty
                            (stack.getItem() == item && stack.getCount() < stack.getMaxCount()) // or the slot is the same item and has space
            ) {
                // get count
                int count = Math.min(input.getCount() + stack.getCount(), input.getMaxCount()) - stack.getCount();
                // add count to item stack
                ItemStack newStack = new ItemStack(item, count + stack.getCount());
                setStack(i, newStack);
                // remove count from input
                input.setCount(input.getCount() - count);
            }
        }

        if(input.getCount() > 0) world.spawnEntity(new ItemEntity(world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), input));
    }

    public void removeItemStack(ItemStack inputStack) {
        Item item = inputStack.getItem();
        for(int i = 0; i < size() && inputStack.getCount() > 0; i++) {
            ItemStack currentStack = getStack(i);
            if(currentStack.getItem().equals(item)) {
                int amount = Math.min(currentStack.getCount(), inputStack.getCount());
                currentStack.setCount(currentStack.getCount() - amount);
                inputStack.setCount(inputStack.getCount() - amount);
            }
        }
    }

    public int countItem(Item item) {
        int amount = 0;
        for(int i = 0; i < getLimit(); i++) {
            ItemStack currentStack = getStack(i);
            if(currentStack.getItem().equals(item)) amount += currentStack.getCount();
        }
        return amount;
    }

    public int countSpace(Item item) {
        int amount = 0;
        for(int i = 0; i < getLimit(); i++) {
            ItemStack stack = getStack(i);
            if(stack.isEmpty()) amount += item.getMaxCount(); // if slot is empty, add max count
            else if(stack.getItem().equals(item)) amount += item.getMaxCount() - stack.getCount(); // if items are the same, add items left in stack
        }
        return amount;
    }

    @Inject(method = "onClose", at = {@At("HEAD")})
    private void close(PlayerEntity player, CallbackInfo ci) {
        // remove all limit and limited items from player
        for(int i = 0; i < 41; i++) {
            if(EconomyItems.LIMIT.sameIdentifierAs(player.inventory.getStack(i))) player.inventory.removeStack(i);
            if(EconomyItems.LIMITED.sameIdentifierAs(player.inventory.getStack(i))) player.inventory.removeStack(i);
        }

        // if the inventory doesn't have a limiter, reset it
        if(chestShop && getLimit() == null) setLimit(size() - 1);
    }

    // bro you could just save data to the block itself
    // i am *massive* brain
    @Inject(method = "toTag", at = {@At("HEAD")})
    private void save(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        if(chestShop) {
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
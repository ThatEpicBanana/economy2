package mods.banana.economy2.mixins.block;

import mods.banana.bananaapi.BlockPosHelper;
import mods.banana.economy2.ItemStackUtil;
import mods.banana.economy2.balance.OfflinePlayer;
import mods.banana.economy2.chestshop.ChestShopItem;
import mods.banana.economy2.chestshop.interfaces.ChestInterface;
import mods.banana.economy2.chestshop.interfaces.HopperInterface;
import mods.banana.economy2.chestshop.interfaces.SignInterface;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(HopperBlockEntity.class)
public abstract class HopperEntityMixin extends LootableContainerBlockEntity implements Hopper, Tickable, HopperInterface {
    @Shadow public abstract int size();

    private boolean autoSell;
    private BlockPos chestShop;
    private UUID parent;

    protected HopperEntityMixin(BlockEntityType<?> blockEntityType) { super(blockEntityType); }

    public void setAutoSell(boolean autoSell) { this.autoSell = autoSell; }
    public boolean isAutoSell() { return autoSell; }

    public void setChestShop(BlockPos chestShop) { this.chestShop = chestShop; }
    public BlockPos getChestShopPos() { return chestShop; }
    public SignInterface getChestShop() { return (SignInterface) world.getBlockEntity(chestShop); }

    public UUID getParent() { return parent; }
    public void setParent(UUID parent) { this.parent = parent; }

    public int countItem(ChestShopItem input) {
        int amount = 0;
        for(int i = 0; i < size(); i++) {
            ItemStack currentStack = getStack(i);
            if(!currentStack.isEmpty() && input.matches(currentStack)) amount += currentStack.getCount();
        }
        return amount;
    }

    public List<ItemStack> removeItem(ChestShopItem item, int count) {
        ArrayList<ItemStack> itemsRemoved = new ArrayList<>();
        for(int i = 0; i < size() && count > 0; i++) {
            ItemStack currentStack = getStack(i);
            // if the items are the same
            if(item.matches(currentStack)) {
                // the amount to remove is either the entirety of the slot or the rest of the input amount
                int amount = Math.min(currentStack.getCount(), count);

                // add item removed to list
                itemsRemoved.add(ItemStackUtil.setCount(currentStack.copy(), amount));
                // remove the amount from the current stack
                currentStack.setCount(currentStack.getCount() - amount);

                // update the count
                count -= amount;
            }
        }
        return itemsRemoved;
    }

    @Inject(method = {"transfer(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/item/ItemStack;ILnet/minecraft/util/math/Direction;)Lnet/minecraft/item/ItemStack;"}, at = { @At("HEAD") }, cancellable = true)
    private static void onExtract(Inventory from, Inventory to, ItemStack stack, int slot, Direction direction, CallbackInfoReturnable<ItemStack> cir) {
        if(from instanceof ChestBlockEntity) {
            // prevent stealing from chest shops
            if(((ChestInterface)from).isChestShop()) cir.setReturnValue(stack);
        } else if(from instanceof HopperBlockEntity) {
            // get hopper interface
            HopperInterface hopperInterface = (HopperInterface) from;
            // check if hopper is auto sell
            if(hopperInterface.isAutoSell()) {
                // get chest shop
                SignInterface chestShop = hopperInterface.getChestShop();
                // make sure item transferred is not the item the hopper is selling
                if(chestShop.getItem().matches(stack)) cir.setReturnValue(stack);
            }
        }
    }

    @Inject(method = "transfer(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/item/ItemStack;ILnet/minecraft/util/math/Direction;)Lnet/minecraft/item/ItemStack;", at = @At("TAIL"))
    private static void afterExtract(Inventory from, Inventory to, ItemStack stack, int slot, Direction direction, CallbackInfoReturnable<ItemStack> cir) {
        if(to instanceof HopperBlockEntity) {
            // get hopper interface
            HopperInterface hopperInterface = (HopperInterface) to;
            // if hopper interface is auto sell, pass the interface and it's parent to it's associated chest shop
            if(hopperInterface.isAutoSell()) hopperInterface.getChestShop().onSell(hopperInterface, OfflinePlayer.getPlayer(hopperInterface.getParent()));
        }
    }

    @Inject(method = "toTag", at = @At("TAIL"))
    private void onSave(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        // {autoSell:{shop:{x:0,y:0,z:0}, parent:[I; 0,0,0,0]}}
        if(autoSell) {
            CompoundTag autoSellTag = new CompoundTag();
            autoSellTag.put("shop", BlockPosHelper.toTag(chestShop));
            autoSellTag.putUuid("parent", parent);
            tag.put("autoSell", autoSellTag);
        }
    }

    @Inject(method = "fromTag", at = @At("TAIL"))
    private void onLoad(BlockState state, CompoundTag tag, CallbackInfo ci) {
        if(tag.contains("autoSell")) {
            autoSell = true;
            chestShop = BlockPosHelper.fromTag(tag.getCompound("autoSell").getCompound("shop"));
            parent = tag.getCompound("autoSell").getUuid("parent");
        }
    }
}

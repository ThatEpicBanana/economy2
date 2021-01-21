package mods.banana.economy2.chestshop;

import mods.banana.economy2.itemmodules.ItemModuleHandler;
import mods.banana.economy2.itemmodules.NbtItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ChestShopItem {
    protected final Item item;

    public ChestShopItem(Item item) {
        this.item = item;
    }

    public static ChestShopItem fromStack(ItemStack itemStack) {
        NbtItem nbtItem = NbtItem.fromStack(itemStack);
        if(nbtItem != null) return nbtItem;
        else return new ChestShopItem(itemStack.getItem());
    }

    public static ChestShopItem fromIdentifier(Identifier identifier) {
        NbtItem nbtItem = ItemModuleHandler.getActiveItem(identifier);
        if(nbtItem != null) return nbtItem;
        else return new ChestShopItem(Registry.ITEM.get(identifier));
    }

    public boolean matches(ItemStack itemStack) {
        return itemStack.getItem().equals(item) && !itemStack.hasTag();
    }

    public ItemStack toItemStack() {
        return new ItemStack(getItem());
    }

    public Item getItem() { return item; }
}

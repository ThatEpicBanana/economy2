package mods.banana.economy2.itemmodules.items;

import mods.banana.economy2.itemmodules.ItemModuleHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BaseNbtItem {
    protected final Item item;

    public BaseNbtItem(Item item) {
        this.item = item;
    }

    public static BaseNbtItem fromStack(ItemStack itemStack) {
        NbtItem nbtItem = NbtItem.fromStack(itemStack);
        if(nbtItem != null) return nbtItem;
        else return new BaseNbtItem(itemStack.getItem());
    }

    public static BaseNbtItem fromIdentifier(Identifier identifier) {
        NbtItem nbtItem = ItemModuleHandler.getActiveItem(identifier);
        if(nbtItem != null) return nbtItem;
        else return new BaseNbtItem(Registry.ITEM.get(identifier));
    }

    public boolean matches(ItemStack itemStack) {
        return itemStack.getItem().equals(item) && !itemStack.hasTag();
    }

    public ItemStack toItemStack() {
        return new ItemStack(getItem());
    }

    public Item getItem() { return item; }
}

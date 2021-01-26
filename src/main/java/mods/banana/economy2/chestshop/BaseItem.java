package mods.banana.economy2.chestshop;

import mods.banana.economy2.chestshop.interfaces.ChestShopItem;
import mods.banana.economy2.itemmodules.ItemModuleHandler;
import mods.banana.economy2.itemmodules.items.NbtItem;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BaseItem implements ChestShopItem {
    protected final Item item;

    public BaseItem(Item item) {
        this.item = item;
    }

    public static ChestShopItem fromStack(ItemStack itemStack) {
        NbtItem nbtMatcher = NbtItem.fromStack(itemStack);
        if(nbtMatcher != null) return nbtMatcher;
        else return new BaseItem(itemStack.getItem());
    }

    public static ChestShopItem fromIdentifier(Identifier identifier) {
        NbtMatcher nbtItem = ItemModuleHandler.getActiveMatcher(identifier);
        if(nbtItem instanceof NbtItem) return (ChestShopItem) nbtItem;
        else return new BaseItem(Registry.ITEM.get(identifier));
    }

    public boolean matches(ItemStack itemStack) {
        return itemStack.getItem().equals(item) && !itemStack.hasTag();
    }

    public ItemStack toItemStack() {
        return new ItemStack(getItem());
    }

    public Item getItem() { return item; }
}

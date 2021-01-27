package mods.banana.economy2.chestshop;

import mods.banana.bananaapi.helpers.PredicateHelper;
import mods.banana.economy2.itemmodules.ItemModuleHandler;
import mods.banana.economy2.itemmodules.interfaces.mixin.ConditionInterface;
import mods.banana.economy2.itemmodules.items.NbtItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ChestShopItem {
    protected final Item item;
    private final LootCondition tag;

    public ChestShopItem(Item item) {
        this.item = item;
        this.tag = null;
    }

    public ChestShopItem(Item item, LootCondition tag) {
        this.item = item;
        this.tag = tag;
    }

    public ChestShopItem(NbtItem item) {
        this(item.getItem(), item.getPredicate());
    }

    public static ChestShopItem fromStack(ItemStack itemStack) {
        NbtItem nbtItem = NbtItem.fromStack(itemStack);
        if(nbtItem != null) return new ChestShopItem(nbtItem);
        else return new ChestShopItem(itemStack.getItem());
    }

    public static ChestShopItem fromIdentifier(Identifier identifier) {
        NbtItem nbtItem = ItemModuleHandler.getActiveItem(identifier);
        if(nbtItem != null) return new ChestShopItem(nbtItem);
        else return new ChestShopItem(Registry.ITEM.get(identifier));
    }

    public boolean matches(ItemStack itemStack) {
        return
                itemStack.getItem().equals(item) && ( // check item
                        tag == null ? !itemStack.hasTag() : PredicateHelper.test(tag, itemStack) // check tag
                );
    }

    public ItemStack toItemStack() {
        ItemStack stack = new ItemStack(getItem());
        if(tag != null) stack.setTag(((ConditionInterface)tag).getTag());
        return stack;
    }

    public Item getItem() { return item; }
    public LootCondition getTag() { return tag; }
}

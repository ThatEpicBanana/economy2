package mods.banana.economy2.chestshop;

import mods.banana.bananaapi.helpers.PredicateHelper;
import mods.banana.economy2.itemmodules.ItemModuleHandler;
import mods.banana.economy2.itemmodules.interfaces.mixin.ConditionInterface;
import mods.banana.economy2.itemmodules.items.NbtItem;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BaseItem {
    protected final Item item;
    private final LootCondition tag;

    public BaseItem(Item item) {
        this.item = item;
        this.tag = null;
    }

    public BaseItem(Item item, LootCondition tag) {
        this.item = item;
        this.tag = tag;
    }

    public BaseItem(NbtItem item) {
        this(item.getItem(), item.getPredicate());
    }

    public static BaseItem fromStack(ItemStack itemStack) {
        NbtItem nbtItem = NbtItem.fromStack(itemStack);
        if(nbtItem != null) return new BaseItem(nbtItem);
        else return new BaseItem(itemStack.getItem());
    }

    public static BaseItem fromIdentifier(Identifier identifier) {
        NbtItem nbtItem = (NbtItem) ItemModuleHandler.getActiveMatcher(identifier, NbtMatcher.Type.ITEM);
        if(nbtItem != null) return new BaseItem(nbtItem);
        else {
            Item item = Registry.ITEM.getOrEmpty(identifier).orElse(null);
            return item != null ? new BaseItem(item) : null;
        }
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

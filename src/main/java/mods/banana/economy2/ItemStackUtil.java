package mods.banana.economy2;

import net.minecraft.item.ItemStack;

public class ItemStackUtil {
    public static ItemStack setCount(ItemStack itemStack, int count) {
        itemStack.setCount(count);
        return itemStack;
    }
}

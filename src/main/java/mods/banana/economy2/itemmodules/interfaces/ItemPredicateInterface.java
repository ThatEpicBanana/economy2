package mods.banana.economy2.itemmodules.interfaces;

import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.tag.Tag;
import org.jetbrains.annotations.Nullable;

public interface ItemPredicateInterface {
    Tag<Item> getTag();
    @Nullable Item getItem();

    NbtPredicateInterface getNbt();
    EnchantmentPredicateInterface[] getEnchantments();
    EnchantmentPredicateInterface[] getStoredEnchantments();
    Potion getPotion();
}

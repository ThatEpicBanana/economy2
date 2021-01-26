package mods.banana.economy2.itemmodules.interfaces.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.predicate.NumberRange;

public interface EnchantmentPredicateInterface {
    Enchantment getEnchantment();
    NumberRange.IntRange getLevels();
}

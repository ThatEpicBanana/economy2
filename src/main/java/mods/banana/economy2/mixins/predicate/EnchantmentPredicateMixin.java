package mods.banana.economy2.mixins.predicate;

import mods.banana.economy2.itemmodules.interfaces.EnchantmentPredicateInterface;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EnchantmentPredicate.class)
public class EnchantmentPredicateMixin implements EnchantmentPredicateInterface {
    @Shadow @Final private Enchantment enchantment;
    @Shadow @Final private NumberRange.IntRange levels;

    public Enchantment getEnchantment() { return enchantment; }
    public NumberRange.IntRange getLevels() { return levels; }
}

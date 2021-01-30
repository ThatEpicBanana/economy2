package mods.banana.economy2.mixins.predicate;

import mods.banana.economy2.itemmodules.interfaces.EnchantmentPredicateInterface;
import mods.banana.economy2.itemmodules.interfaces.ItemPredicateInterface;
import mods.banana.economy2.itemmodules.interfaces.NbtPredicateInterface;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.tag.Tag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemPredicate.class)
public class ItemPredicateMixin implements ItemPredicateInterface {
    // item stuff
    @Shadow @Final @Nullable private Item item;
    @Shadow @Final @Nullable private Tag<Item> tag;

    // nbt stuff
    @Shadow @Final private NbtPredicate nbt;
    @Shadow @Final private EnchantmentPredicate[] enchantments;
    @Shadow @Final private EnchantmentPredicate[] storedEnchantments;
    @Shadow @Final @Nullable private Potion potion;

    public NbtPredicateInterface getNbt() { return (NbtPredicateInterface) nbt; }
    public @Nullable Item getItem() { return item; }
    public EnchantmentPredicateInterface[] getEnchantments() { return (EnchantmentPredicateInterface[]) enchantments; }
    public EnchantmentPredicateInterface[] getStoredEnchantments() { return (EnchantmentPredicateInterface[]) storedEnchantments; }
    public Potion getPotion() { return potion; }
    public Tag<Item> getTag() { return tag; }
}

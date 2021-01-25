package mods.banana.economy2.mixins.predicate;

import mods.banana.economy2.itemmodules.exceptions.ParseNbtItemPredicateException;
import mods.banana.economy2.itemmodules.interfaces.ItemPredicateInterface;
import mods.banana.economy2.itemmodules.interfaces.MatchToolConditionInterface;
import net.minecraft.item.Item;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MatchToolLootCondition.class)
public abstract class MatchToolConditionMixin implements MatchToolConditionInterface {
    @Shadow @Final private ItemPredicate predicate;

    public ItemPredicateInterface getPredicate() { return (ItemPredicateInterface) predicate; }

    public Pair<Item, CompoundTag> getStack() {
        return new Pair<>(getPredicate().getItem(), getPredicate().getNbt().getTag());
    }
}

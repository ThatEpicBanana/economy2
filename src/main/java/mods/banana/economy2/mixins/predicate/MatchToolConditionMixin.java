package mods.banana.economy2.mixins.predicate;

import mods.banana.economy2.itemmodules.interfaces.ItemPredicateInterface;
import mods.banana.economy2.itemmodules.interfaces.MatchToolConditionInterface;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.item.ItemPredicate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MatchToolLootCondition.class)
public abstract class MatchToolConditionMixin implements MatchToolConditionInterface {
    @Shadow @Final private ItemPredicate predicate;

    public ItemPredicateInterface getPredicate() { return (ItemPredicateInterface) predicate; }

    @Override
    public CompoundTag getTag() {
        return getPredicate().getNbt().getTag();
    }
}

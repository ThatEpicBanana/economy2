package mods.banana.economy2.mixins.predicate;

import mods.banana.bananaapi.helpers.TagHelper;
import mods.banana.economy2.itemmodules.exceptions.ParseNbtItemPredicateException;
import mods.banana.economy2.itemmodules.interfaces.AlternativeConditionInterface;
import mods.banana.economy2.itemmodules.interfaces.ConditionInterface;
import net.minecraft.item.Item;
import net.minecraft.loot.condition.AlternativeLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AlternativeLootCondition.class)
public abstract class AlternativeConditionMixin implements AlternativeConditionInterface {
    @Shadow @Final private LootCondition[] terms;

    public ConditionInterface[] getTerms() { return (ConditionInterface[]) terms; }

    @Override
    public Pair<Item, CompoundTag> getStack() {
        // initialize tag
        CompoundTag tag = new CompoundTag();
        // for each condition
        for(ConditionInterface condition : getTerms()) {
            // get it's stack and combine tags
            tag = TagHelper.combine(tag, condition.getStack().getRight());
        }
        // get first non-null item and return it
        for(ConditionInterface condition : getTerms()) {
            if(condition.getStack().getLeft() != null) return new Pair<>(condition.getStack().getLeft(), tag);
        }
        // if no children has an item, return null
        return new Pair<>(null, tag);
    }
}

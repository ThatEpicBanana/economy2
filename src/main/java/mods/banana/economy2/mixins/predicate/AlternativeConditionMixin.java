package mods.banana.economy2.mixins.predicate;

import mods.banana.bananaapi.helpers.TagHelper;
import mods.banana.economy2.itemmodules.interfaces.AlternativeConditionInterface;
import mods.banana.economy2.itemmodules.interfaces.ConditionInterface;
import net.minecraft.loot.condition.AlternativeLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AlternativeLootCondition.class)
public abstract class AlternativeConditionMixin implements AlternativeConditionInterface {
    @Shadow @Final private LootCondition[] terms;

    public ConditionInterface[] getTerms() { return (ConditionInterface[]) terms; }

    @Override
    public CompoundTag getTag() {
        // initialize tag
        CompoundTag tag = new CompoundTag();
        // for each condition
        for(ConditionInterface condition : getTerms()) {
            // get it's stack and combine tags
            tag = TagHelper.combine(tag, condition.getTag());
        }
        // return tag
        return tag;
    }
}

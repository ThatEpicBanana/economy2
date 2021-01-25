package mods.banana.economy2.mixins.predicate;

import mods.banana.economy2.itemmodules.interfaces.ConditionInterface;
import net.minecraft.item.Item;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LootCondition.class)
public interface ConditionMixin extends ConditionInterface {
    Pair<Item, CompoundTag> getStack();
}

package mods.banana.economy2.mixins.predicate;

import mods.banana.bananaapi.helpers.TagHelper;
import mods.banana.economy2.itemmodules.exceptions.ParseNbtItemPredicateException;
import mods.banana.economy2.itemmodules.interfaces.AlternativeConditionInterface;
import mods.banana.economy2.itemmodules.interfaces.ConditionInterface;
import mods.banana.economy2.itemmodules.interfaces.MatchToolConditionInterface;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LootCondition.class)
public interface ConditionMixin extends ConditionInterface {
    Pair<Item, CompoundTag> getStack();
}

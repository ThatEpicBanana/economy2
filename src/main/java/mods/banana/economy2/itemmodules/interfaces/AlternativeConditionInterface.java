package mods.banana.economy2.itemmodules.interfaces;

import net.minecraft.loot.condition.LootCondition;

public interface AlternativeConditionInterface extends ConditionInterface {
    ConditionInterface[] getTerms();
}

package mods.banana.economy2.itemmodules.interfaces;

import mods.banana.economy2.itemmodules.exceptions.ParseNbtItemPredicateException;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Pair;

public interface ConditionInterface {
    Pair<Item, CompoundTag> getStack();
}

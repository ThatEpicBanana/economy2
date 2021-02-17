package mods.banana.economy2.itemmodules.items.accepts;

import mods.banana.economy2.itemmodules.items.NbtMatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DefaultedAccepts implements MatcherAccepts {
    private final boolean defaultValue;

    public DefaultedAccepts(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean accepts(ItemStack stack) {
        return defaultValue;
    }

    @Override
    public boolean accepts(NbtMatcher matcher, Item baseItem) {
        return defaultValue;
    }

    public boolean getDefaultValue() { return defaultValue; }
}

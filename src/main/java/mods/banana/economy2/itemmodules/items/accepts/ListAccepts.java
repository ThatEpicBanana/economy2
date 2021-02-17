package mods.banana.economy2.itemmodules.items.accepts;

import mods.banana.bananaapi.helpers.PredicateHelper;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.util.Identifier;

import java.util.List;

public class ListAccepts implements MatcherAccepts {
    private final List<Identifier> list;
    private final boolean type;

    /**
     * @param list either the whitelist or blacklist
     * @param type type: true for whitelist, false for blacklist
     */
    public ListAccepts(List<Identifier> list, boolean type) {
        this.list = list;
        this.type = type;
    }

    @Override
    public boolean accepts(ItemStack stack) {
        for(Identifier id : list) {
            LootCondition condition = Economy2.server.getPredicateManager().get(id);
            if(PredicateHelper.test(condition, stack)) return type;
        }
        return !type;
    }

    @Override
    public boolean accepts(NbtMatcher matcher, Item baseItem) {
        ItemStack stack = new ItemStack(baseItem);
        stack.setTag(matcher.getCompoundTag());
        return accepts(stack);
    }

    public List<Identifier> getList() { return list; }
    public boolean getType() { return type; }
}

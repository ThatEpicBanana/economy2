package mods.banana.economy2.bounties;

import mods.banana.economy2.chestshop.BaseItem;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Bounty {
    private final UUID owner;
    private final BaseItem baseItem;
    private final List<NbtMatcher> mustMatch;
    private final List<NbtMatcher> cannotMatch;
    private final int amount;
    private final long price;

    public Bounty(UUID owner, BaseItem baseItem, List<NbtMatcher> mustMatch, int amount, long price) {
        this.owner = owner;
        this.baseItem = baseItem;
        this.mustMatch = mustMatch;
        this.cannotMatch = new ArrayList<>();
        this.amount = amount;
        this.price = price;
    }

    public Bounty(UUID owner, BaseItem baseItem, List<NbtMatcher> mustMatch, List<NbtMatcher> cannotMatch, int amount, long price) {
        this.owner = owner;
        this.baseItem = baseItem;
        this.mustMatch = mustMatch;
        this.cannotMatch = cannotMatch;
        this.amount = amount;
        this.price = price;
    }

    public ItemStack toItemStack() {
        // initialize stack
        ItemStack stack = baseItem.toItemStack();
        // set tag if null
        if(!stack.hasTag()) stack.setTag(new CompoundTag());
        // apply each nbt matcher to stack
        for (NbtMatcher item : mustMatch) item.apply(stack);
        // return the new stack
        return stack;
    }
}

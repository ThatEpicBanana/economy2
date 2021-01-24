package mods.banana.economy2.bounties;

import mods.banana.bananaapi.helpers.TagHelper;
import mods.banana.economy2.itemmodules.items.BaseNbtItem;
import mods.banana.economy2.itemmodules.ItemModuleHandler;
import mods.banana.economy2.itemmodules.items.NbtItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Bounty {
    private final UUID owner;
    private final Identifier baseItem;
    private final List<Identifier> mustMatch;
    private final List<Identifier> cannotMatch;
    private final int amount;
    private final long price;

    public Bounty(UUID owner, Identifier baseItem, List<Identifier> mustMatch, int amount, long price) {
        this.owner = owner;
        this.baseItem = baseItem;
        this.mustMatch = mustMatch;
        this.cannotMatch = new ArrayList<>();
        this.amount = amount;
        this.price = price;
    }

    public Bounty(UUID owner, Identifier baseItem, List<Identifier> mustMatch, List<Identifier> cannotMatch, int amount, long price) {
        this.owner = owner;
        this.baseItem = baseItem;
        this.mustMatch = mustMatch;
        this.cannotMatch = cannotMatch;
        this.amount = amount;
        this.price = price;
    }

    public ItemStack toItemStack() {
        // initialize stack
        ItemStack stack = ItemModuleHandler.getItem(baseItem).toItemStack();
        // set tag
        if(!stack.hasTag()) stack.setTag(new CompoundTag());
        // for each matching nbt item
        for (Identifier itemId : mustMatch) {
            // get the item
            BaseNbtItem item = ItemModuleHandler.getItem(itemId);
            // combine the tags
            if (item instanceof NbtItem) stack.setTag(TagHelper.combine(((NbtItem) item).getTag(), stack.getTag()));
        }
        // return the new stack
        return stack;
    }
}

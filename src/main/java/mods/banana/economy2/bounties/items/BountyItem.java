package mods.banana.economy2.bounties.items;

import mods.banana.economy2.Economy2;
import mods.banana.economy2.bounties.Bounty;
import mods.banana.economy2.items.GuiItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class BountyItem extends GuiItem {
    public BountyItem(Identifier identifier) {
        super(null, identifier, 0, true, null);
    }

    public ItemStack setId(ItemStack stack, UUID uuid) {
        convertTag(stack);
        setCustomValue(stack, "id", NbtHelper.fromUuid(uuid));
        return stack;
    }

    public Bounty getBounty(ItemStack stack) {
        return Economy2.bountyHandler.get(
                NbtHelper.toUuid(
                        getCustomValue(stack, "id")
                )
        );
    }
}

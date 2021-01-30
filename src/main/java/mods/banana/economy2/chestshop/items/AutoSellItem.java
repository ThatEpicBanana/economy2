package mods.banana.economy2.chestshop.items;

import mods.banana.bananaapi.serverItems.ServerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class AutoSellItem extends ServerItem {
    public AutoSellItem(Identifier identifier) {
        super(Items.HOPPER, identifier);
    }

    public void load(ItemStack stack, BlockPos pos, UUID uuid) {
        convert(stack);

        CompoundTag signTag = new CompoundTag();
        signTag.putInt("x", pos.getX());
        signTag.putInt("y", pos.getY());
        signTag.putInt("z", pos.getZ());

        getCustomTag(stack).put("shop", signTag);
        getCustomTag(stack).putUuid("player", uuid);
    }

    public BlockPos getShop(ItemStack stack) {
        CompoundTag shopTag = getCustomTag(stack).getCompound("shop");
        return new BlockPos(shopTag.getInt("x"), shopTag.getInt("y"), shopTag.getInt("z"));
    }
}

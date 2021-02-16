package mods.banana.economy2.chestshop.items;

import mods.banana.bananaapi.itemsv2.CustomItem;
import mods.banana.bananaapi.serverItems.ServerItem;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.UUID;

public class AutoSellItem extends CustomItem {
    public AutoSellItem(Identifier identifier) {
        super(identifier, Items.HOPPER, 0, null, false, new ArrayList<>());
    }

    public void load(ItemStack stack, BlockPos pos, UUID uuid) {
        convert(stack);

        CompoundTag signTag = new CompoundTag();
        signTag.putInt("x", pos.getX());
        signTag.putInt("y", pos.getY());
        signTag.putInt("z", pos.getZ());

        stack = toBuilder(stack, true)
                .customValue("shop" ,signTag)
                .customValue("player", NbtHelper.fromUuid(uuid))
                .build();

//        getCustomTag(stack).put("shop", signTag);
//        getCustomTag(stack).putUuid("player", uuid);
    }

    public BlockPos getShop(ItemStack stack) {
        CompoundTag shopTag = (CompoundTag) toReader(stack).getCustomValue("shop", NbtType.COMPOUND);
        return new BlockPos(shopTag.getInt("x"), shopTag.getInt("y"), shopTag.getInt("z"));
    }
}

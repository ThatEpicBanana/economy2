package mods.banana.bananaapi.serverItems;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public abstract class ServerItem {
    protected ItemConvertible parent;
    protected Identifier identifier;
    protected CompoundTag tag;

    /**
     * @param count amount of items in stack
     * @return item stack of server item
     */
    public abstract ItemStack getItemStack(int count);

    /**
     * @return item stack with count 1 of server item
     */
    public ItemStack getItemStack() { return getItemStack(1); }

    /**
     * executes when item used
     * @param itemStack item stack of used item
     * @return if use action should be cancelled
     */
    public boolean onUse(ItemStack itemStack) {
        return false;
    }

    /**
     * executes when item entity of this type is spawned
     * @param itemStack item stack of spawned item
     * @return if spawn action should be cancelled
     */
    public boolean onItemEntitySpawn(ItemStack itemStack) {
        return false;
    }
}

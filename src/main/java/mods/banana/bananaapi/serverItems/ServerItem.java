package mods.banana.bananaapi.serverItems;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public abstract class ServerItem {
    public static ArrayList<ServerItem> items = new ArrayList<>();

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

    public Identifier getIdentifier() { return identifier; }

    public ItemConvertible getParent() { return parent; }

    /**
     * executes when item used
     * @param itemStack item stack of used item
     * @return if use action should be cancelled
     */
    public boolean onUse(ItemStack itemStack, ServerPlayerEntity player, int slot) { return false; }

    /**
     * executes when item entity of this type is spawned
     * @param itemStack item stack of spawned item
     * @return if spawn action should be cancelled
     */
    public boolean onItemEntitySpawn(ItemStack itemStack) {
        return false;
    }

    public boolean sameIdentifierAs(ItemStack stack) {
        if(stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if(tag.contains(identifier.getNamespace())) {
                return tag.getCompound(identifier.getNamespace()).getString("type").equals(identifier.getPath());
            }
        }
        return false;
    }
}

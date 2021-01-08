package mods.banana.bananaapi.serverItems;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SimpleItem extends ServerItem {
    public SimpleItem(ItemConvertible parent, Identifier identifier) {
        this.parent = parent;
        this.identifier = identifier;
        this.tag = new CompoundTag();
    }

    /**
     * Sets the item's customModelData to the input number
     * @param customModelData new data
     * @return the updated item
     */
    public SimpleItem withCustomModelData(int customModelData) {
        tag.putInt("CustomModelData", customModelData);
        return this;
    }

    /**
     * Sets the item's name to the input text
     * @param name new name
     * @return the updated item
     */
    public SimpleItem withName(Text name) {
        CompoundTag displayTag = new CompoundTag();
        displayTag.putString("Name", Text.Serializer.toJson(name));

        tag.put("display", displayTag);

        return this;
    }

    @Override
    public ItemStack getItemStack(int count) {
        CompoundTag moduleTag = new CompoundTag();
        moduleTag.putString("type", identifier.getPath());

        tag.put(identifier.getNamespace(), moduleTag);

        ItemStack stack = new ItemStack(parent, count);
        stack.setTag(tag);

        return stack;
    }

    @Override
    public boolean onItemEntitySpawn(ItemStack itemStack) { return true; }

    @Override
    public boolean onUse(ItemStack itemStack, ServerPlayerEntity player, int slot) {
        return true;
    }
}

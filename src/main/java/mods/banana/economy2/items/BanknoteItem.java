package mods.banana.economy2.items;

import mods.banana.bananaapi.serverItems.SimpleItem;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.interfaces.PlayerInterface;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class BanknoteItem extends SimpleItem {
    public BanknoteItem(Identifier identifier) {
        super(Items.PAPER, identifier);
        withCustomModelData(1);
        withName(new LiteralText("Banknote").formatted(Formatting.GREEN));
    }

    @Override
    public boolean onUse(ItemStack itemStack, ServerPlayerEntity player, int slot) {
        long amount = tag.getCompound("economy").getLong("amount");

        System.out.println("banknote used");

        player.sendSystemMessage(new LiteralText("Deposited " + Economy2.addCurrencySign(amount)), UUID.randomUUID());
        ((PlayerInterface)player).addBal(amount);
        player.inventory.removeStack(slot);
        return true;
    }
}

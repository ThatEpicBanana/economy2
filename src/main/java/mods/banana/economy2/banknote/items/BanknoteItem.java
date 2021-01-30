package mods.banana.economy2.banknote.items;

import mods.banana.bananaapi.serverItems.ServerItem;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.balance.PlayerInterface;
import mods.banana.economy2.trade.TradePlayerInterface;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class BanknoteItem extends ServerItem {
    public BanknoteItem(Identifier identifier) {
        super(Items.PAPER, identifier, new LiteralText("Banknote").formatted(Formatting.GREEN));
    }

    @Override
    public boolean onUse(ItemStack itemStack, ServerPlayerEntity player, int slot) {
        if(((TradePlayerInterface)player).getTrade() != null) return false;

        long amount = tag.getCompound("economy").getLong("amount");

        player.sendSystemMessage(new LiteralText("Deposited " + Economy2.addCurrencySign(amount)), UUID.randomUUID());
        ((PlayerInterface)player).addBal(amount);
        player.inventory.removeStack(slot);
        player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1, 1);
        return true;
    }
}

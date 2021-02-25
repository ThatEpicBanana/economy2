package mods.banana.economy2.banknote.items;

import mods.banana.bananaapi.itemsv2.CustomItem;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.EconomyItems;
import mods.banana.economy2.balance.PlayerInterface;
import mods.banana.economy2.trade.TradePlayerInterface;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BanknoteItem extends CustomItem {
    public BanknoteItem(Identifier identifier) {
        super(identifier, Items.PAPER, new LiteralText("Banknote").formatted(Formatting.GREEN), new ArrayList<>(Collections.singleton(Tag.USE)));
    }

    @Override
    public TypedActionResult<ItemStack> interact(PlayerEntity player, World world, Hand hand) {
        if(player instanceof ClientPlayerEntity) return TypedActionResult.pass(ItemStack.EMPTY);

        if(EconomyItems.Banknote.BANKNOTE.matches(player.getStackInHand(hand))) {
            if(((TradePlayerInterface)player).getTrade() != null) return TypedActionResult.pass(ItemStack.EMPTY);

            ItemStack stack = player.getStackInHand(hand);
            CompoundTag tag = stack.getOrCreateTag();

            long amount = tag.getCompound("economy").getLong("amount");

            player.sendSystemMessage(new LiteralText("Deposited " + Economy2.addCurrencySign(amount)), UUID.randomUUID());
            ((PlayerInterface)player).addBal(amount);
            player.inventory.removeStack(hand == Hand.OFF_HAND ? 41 : player.inventory.selectedSlot);
            player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1, 1);

            return TypedActionResult.success(ItemStack.EMPTY);
        }

        return TypedActionResult.pass(ItemStack.EMPTY);
    }
}

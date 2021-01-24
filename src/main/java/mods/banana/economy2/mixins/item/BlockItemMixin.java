package mods.banana.economy2.mixins.item;

import mods.banana.bananaapi.helpers.BlockPosHelper;
import mods.banana.bananaapi.helpers.TextHelper;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.EconomyItems;
import mods.banana.economy2.chestshop.interfaces.ChestInterface;
import mods.banana.economy2.chestshop.interfaces.SignInterface;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void onUse(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        // return client call
        if(context.getWorld().isClient) return;

        // get stack used and the player that used it
        ItemStack stack = context.getStack();
        ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();

        // check if player is sneaking with hopper in hand
        if(player.isSneaking() && stack.getItem().equals(Items.HOPPER)) {
            // get block
            BlockPos pos = context.getBlockPos();
            BlockEntity block = context.getWorld().getBlockEntity(pos);
            // check if item used is a sign that's part of a chest shop, and if it allows for selling
            if(block instanceof SignBlockEntity && ((SignInterface)block).isChestShop() && ((SignInterface)block).getSell() != -1) {
                // convert item
                EconomyItems.AUTOSELL.load(stack, context.getBlockPos(), player.getUuid());

                // add lore ex: Autosell: 0, 0, 0
                stack.getTag().put("display", TextHelper.getLore(new LiteralText("Autosell: " + BlockPosHelper.toString(pos)).formatted(Formatting.GOLD)));

                // alert player
                player.playSound(SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.BLOCKS, 1F, 1.5F);
                player.sendSystemMessage(new LiteralText("Created new autosell hopper selling at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()).formatted(Formatting.GREEN), UUID.randomUUID());

                // return place action, don't want player to accidentally place hopper
                cir.setReturnValue(ActionResult.FAIL);
            }
        }
    }
}

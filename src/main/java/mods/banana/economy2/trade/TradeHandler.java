package mods.banana.economy2.trade;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;

import java.util.ArrayList;

public class TradeHandler {
    public static ArrayList<TradeInstance> requests = new ArrayList<>();
    public static ArrayList<TradeInstance> trades = new ArrayList<>();

    public static void onInit() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            // return if this is a client call or the player is not in a trade
            if(world.isClient) return TypedActionResult.pass(ItemStack.EMPTY);

            // get player interface
            TradePlayerInterface playerInterface = (TradePlayerInterface) player;
            // if player is in trade
            if(playerInterface.getTrade() != null) {
                // add stack to player's trade list
                playerInterface.getTradeItems().add(player.getStackInHand(hand));
                // remove stack from player
                player.inventory.removeStack(hand == Hand.MAIN_HAND ? player.inventory.selectedSlot : 40);

                player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1, 1);

                // send update message
                playerInterface.getTrade().sendUpdateMessage(true);
            }

            // return
            return TypedActionResult.pass(ItemStack.EMPTY);
        });

        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer) -> {
            for(TradeInstance trade : requests) {
                trade.timer += 1;
                if(trade.timer > (60*20)) {
                    trade.sendTimeoutMessage();
                    requests.remove(trade);
                    break;
                }
            }
            for(TradeInstance trade : trades) {
                trade.timer += 1;
                if(trade.timer == TradeInstance.confirmTime) {
                    trade.sendUpdateMessage(false);
                }
            }
        });
    }
}

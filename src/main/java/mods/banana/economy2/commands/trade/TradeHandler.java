package mods.banana.economy2.commands.trade;

import mods.banana.economy2.interfaces.PlayerInterface;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;

import java.util.ArrayList;

public class TradeHandler {
    public ArrayList<TradeInstance> requests = new ArrayList<>();
    public ArrayList<TradeInstance> trades = new ArrayList<>();

    public void onLoad() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            // return if this is a client call or the player is not in a trade
            if(world.isClient) return TypedActionResult.pass(ItemStack.EMPTY);

            // get player interface
            PlayerInterface playerInterface = (PlayerInterface) player;
            // if player is in trade
            if(playerInterface.getTrade() != null) {
                // add stack to player's trade list
                playerInterface.getTradeItems().add(player.getStackInHand(hand));
                // remove stack from player
                player.inventory.removeStack(hand == Hand.MAIN_HAND ? player.inventory.selectedSlot : 40);

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

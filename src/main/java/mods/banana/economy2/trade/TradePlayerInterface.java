package mods.banana.economy2.trade;

import mods.banana.economy2.balance.PlayerInterface;
import mods.banana.economy2.trade.TradeInstance;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public interface TradePlayerInterface extends PlayerInterface {
    TradeInstance getTrade();
    void setTrade(TradeInstance tradeInstance);

    ArrayList<ItemStack> getTradeItems();
    void resetTrade();

    boolean getAccepted();
    void setAccepted(boolean accepted);
}

package mods.banana.economy2.interfaces;

import mods.banana.economy2.commands.trade.TradeInstance;
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

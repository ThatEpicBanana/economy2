package mods.banana.economy2.interfaces;

import mods.banana.economy2.commands.trade.TradeInstance;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public interface PlayerInterface {
    long getBal();
    void setBal(long value);
    void addBal(long amount);

    String getBalString();

    TradeInstance getTrade();
    void setTrade(TradeInstance tradeInstance);

    ArrayList<ItemStack> getTradeItems();
    void resetTrade();

    void setAccepted(boolean value);
    boolean getAccepted();

    void onConnect();
    void onDisconnect();

    void save();
}

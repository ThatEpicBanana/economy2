package mods.banana.economy2.interfaces;

import mods.banana.economy2.commands.trade.TradeInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public interface PlayerInterface {
    long getBal();
    void setBal(long value);
    void addBal(long amount);

    String getBalAsString();

    void save();
    void reset(String player);
}

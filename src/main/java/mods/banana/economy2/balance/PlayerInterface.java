package mods.banana.economy2.balance;

public interface PlayerInterface {
    long getBal();
    void setBal(long value);
    void addBal(long amount);

    String getBalAsString();

    String getPlayerName();

    void save();
    void reset(String playerName);
}

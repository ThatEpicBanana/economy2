package mods.banana.economy2.interfaces;

public interface PlayerInterface {
    long getBal();
    void setBal(long value);
    void addBal(long amount);

    String getBalString();

    void onConnect();
    void onDisconnect();

    void save();
}

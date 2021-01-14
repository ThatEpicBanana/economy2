package mods.banana.economy2.balance;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mods.banana.economy2.Economy2;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;
import java.util.UUID;

public class OfflinePlayer implements PlayerInterface {
    private final UUID uuid;
    private final String playerName;
    private long bal;

    OfflinePlayer(UUID uuid) {
        this.uuid = uuid;
        this.playerName = Economy2.BalanceJson.get(uuid.toString()).getAsJsonObject().get("playerName").getAsString();
        this.bal = Economy2.BalanceJson.get(uuid.toString()).getAsJsonObject().get("bal").getAsLong();
    }

    public long getBal() {
        return bal;
    }
    public void setBal(long value) { bal = value; save(); }
    public void addBal(long amount) { bal += amount; save(); }

    public String getBalAsString() { return Economy2.addCurrencySign(bal); }

    public String getPlayerName() { return playerName; }

    @Override
    public void save() {
        JsonObject player = new JsonObject();
        player.addProperty("playerName", playerName);
        player.addProperty("bal", bal);

        Economy2.BalanceJson.remove(uuid.toString());
        Economy2.BalanceJson.add(uuid.toString(), player);
    }

    public void reset(String player) {
        bal = (long) Economy2.CONFIG.getValue("player.startingBalance", Integer.class);
        save();
    }

    public static PlayerInterface fromString(String player) {
        for(Map.Entry<String, JsonElement> playerInstance : Economy2.BalanceJson.entrySet()) {
            if(playerInstance.getValue().getAsJsonObject().get("playerName").getAsString().equals(player)) {
                return getPlayer(UUID.fromString(playerInstance.getKey()));
            }
        }
        return null;
    }

    public static PlayerInterface getPlayer(UUID uuid) {
        // get player from server
        ServerPlayerEntity player = Economy2.server.getPlayerManager().getPlayer(uuid);
        // if player is in server, return the interface of that player
        if(player != null) return (PlayerInterface) player;
        // if player isn't in server, return a new offline player instance
        else return new OfflinePlayer(uuid);
    }
}

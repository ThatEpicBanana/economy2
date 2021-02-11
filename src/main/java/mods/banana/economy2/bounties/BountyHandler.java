package mods.banana.economy2.bounties;

import com.google.gson.*;
import mods.banana.bananaapi.helpers.ItemStackHelper;
import mods.banana.economy2.balance.OfflinePlayer;
import mods.banana.economy2.balance.PlayerInterface;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.*;

public class BountyHandler {
    private final List<Bounty> bounties;

    public BountyHandler(List<Bounty> bounties) {
        this.bounties = bounties;
    }

    public void add(Bounty bounty) {
        OfflinePlayer.getPlayer(bounty.getOwner()).addBal(-bounty.getPrice());
        bounties.add(bounty);
    }

    public void remove(Bounty bounty, boolean returnBalance) {
        if(returnBalance) OfflinePlayer.getPlayer(bounty.getOwner()).addBal(bounty.getPrice());
        bounties.remove(bounty);
    }

    public Bounty get(UUID uuid) {
        for(Bounty bounty : bounties) {
            if(bounty.getId().equals(uuid))
                return  bounty;
        }
        return null;
    }

    public List<Bounty> getBounties() { return bounties; }

    public List<Bounty> getBounties(UUID player) {
        ArrayList<Bounty> output = new ArrayList<>();

        for(Bounty bounty : bounties) if(bounty.getOwner().equals(player)) output.add(bounty);

        return output;
    }

    public Optional<String> redeem(ServerPlayerEntity player, Bounty bounty) {
        if(!bounty.isValid()) remove(bounty, true);

        int amount = bounty.getAmount();
        long price = bounty.getPrice();

        List<Integer> stacks = new ArrayList<>();
        int amountFound = 0;

        // check if player has items
        for(int i = 0; i < player.inventory.size(); i++) {
            ItemStack playerStack = player.inventory.getStack(i);

            if(bounty.matches(playerStack)) {
                stacks.add(i);
                amountFound += playerStack.getCount();
            }
        }

        // if player has items, remove items from player and finalize trade
        if(stacks.size() > 0 && amountFound >= amount) {
            for(int stackIndex : stacks) {
                ItemStack stack = player.inventory.getStack(stackIndex);

                int amountToRemove = Math.min(amount, stack.getCount());

                player.inventory.setStack(stackIndex, ItemStackHelper.addCount(stack, -amountToRemove));
                amount -= amountToRemove;
            }

            ((PlayerInterface)player).addBal(price);
//            OfflinePlayer.getPlayer(bounty.getOwner()).addBal(-price);

            remove(bounty, false);

        } else return Optional.of("No items in your inventory match!");

        return Optional.empty();
    }

    public static class Serializer implements JsonSerializer<BountyHandler>, JsonDeserializer<BountyHandler> {
        public static final Gson GSON;

        @Override
        public BountyHandler deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            List<Bounty> bounties = new ArrayList<>();

            for(JsonElement element : json.getAsJsonArray()) {
                bounties.add(context.deserialize(element, Bounty.class));
            }

            return new BountyHandler(bounties);
        }

        @Override
        public JsonElement serialize(BountyHandler src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray array = new JsonArray();

            for(Bounty bounty : src.getBounties()) {
                array.add(context.serialize(bounty));
            }

            return array;
        }

        static {
            GSON = new GsonBuilder()
                    .registerTypeAdapter(BountyHandler.class, new BountyHandler.Serializer())
                    .registerTypeAdapter(Bounty.class, new Bounty.Serializer())
                    .setPrettyPrinting()
                    .create();
        }
    }
}

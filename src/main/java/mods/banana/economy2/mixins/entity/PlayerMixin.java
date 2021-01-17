package mods.banana.economy2.mixins.entity;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.trade.TradeInstance;
import mods.banana.economy2.chestshop.interfaces.ChestShopPlayerInterface;
import mods.banana.economy2.trade.TradePlayerInterface;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerMixin extends PlayerEntity implements TradePlayerInterface, ChestShopPlayerInterface {
    @Shadow @Final public MinecraftServer server;
    private long bal;

    // trade variables
    private TradeInstance currentTrade;
    private ArrayList<ItemStack> tradingItems = new ArrayList<>();
    private boolean accepted;

    public PlayerMixin(World world, BlockPos pos, float yaw, GameProfile profile) { super(world, pos, yaw, profile); }

    // balance functions
    public long getBal() { return bal; }
    public void setBal(long value) { bal = value; save(); }
    public void addBal(long amount) { bal += amount; save(); }

    public String getBalAsString() {
        return Economy2.addCurrencySign(bal);
    }

    public String getPlayerName() {return getEntityName();}

    // trade functions
    public TradeInstance getTrade() { return currentTrade; }
    public void setTrade(TradeInstance tradeInstance) {
        currentTrade = tradeInstance;
    }
    public ArrayList<ItemStack> getTradeItems() { return tradingItems; }

    public boolean getAccepted() { return accepted; }
    public void setAccepted(boolean accepted) { this.accepted = accepted; }

    public void resetTrade() {
        currentTrade = null;
        tradingItems = new ArrayList<>();
    }

    // chest shop functions
    public int countItem(Item item) {
        int amount = 0;
        for(int i = 0; i < inventory.size(); i++) {
            ItemStack currentStack = inventory.getStack(i);
            if(currentStack.getItem().equals(item)) amount += currentStack.getCount();
        }
        return amount;
    }

    public int countItemStack(ItemStack stack) {
        int amount = 0;
        for(int i = 0; i < inventory.size(); i++) {
            ItemStack currentStack = inventory.getStack(i);
            if(!currentStack.isEmpty() && ScreenHandler.canStacksCombine(currentStack, stack)) amount += currentStack.getCount();
        }
        return amount;
    }

    public void removeItemStack(ItemStack inputStack) {
        for(int i = 0; i < inventory.size() && inputStack.getCount() > 0; i++) {
            ItemStack currentStack = inventory.getStack(i);
            // if the items are the same, remove it
            if(ScreenHandler.canStacksCombine(currentStack, inputStack)) {
                // amount to remove is either the amount in the slot or the rest of the input amount
                int amount = Math.min(currentStack.getCount(), inputStack.getCount());
                // remove the count from the current stack
                currentStack.setCount(currentStack.getCount() - amount);
                // update the slot
                inventory.setStack(i, currentStack);
                // remove the count removed from the input
                inputStack.setCount(inputStack.getCount() - amount);
            }
        }
    }

    public void giveStack(ItemStack inputStack) {
        if(!inventory.insertStack(inputStack)) {
            ItemEntity itemEntity = dropItem(inputStack, false);
            if (itemEntity != null) {
                itemEntity.resetPickupDelay();
                itemEntity.setOwner(getUuid());
            }
        }
    }

    // reset player
    public void reset(String player) {
        bal = (long) Economy2.CONFIG.getValue("player.startingBalance", Integer.class);
//        playerName = player;
        save();
    }

    // save player to json
    public void save() {
        JsonObject player = new JsonObject();
        player.addProperty("playerName", getEntityName());
        player.addProperty("bal", bal);

        Economy2.BalanceJson.remove(getUuidAsString());
        Economy2.BalanceJson.add(getUuidAsString(), player);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConnect(MinecraftServer server, ServerWorld world, GameProfile profile, ServerPlayerInteractionManager interactionManager, CallbackInfo ci) {
        if(!Economy2.BalanceJson.has(getUuidAsString())) {
            reset(profile.getName());
            LOGGER.info("Added player " + profile.getName() + " to balance file");
//            System.out.println("Added player " + profile.getName() + " to balance file");
        } else {
            JsonObject player = Economy2.BalanceJson.get(getUuidAsString()).getAsJsonObject();

            bal = player.get("bal").getAsLong();
        }
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void onDisconnect(CallbackInfo ci) { save(); }
}

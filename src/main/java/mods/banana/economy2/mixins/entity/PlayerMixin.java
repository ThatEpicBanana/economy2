package mods.banana.economy2.mixins.entity;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import mods.banana.bananaapi.helpers.ItemStackHelper;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.gui.*;
import mods.banana.economy2.itemmodules.items.NbtItem;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import mods.banana.economy2.trade.TradeInstance;
import mods.banana.economy2.chestshop.interfaces.mixin.ChestShopPlayerInterface;
import mods.banana.economy2.trade.TradePlayerInterface;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerMixin extends PlayerEntity implements TradePlayerInterface, ChestShopPlayerInterface, GuiPlayer {
    @Shadow @Final public MinecraftServer server;

    @Shadow public abstract OptionalInt openHandledScreen(@Nullable NamedScreenHandlerFactory factory);
    @Shadow public abstract void openEditSignScreen(SignBlockEntity sign);

    @Shadow public ServerPlayNetworkHandler networkHandler;

    @Shadow public abstract void sendSystemMessage(Text message, UUID senderUuid);
    @Shadow public abstract void closeHandledScreen();

    private long bal;

    private final List<CustomGui> screenStack = new ArrayList<>();
    private SignGui customSign = null;
    private int openSignState = 0;

    private boolean closingGuiScreen = false;

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

    // gui stuff
    public void openScreen(GuiScreen screen) {
        openHandledScreen(screen.toFactory());
        // successfully opened screen
        if(currentScreenHandler instanceof GuiScreen) {
            screenStack.add(0, (GuiScreen) currentScreenHandler);
            ((GuiScreen) currentScreenHandler).updateState();
        }
    }

    public void openSignGui() {
        // so what happens when a player opens a sign is that the sign's position goes over to the client
        // what has to happen is for the client to be sent fake block data before it gets that sign
        BlockPos pos = getBlockPos();
        customSign = new SignGui(pos);
        networkHandler.sendPacket(new BlockUpdateS2CPacket(pos, Blocks.OAK_SIGN.getDefaultState()), (future) -> {
            // equivalent to openEditSignScreen
            customSign.setEditor(this);
            this.networkHandler.sendPacket(new SignEditorOpenS2CPacket(customSign.getPos()), (future1 -> {
                // last part updates the block back to what it should be
                networkHandler.sendPacket(new BlockUpdateS2CPacket(customSign.getPos(), world.getBlockState(getBlockPos())));
                // and adds the sign to the stack
                screenStack.add(0, customSign);
            }));
        });
        this.openSignState = 1;
    }

//    private void openSignGui2() {
//        // opening a sign has to be split into 3 parts for if the client processes the packets in the wrong order
//        openEditSignScreen(customSign);
//        this.openSignState = 2;
//    }
//
//    private void openSignGui3() {
//        // last part updates the block back to what it should be
//        networkHandler.sendPacket(new BlockUpdateS2CPacket(customSign.getPos(), world.getBlockState(getBlockPos())));
//        // and adds the sign to the stack
//        screenStack.add(0, customSign);
//        this.openSignState = 0;
//    }

    public void closeSignGui() {
        customSign = null;
        this.closeScreen();
    }

    public SignGui getCustomSign() { return customSign; }

    public CustomGui getGui(int i) {
        return screenStack.get(i);
    }

    public GuiScreen getScreen(int i) {
        CustomGui gui = screenStack.get(i);
        return gui instanceof GuiScreen ? (GuiScreen) gui : null;
    }

    public void closeScreen() { closeScreen(true); }

    public void closeScreen(boolean closeScreenHandler) {
        closingGuiScreen = true;

        GuiReturnValue<?> returnValue = getGui(0).getReturnValue();

        screenStack.remove(0);

        if(screenStack.size() > 0) {
            openScreen((GuiScreen) screenStack.get(0));

            // remove duplicate screen
            screenStack.remove(1);

            if(currentScreenHandler instanceof GuiScreen) {
                ((GuiScreen) currentScreenHandler).withReturnValue(returnValue);
                ((GuiScreen) currentScreenHandler).updateState();
            }
        } else {
            if(returnValue != null) sendSystemMessage(new LiteralText(String.valueOf(returnValue.getValue())), UUID.randomUUID());
            closeHandledScreen();
            clearScreenStack();
        }

        this.customSign = null;

        closingGuiScreen = false;
    }

    public boolean isClosingGuiScreen() { return closingGuiScreen; }
    public void clearScreenStack() { screenStack.clear(); }
    public int getScreenStackSize() { return screenStack.size(); }

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

    public int countItem(NbtItem item) {
        int amount = 0;
        for(int i = 0; i < inventory.main.size(); i++) {
            ItemStack currentStack = inventory.main.get(i);
            if(item.matches(currentStack, NbtMatcher.Type.ITEM)) amount += currentStack.getCount();
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

    public List<ItemStack> removeItem(NbtItem item, int count) {
        ArrayList<ItemStack> itemsRemoved = new ArrayList<>();
        for(int i = 0; i < inventory.main.size() && count > 0; i++) {
            ItemStack currentStack = inventory.main.get(i);
            // if the items are the same
            if(item.matches(currentStack, NbtMatcher.Type.ITEM)) {
                // the amount to remove is either the entirety of the slot or the rest of the input amount
                int amount = Math.min(currentStack.getCount(), count);

                // add item removed to list
                itemsRemoved.add(ItemStackHelper.setCount(currentStack.copy(), amount));
                // remove the amount from the current stack
                currentStack.setCount(currentStack.getCount() - amount);

                // update the count
                count -= amount;
            }
        }
        return itemsRemoved;
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

    public void giveStacks(List<ItemStack> inputStacks) {
        for(ItemStack stack : inputStacks) giveStack(stack);
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

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if(world.isClient) return;

        if(customSign == null) openSignState = 0;
//        else if(openSignState == 1) openSignGui2();
//        else if(openSignState == 2) openSignGui3();
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

//        openScreen(new ModulesScreen());
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void onDisconnect(CallbackInfo ci) { save(); }
}

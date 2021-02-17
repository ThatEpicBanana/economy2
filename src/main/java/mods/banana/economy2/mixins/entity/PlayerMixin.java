package mods.banana.economy2.mixins.entity;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import mods.banana.bananaapi.helpers.ItemStackHelper;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.EconomyItems;
import mods.banana.economy2.gui.*;
import mods.banana.economy2.gui.mixin.GuiPlayer;
import mods.banana.economy2.gui.screens.GuiScreen;
import mods.banana.economy2.gui.screens.SignGui;
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
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerMixin extends PlayerEntity implements TradePlayerInterface, ChestShopPlayerInterface, GuiPlayer {
    @Shadow @Final public MinecraftServer server;
    @Shadow public ServerPlayNetworkHandler networkHandler;

    @Shadow public abstract OptionalInt openHandledScreen(@Nullable NamedScreenHandlerFactory factory);
    @Shadow public abstract void openEditSignScreen(SignBlockEntity sign);
    @Shadow public abstract void closeHandledScreen();

    @Shadow public abstract void sendSystemMessage(Text message, UUID senderUuid);

    @Shadow public abstract void closeScreenHandler();

    private long bal;

    private final List<CustomGui> screenStack = new ArrayList<>();
    private SignGui customSign = null;
    private int openSignState = 0;
    // used to prevent infinite loops of closing screens
    private boolean closingOrOpeningGuiScreen = false;

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
        return Economy2.addCurrencySign(getBal());
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

    // custom gui stuff

    /**
     * Opens a gui screen and adds it to stack.
     * @param screen screen to be added
     */
    public void openScreen(GuiScreen screen) {
        openScreen(screen, false);
    }

    private void openScreen(GuiScreen screen, boolean replaceFirst) {
        this.closingOrOpeningGuiScreen = true;

        openHandledScreen(screen);
        // successfully opened screen
        if(currentScreenHandler instanceof GuiScreen) {
            if(replaceFirst) screenStack.set(0, (GuiScreen) currentScreenHandler);
            else screenStack.add(0, (GuiScreen) currentScreenHandler);

            ((GuiScreen) currentScreenHandler).updateState();
            ((GuiScreen) currentScreenHandler).forceStackUpdates(networkHandler);
        }

        this.closingOrOpeningGuiScreen = false;
    }

    @Redirect(method = "openHandledScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;closeHandledScreen()V"))
    private void preventClose(ServerPlayerEntity playerEntity) {
        if(!(playerEntity.currentScreenHandler instanceof FluidScreen)) playerEntity.closeHandledScreen();
    }

    @Inject(method = "swingHand", at = @At("HEAD"), cancellable = true)
    private void preventPunch(Hand hand, CallbackInfo ci) {
        if(currentScreenHandler instanceof FluidScreen) ci.cancel();
    }

    /**
     * closes a screen handler and sends it's return value to the next screen in the stack if it exits
     */
    public void closeScreen() {
        // declare that we are closing the gui screen
        closingOrOpeningGuiScreen = true;

        if(screenStack.size() > 1) {
            // get the return value of the gui
            GuiReturnValue<?> returnValue = getGui(0).getReturnValue();

            // remove screen from stack
            screenStack.remove(0);

            // open the next screen in the stack
            openScreen((GuiScreen) screenStack.get(0), true);

            // send return value to screen
            if(currentScreenHandler instanceof GuiScreen) {
                ((GuiScreen) currentScreenHandler).withReturnValue(returnValue);
                ((GuiScreen) currentScreenHandler).updateState();
            }
        } else exitScreen();

        this.customSign = null;

        closingOrOpeningGuiScreen = false;
    }

    /**
     * Exits the current gui screen without returning to a previous screen in the stack.
     */
    public void exitScreen() {
        // declare that we are closing the gui screen
        closingOrOpeningGuiScreen = true;

        // get the return value of the gui
        GuiReturnValue<?> returnValue = getGui(0).getReturnValue();

        if(returnValue != null && returnValue != GuiReturnValue.EMPTY && returnValue.getValue() != null)
            sendSystemMessage(new LiteralText(String.valueOf(returnValue.getValue())), UUID.randomUUID());

        closeHandledScreen();
        clearScreenStack();

        this.customSign = null;

        closingOrOpeningGuiScreen = false;
    }

    // check for player escaping from screen
    @Inject(method = "closeScreenHandler", at = @At("HEAD"), cancellable = true)
    private void afterClose(CallbackInfo ci) {
        // check if screen to close is a gui screen and it's not a duplicate call
        if(currentScreenHandler instanceof GuiScreen && !closingOrOpeningGuiScreen) {
            // if so, fully close the screen
            currentScreenHandler.close(this);
            exitScreen();

            // and return
            ci.cancel();
        }
    }


    // sign gui stuff

    /**
     * <p>Opens a custom sign gui.</p>
     * To close the gui, you must use the {@link #closeSignGui() closeSignGui} method.
     */
    public void openSignGui(Identifier id) {
        // so what happens when a player opens a sign is that the sign's position goes over to the client
        // what has to happen is for the client to be sent fake block data before it gets that sign

        // create sign at the bottom of the world at the player's position
        customSign = new SignGui(new BlockPos(getBlockPos().getX(), 0, getBlockPos().getZ()), id);
        // send fake sign to client
        networkHandler.sendPacket(new BlockUpdateS2CPacket(customSign.getPos(), Blocks.OAK_SIGN.getDefaultState()));
        // increment sign state
        this.openSignState = 1;
    }

    private void openSignGui2() {
        // opening a sign has to be split into 2 parts to make sure the client's block has updated
        openEditSignScreen(customSign);
        // add sign to stack
        screenStack.add(0, customSign);
    }

    public void closeSignGui() {
        networkHandler.sendPacket(new BlockUpdateS2CPacket(customSign.getPos(), world.getBlockState(getBlockPos())));
        this.closeScreen();
    }

    /**s
     * utility function to only get gui screens
     * @param i index of screen
     * @return screen in stack
     */
    public GuiScreen getScreen(int i) {
        CustomGui gui = screenStack.get(i);
        return gui instanceof GuiScreen ? (GuiScreen) gui : null;
    }


    public boolean isClosingOrOpeningGuiScreen() { return closingOrOpeningGuiScreen; }
    public void clearScreenStack() { screenStack.clear(); }

    public int getScreenStackSize() { return screenStack.size(); }
    public SignGui getCustomSign() { return customSign; }
    public CustomGui getGui(int i) { return screenStack.get(i); }


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

    public void giveStack(ItemStack itemStack) {
        // literally just copied from the /give command
        boolean bl = inventory.insertStack(itemStack);
        ItemEntity itemEntity;
        if (bl && itemStack.isEmpty()) {
            itemStack.setCount(1);
            itemEntity = dropItem(itemStack, false);
            if (itemEntity != null) {
                itemEntity.setDespawnImmediately();
            }

            world.playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((getRandom().nextFloat() - getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            playerScreenHandler.sendContentUpdates();
        } else {
            itemEntity = dropItem(itemStack, false);
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
        if(openSignState == 2) openSignGui2();
        if(openSignState != 0) openSignState++;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConnect(MinecraftServer server, ServerWorld world, GameProfile profile, ServerPlayerInteractionManager interactionManager, CallbackInfo ci) {
        if(!Economy2.BalanceJson.has(getUuidAsString())) {
            reset(profile.getName());
            LOGGER.info("Added player " + profile.getName() + " to balance file");
        } else {
            JsonObject player = Economy2.BalanceJson.get(getUuidAsString()).getAsJsonObject();

            bal = player.get("bal").getAsLong();
        }
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void onDisconnect(CallbackInfo ci) { save(); }
}

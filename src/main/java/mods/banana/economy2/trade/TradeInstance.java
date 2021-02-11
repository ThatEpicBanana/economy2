package mods.banana.economy2.trade;

import mods.banana.economy2.balance.PlayerInterface;
import mods.banana.economy2.chestshop.interfaces.mixin.ChestShopPlayerInterface;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.UUID;

public class TradeInstance {
    private final ServerPlayerEntity source;
    private final ServerPlayerEntity target;

    enum Side {
        SOURCE,
        TARGET
    }

    public int timer = 0;
    public final static int confirmTime = 60;
//    private boolean canAccept;

//    public void markAcceptable() { canAccept = true; }

    public TradeInstance(ServerPlayerEntity source, ServerPlayerEntity target) {
        this.source = source;
        this.target = target;
    }

    public ServerPlayerEntity getSource() { return source; }

    public ServerPlayerEntity getTarget() { return target; }

    public void sendTimeoutMessage() {
        source.sendSystemMessage(new LiteralText(target.getName().getString() + " has not responded in time!"), UUID.randomUUID());
        target.playSound(SoundEvents.UI_TOAST_OUT, SoundCategory.PLAYERS, 1, 1);
    }

    public void sendRequestMessage() {
        target.sendSystemMessage(new LiteralText(source.getName().getString() + " has asked for a trade. Click here to accept!")
                .fillStyle(Style.EMPTY
                                .withFormatting(Formatting.GREEN)
                                .withClickEvent(
                                        new ClickEvent(
                                                ClickEvent.Action.RUN_COMMAND,
                                                "/trade accept " + source.getName().getString()
                                        )
                                )
                ), UUID.randomUUID()
        );
        source.playSound(SoundEvents.UI_TOAST_OUT, SoundCategory.PLAYERS, 1, 1);
        target.playSound(SoundEvents.UI_TOAST_IN, SoundCategory.PLAYERS, 1, 1);
    }

    public void complete() { complete(true); }

    public void complete(boolean exchangeItems) {
        TradePlayerInterface sourceInterface = (TradePlayerInterface) source;
        TradePlayerInterface targetInterface = (TradePlayerInterface) target;

        if(exchangeItems) {
            for(ItemStack itemStack : sourceInterface.getTradeItems()) {
                targetInterface.giveStack(itemStack);
            }
            for(ItemStack itemStack : targetInterface.getTradeItems()) {
                sourceInterface.giveStack(itemStack);
            }

            source.playSound(SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 0.5F, 1);
            target.playSound(SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 0.5F, 1);
        } else {
            for(ItemStack itemStack : sourceInterface.getTradeItems()) {
                sourceInterface.giveStack(itemStack);
            }
            for(ItemStack itemStack : targetInterface.getTradeItems()) {
                targetInterface.giveStack(itemStack);
            }
        }

        sourceInterface.resetTrade();
        targetInterface.resetTrade();

        TradeHandler.trades.remove(this);
    }

    public void sendUpdateMessage(boolean resetTimer) {
        source.sendSystemMessage(new LiteralText("Trade with " + target.getName().asString()).formatted(Formatting.BOLD).formatted(Formatting.YELLOW).formatted(Formatting.UNDERLINE), UUID.randomUUID());
        target.sendSystemMessage(new LiteralText("Trade with " + source.getName().asString()).formatted(Formatting.BOLD).formatted(Formatting.YELLOW).formatted(Formatting.UNDERLINE), UUID.randomUUID());

        sendItemsOf(source, Side.SOURCE);
        sendItemsOf(target, Side.TARGET);

        MutableText bottomText = new LiteralText("\nCancel").fillStyle(Style.EMPTY
                .withFormatting(Formatting.DARK_RED)
                .withClickEvent(
                        new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/trade cancel"
                        )
                )
        );

        if(timer >= confirmTime && !resetTimer) bottomText.append(new LiteralText(" Confirm").fillStyle(Style.EMPTY
                .withFormatting(Formatting.GREEN)
                .withClickEvent(
                        new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/trade confirm"
                        )
                )
        ));

        source.sendSystemMessage(bottomText, UUID.randomUUID());
        target.sendSystemMessage(bottomText, UUID.randomUUID());

        if(resetTimer) {
            timer = 0;
            ((TradePlayerInterface)source).setAccepted(false);
            ((TradePlayerInterface)target).setAccepted(false);
        }
    }

    private void sendItemsOf(ServerPlayerEntity player, Side side) {
        // get player name
        LiteralText name = new LiteralText("\n" + player.getName().getString() + ":");
        // send to both players
        source.sendSystemMessage(name, UUID.randomUUID());
        target.sendSystemMessage(name, UUID.randomUUID());

        // get player's trading items
        ArrayList<ItemStack> sourceItems = ((TradePlayerInterface) player).getTradeItems();
        for(int i = 0; i < sourceItems.size(); i++) {
            // send items to both
            sendItemMessage(source, sourceItems.get(i), i, side == Side.SOURCE);
            sendItemMessage(target, sourceItems.get(i), i, side == Side.TARGET);
        }
    }

    private void sendItemMessage(ServerPlayerEntity player, ItemStack item, int index, boolean withClear) {
        MutableText text = new LiteralText("   " + item.getCount() + " " + item.getName().getString()) // ex: 64 String
                .fillStyle(
                        Style.EMPTY.withFormatting(Formatting.GRAY)
                                .withHoverEvent(
                                        new HoverEvent(
                                                HoverEvent.Action.SHOW_ITEM,
                                                new HoverEvent.ItemStackContent(item)
                                        )
                                )
                );
        if(withClear) text.append(
                new LiteralText(" Remove").formatted(Formatting.RED).fillStyle(Style.EMPTY
                        .withClickEvent(
                                new ClickEvent(
                                        ClickEvent.Action.RUN_COMMAND,
                                        "/trade remove " + index
                                )
                        ).withHoverEvent(
                                new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        new LiteralText("Remove item from current trade").formatted(Formatting.RED)
                                )
                        )
                )
        );

        player.sendSystemMessage(text, UUID.randomUUID());
    }
}

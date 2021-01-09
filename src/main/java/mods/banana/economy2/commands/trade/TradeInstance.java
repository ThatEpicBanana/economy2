package mods.banana.economy2.commands.trade;

import mods.banana.economy2.interfaces.PlayerInterface;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.UUID;

public class TradeInstance {
    private final ServerPlayerEntity source;
    private final ServerPlayerEntity target;
    private final TradeHandler handler;

    enum Side {
        SOURCE,
        TARGET
    }

    public int timer = 0;
    public final static int confirmTime = 60;
//    private boolean canAccept;

//    public void markAcceptable() { canAccept = true; }

    TradeInstance(ServerPlayerEntity source, ServerPlayerEntity target, TradeHandler handler) {
        this.source = source;
        this.target = target;
        this.handler = handler;
    }

    public ServerPlayerEntity getSource() { return source; }

    public ServerPlayerEntity getTarget() { return target; }

    public void sendTimeoutMessage() {
        source.sendSystemMessage(new LiteralText(target.getName().getString() + " has not responded in time!"), UUID.randomUUID());
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
    }

    public void complete() { complete(true); }

    public void complete(boolean exchangeItems) {
        PlayerInterface sourceInterface = (PlayerInterface) source;
        PlayerInterface targetInterface = (PlayerInterface) target;

        if(exchangeItems) {
            for(ItemStack itemStack : sourceInterface.getTradeItems()) {
                target.giveItemStack(itemStack);
            }
            for(ItemStack itemStack : targetInterface.getTradeItems()) {
                source.giveItemStack(itemStack);
            }
        } else {
            for(ItemStack itemStack : sourceInterface.getTradeItems()) {
                source.giveItemStack(itemStack);
            }
            for(ItemStack itemStack : targetInterface.getTradeItems()) {
                target.giveItemStack(itemStack);
            }
        }

        sourceInterface.resetTrade();
        targetInterface.resetTrade();

        handler.trades.remove(this);
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
            ((PlayerInterface)source).setAccepted(false);
            ((PlayerInterface)target).setAccepted(false);
        }
    }

    private void sendItemsOf(ServerPlayerEntity player, Side side) {
        // get player name
        LiteralText name = new LiteralText("\n" + player.getName().getString() + ":");
        // send to both players
        source.sendSystemMessage(name, UUID.randomUUID());
        target.sendSystemMessage(name, UUID.randomUUID());

        // get player's trading items
        ArrayList<ItemStack> sourceItems = ((PlayerInterface) player).getTradeItems();
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

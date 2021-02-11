package mods.banana.economy2.bounties.gui;

import mods.banana.bananaapi.helpers.ItemStackHelper;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.EconomyItems;
import mods.banana.economy2.bounties.Bounty;
import mods.banana.economy2.gui.GuiReturnValue;
import mods.banana.economy2.gui.mixin.GuiPlayer;
import mods.banana.economy2.gui.screens.GuiScreen;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static mods.banana.economy2.EconomyItems.Gui.*;
import static mods.banana.economy2.EconomyItems.Bounties.*;
import static mods.banana.economy2.EconomyItems.Bounties.View.*;

public class ViewBounty extends GuiScreen {
    private final Bounty bounty;
    private boolean reload = false;
    private String exception = "";

    public ViewBounty(Bounty bounty) { this(0, new PlayerInventory(null), bounty); }

    public ViewBounty(int syncId, PlayerInventory playerInventory, Bounty bounty) {
        super(syncId, playerInventory, 6, new Identifier("bounty", "list"));
        this.bounty = bounty;
    }

    @Override
    public GuiReturnValue<?> getReturnValue() {
        return reload ? new GuiReturnValue<>(true, this) : null;
    }

    @Override
    public Text getDisplayName() {
        return new LiteralText("Bounty");
    }

    @Override
    public void updateState() {
        clear();

        setStackInSlot(9 + 4, BOUNTY.convertTag(bounty.toItemStack()));

        if(bounty.getCannotMatch().size() > 0)
            setStackInSlot(
                    9 + 5,
                    ItemStackHelper.setLore(
                            CANNOT_MATCH.getItemStack(),
                            bounty.getCannotMatch().stream().map(
                                    nbtMatcher -> new LiteralText(nbtMatcher.getIdentifier().toString())
                            ).collect(Collectors.toList())
                    )
            );

        setStackInSlot(9 * 3 + 4, ItemStackHelper.setLore(REDEEM.getItemStack(), List.of(new LiteralText(exception))));
        setStackInSlot(9 * 5 + 4, RETURN.getItemStack());
    }

    @Override
    public ItemStack onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
        if(i >= 0 && !playerEntity.world.isClient) {
            ItemStack stack = getSlot(i).getStack();

            if(REDEEM.matches(stack)) {
                Optional<String> returnValue = Economy2.bountyHandler.redeem((ServerPlayerEntity) playerEntity, bounty);

                if(returnValue.isPresent()) {
                    exception = returnValue.get();
                } else {
                    reload = true;
                    ((GuiPlayer)playerEntity).closeScreen();
                }
            }

            setStackInSlot(i, stack);
        }

        return super.onSlotClick(i, j, actionType, playerEntity);
    }
}

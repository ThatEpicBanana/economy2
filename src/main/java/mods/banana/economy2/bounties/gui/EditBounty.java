package mods.banana.economy2.bounties.gui;

import mods.banana.economy2.Economy2;
import mods.banana.economy2.bounties.Bounty;
import mods.banana.economy2.gui.GuiReturnValue;
import mods.banana.economy2.gui.mixin.GuiPlayer;
import mods.banana.economy2.gui.screens.GuiScreen;
import mods.banana.economy2.gui.screens.SignGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.regex.Pattern;

import static mods.banana.economy2.EconomyItems.Bounties.Edit.*;
import static mods.banana.economy2.EconomyItems.Bounties.*;
import static mods.banana.economy2.EconomyItems.Gui.*;

public class EditBounty extends GuiScreen {
    private final Bounty bounty;
    private static final Pattern checkInt = Pattern.compile("^\\d+$");

    @Override
    public void withReturnValue(GuiReturnValue<?> value) {
        Identifier id = value.getParent().getId();
        // if parent is sign with id of edit bounty
        if(value.getParent() instanceof SignGui && id.getNamespace().equals("edit-bounty")) {
            String string = (String) value.getValue();
            // if string is a number
            if(checkInt.matcher(string).find()) {
                // set amount or price to number
                if(id.getPath().equals("amount")) bounty.setAmount(Integer.parseInt(string));
                else if(id.getPath().equals("price")) bounty.setPrice(Long.parseLong(string));
            }
        }
    }

    public EditBounty(Bounty bounty) {
        this(1, new PlayerInventory(null), bounty);
    }

    public EditBounty(int syncId, PlayerInventory playerInventory, Bounty bounty) {
        this(syncId, playerInventory, new SimpleInventory(9 * 3), bounty);
    }

    public EditBounty(int syncId, PlayerInventory playerInventory, Inventory inventory, Bounty bounty) {
        super(syncId, playerInventory, inventory, 3, new Identifier("bounty", "edit"));
        this.bounty = bounty;
    }

    @Override
    public Text getDisplayName() {
        return new LiteralText("Edit Bounty");
    }

    @Override
    public void updateState() {
        clear();
        setStackInSlot(4, bounty.toItemStack());

        setStackInSlot(9 + 2, AMOUNT.getItemStack());
        setStackInSlot(9 + 4, DELETE.getItemStack());
        setStackInSlot(9 + 6, PRICE.getItemStack());

        setStackInSlot(9 * 2 + 4, RETURN.getItemStack());
    }

    @Override
    public ItemStack onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
        if(i >= 0 && !playerEntity.world.isClient) {
            ItemStack stack = getSlot(i).getStack();
            if(DELETE.matches(stack)) {
                Economy2.bountyHandler.remove(bounty);
                ((GuiPlayer)playerEntity).closeScreen();
            }

            if(AMOUNT.matches(stack)) {
                ((GuiPlayer)playerEntity).openSignGui(new Identifier("edit-bounty", "amount"));
            }

            if(PRICE.matches(stack)) {
                ((GuiPlayer)playerEntity).openSignGui(new Identifier("edit-bounty", "price"));
            }
        }

        return super.onSlotClick(i, j, actionType, playerEntity);
    }
}

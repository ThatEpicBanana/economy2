package mods.banana.economy2.bounties.gui;

import mods.banana.economy2.Economy2;
import mods.banana.economy2.EconomyItems;
import mods.banana.economy2.bounties.Bounty;
import mods.banana.economy2.bounties.items.BountyItem;
import mods.banana.economy2.gui.GuiReturnValue;
import mods.banana.economy2.gui.mixin.GuiPlayer;
import mods.banana.economy2.gui.screens.GuiScreen;
import mods.banana.economy2.gui.screens.ListGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static mods.banana.economy2.EconomyItems.Bounties.*;

public class BountyList extends ListGui {
    private final List<Bounty> bounties = new ArrayList<>(Economy2.bountyHandler.getBounties());

    public static int sizeInScreen = 7 * 4;

    public BountyList() { this(0, new PlayerInventory(null)); }

    public BountyList(int syncId, PlayerInventory playerInventory) {
        super(syncId, playerInventory, 6, new Identifier("bounty", "list"));
    }

//    @Override
//    public GuiReturnValue<?> getReturnValue() {
//        return new GuiReturnValue<>("aaaa", this);
//    }


    @Override
    public void withReturnValue(GuiReturnValue<?> value) {
        if(value != null && value.getValue() instanceof Boolean && (Boolean) value.getValue()) {
            // reset bounties
            bounties.clear();
            bounties.addAll(Economy2.bountyHandler.getBounties());
        }
    }

    @Override
    public Text getDisplayName() {
        return new LiteralText("Bounties List");
    }

    @Override
    public void updateState() {
        // setup top and bottom rows
        for(int i = 0; i < 9; i++) {
            setStackInSlot(i, EconomyItems.Gui.EMPTY.getItemStack());
            setStackInSlot(5 * 9 + i, EconomyItems.Gui.EMPTY.getItemStack());
        }

        // setup sides
        for(int i = 0; i < 5; i++) {
            setStackInSlot(i * 9, EconomyItems.Gui.EMPTY.getItemStack());
            setStackInSlot(i * 9 + 8, EconomyItems.Gui.EMPTY.getItemStack());
        }

        if(getPage() != 0) setStackInSlot(5 * 9, EconomyItems.Gui.PREVIOUS.getItemStack());
        if(hasMoreItems()) setStackInSlot(5 * 9 + 8, EconomyItems.Gui.NEXT.getItemStack());

        setStackInSlot(5 * 9 + 4, EconomyItems.Gui.RETURN.getItemStack());

        for(int i = 0; i < sizeInScreen; i++) {
            int adjusted = i + 10 + (Math.floorDiv(i, 7) * 2); // adjust for the two slots taken up by the left
            int index = i + sizeInScreen * getPage(); // get the index

            if(index < bounties.size()) {
                Bounty bounty = bounties.get(index);
//                setStackInSlot(adjusted, BOUNTY.setId(bounty.toItemStack(), bounty.getId()));
                setStackInSlot(
                        adjusted,
                        BOUNTY.toBuilder(bounty.toItemStack(), false)
                                .customValue(
                                        "id",
                                        NbtHelper.fromUuid(bounty.getId())
                                ).build()
                );
            }
            else setStackInSlot(adjusted, ItemStack.EMPTY);
        }
    }

    @Override
    public ItemStack onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
        if(i >= 0 && !playerEntity.world.isClient) {
            ItemStack stack = getSlot(i).getStack();

            if(BOUNTY.matches(stack)) {
                ((GuiPlayer)playerEntity).openScreen(
                        new ViewBounty(
                                Economy2.bountyHandler.get(
                                        NbtHelper.toUuid(
                                                BOUNTY.toReader(stack).getCustomValue("id")
                                        )
                                )
                        )
                );
            }
        }
        return super.onSlotClick(i, j, actionType, playerEntity);
    }

    @Override
    public boolean hasMoreItems() {
        return bounties.size() > (getPage() + 1) * sizeInScreen;
    }

//    private BountyList(int syncId, PlayerInventory playerInventory, Inventory inventory) {
//        super(syncId, playerInventory, inventory, 6, new Identifier("bounty", "list"));
//    }

//    @Override
//    public GuiScreen copy(int syncId, PlayerInventory inventory, PlayerEntity player) {
//        return new BountyList(syncId, inventory, getInventory());
//    }
}

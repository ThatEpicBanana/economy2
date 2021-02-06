package mods.banana.economy2.bounties.gui;

import mods.banana.economy2.EconomyItems;
import mods.banana.economy2.gui.screens.GuiScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CreateBounty extends GuiScreen {
    private boolean customTags;

    public CreateBounty() { this(0, new PlayerInventory(null)); }

    public CreateBounty(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(9 * 6), false);
    }

    public CreateBounty(int syncId, PlayerInventory playerInventory, Inventory inventory, boolean customTags) {
        super(syncId, playerInventory, inventory, 6, new Identifier("bounty", "create"));
        this.customTags = customTags;
    }

    @Override
    public Text getDisplayName() {
        return new LiteralText("Create bounty");
    }

    @Override
    public void updateState() {
        clear();

        if(customTags) {
            setStackInSlot(5, EconomyItems.Bounties.ALLOW_CUSTOM_TAGS.getItemStack());

            setStackInSlot(9 + 1, EconomyItems.Bounties.SET_ITEM.getItemStack());

            for(int i = 0; i < 5; i++) {
                setStackInSlot(9 + 3 + i, EconomyItems.Bounties.UNSET_MATCHER.getItemStack());
            }
        } else {
            setStackInSlot(4, EconomyItems.Bounties.ALLOW_CUSTOM_TAGS.getItemStack());

            setStackInSlot(9 + 4, EconomyItems.Bounties.SET_ITEM.getItemStack());
        }

        setStackInSlot(9 * 5 + 4, EconomyItems.Gui.RETURN.getItemStack());
    }

    @Override
    public ItemStack onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
        if(i >= 0 && !playerEntity.world.isClient) {
            ItemStack stack = getSlot(i).getStack();

            if(EconomyItems.Bounties.ALLOW_CUSTOM_TAGS.matches(stack)) customTags = !customTags;
//            if(i == 0) {
//                for(int k = 0; k < 9; k++) {
//
//                }
//            }
        }

        return super.onSlotClick(i, j, actionType, playerEntity);
    }

    @Override
    public GuiScreen copy(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new CreateBounty(syncId, inventory, getInventory(), customTags);
    }
}

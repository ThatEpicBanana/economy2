package mods.banana.economy2.bounties.gui;

import mods.banana.economy2.EconomyItems;
import mods.banana.economy2.gui.GuiScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CreateBounty extends GuiScreen {
    public CreateBounty(Identifier id) { this(0, new PlayerInventory(null), id); }
    public CreateBounty(int syncId, PlayerInventory playerInventory, Identifier id) { this(syncId, playerInventory, new SimpleInventory(9 * 3), id); }
    public CreateBounty(int syncId, PlayerInventory playerInventory, Inventory inventory, Identifier id) {
        super(syncId, playerInventory, inventory, 3, id);
    }

    @Override
    public Text getName() {
        return new LiteralText("Create bounty");
    }

    @Override
    public void updateState() {
        setStackInSlot(9 + 3, EconomyItems.Bounties.SET_ITEM.getItemStack());
    }

    @Override
    public GuiScreen copy(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new CreateBounty(syncId, inventory, getInventory(), getId());
    }
}

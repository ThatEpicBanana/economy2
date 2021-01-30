package mods.banana.economy2.itemmodules.gui;

import mods.banana.economy2.bounties.gui.BountyListScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class ModulesScreenFactory implements NamedScreenHandlerFactory {
    @Override
    public Text getDisplayName() {
        return new LiteralText("Bounties");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new ModulesScreen(syncId, inv);
    }
}

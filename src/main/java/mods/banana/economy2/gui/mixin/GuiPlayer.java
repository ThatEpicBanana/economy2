package mods.banana.economy2.gui.mixin;

import mods.banana.economy2.gui.screens.GuiScreen;
import mods.banana.economy2.gui.screens.SignGui;
import net.minecraft.util.Identifier;

public interface GuiPlayer {
    // base functions
    void openScreen(GuiScreen screen);
    void closeScreen();
    void exitScreen();

    GuiScreen getScreen(int i);
    void clearScreenStack();

    int getScreenStackSize();
    boolean isClosingOrOpeningGuiScreen();

    void openSignGui(Identifier id);
    SignGui getCustomSign();
    void closeSignGui();
}

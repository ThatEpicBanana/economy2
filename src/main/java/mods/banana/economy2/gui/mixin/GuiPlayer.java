package mods.banana.economy2.gui.mixin;

import mods.banana.economy2.gui.screens.GuiScreen;
import mods.banana.economy2.gui.screens.SignGui;

public interface GuiPlayer {
    // base functions
    void openScreen(GuiScreen screen);
    void closeScreen();
    void closeScreen(boolean closeScreenHandler);
    void exitScreen();

    GuiScreen getScreen(int i);
    void clearScreenStack();

    int getScreenStackSize();
    boolean isClosingOrOpeningGuiScreen();

    void openSignGui();
    SignGui getCustomSign();
    void closeSignGui();
}

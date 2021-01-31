package mods.banana.economy2.gui;

public interface GuiPlayer {
    // base functions
    void openScreen(GuiScreen screen);
    void closeScreen();
    void closeScreen(boolean closeScreenHandler);

    GuiScreen getScreen(int i);
    void clearScreenStack();

    int getScreenStackSize();
    boolean isClosingGuiScreen();

    void openSignGui();
    SignGui getCustomSign();
    void closeSignGui();
}

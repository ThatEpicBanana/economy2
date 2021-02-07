package mods.banana.economy2.gui;

import net.minecraft.util.Identifier;

public interface CustomGui {
    GuiReturnValue<?> getReturnValue();
    Identifier getId();
}

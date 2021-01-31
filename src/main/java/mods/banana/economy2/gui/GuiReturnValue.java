package mods.banana.economy2.gui;

import net.minecraft.screen.ScreenHandler;

public class GuiReturnValue<T> {
    public static GuiReturnValue<?> EMPTY = new GuiReturnValue<>(null, null);

    private final T value;
    private final CustomGui parent;

    public GuiReturnValue(T value, CustomGui parent) {
        this.value = value;
        this.parent = parent;
    }

    public T getValue() { return value; }
    public CustomGui getParent() { return parent; }
}

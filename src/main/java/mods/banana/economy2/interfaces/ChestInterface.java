package mods.banana.economy2.interfaces;

import java.util.UUID;

public interface ChestInterface {
    boolean isChestShop();
    UUID getParent();
    void setLimit(int index);
}

package mods.banana.economy2.chestshop.interfaces;

import java.util.UUID;

public interface ChestShopPart {
    boolean isChestShop();
    void destroy(boolean destroyOther);
    UUID getParent();
}

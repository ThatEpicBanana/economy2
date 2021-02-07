package mods.banana.economy2.gui.screens;

import mods.banana.economy2.gui.CustomGui;
import mods.banana.economy2.gui.GuiReturnValue;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class SignGui extends SignBlockEntity implements CustomGui {
    private String returnValue;
    private final Identifier id;

    public SignGui(BlockPos pos, Identifier id) {
        setPos(pos);
        this.id = id;
    }

    public GuiReturnValue<?> getReturnValue() {
        return new GuiReturnValue<>(returnValue, this);
    }

    @Override
    public Identifier getId() { return id; }

    public void setReturnValue(String returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public BlockState getCachedState() {
        return Blocks.OAK_SIGN.getDefaultState();
    }
}
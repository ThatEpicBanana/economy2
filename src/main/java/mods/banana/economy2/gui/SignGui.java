package mods.banana.economy2.gui;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.util.math.BlockPos;

public class SignGui extends SignBlockEntity implements CustomGui {
    String returnValue;

    public SignGui(BlockPos pos) {
        setPos(pos);
    }

    public GuiReturnValue<?> getReturnValue() {
        return new GuiReturnValue<>(returnValue, this);
    }

    public void setReturnValue(String returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public BlockState getCachedState() {
        return Blocks.OAK_SIGN.getDefaultState();
    }
}
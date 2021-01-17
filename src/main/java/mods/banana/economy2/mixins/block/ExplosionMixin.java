package mods.banana.economy2.mixins.block;

import mods.banana.economy2.chestshop.interfaces.ChestShopPart;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;

@Mixin(Explosion.class)
public class ExplosionMixin<E> {
    @Shadow @Final private World world;

    @Redirect(method = "affectWorld", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;", ordinal = 0))
    private E onGetPosition(Iterator<E> iterator) {
        BlockPos pos = (BlockPos) iterator.next();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(!world.isClient) {
            if(blockEntity instanceof ChestBlockEntity || blockEntity instanceof SignBlockEntity) {
                if(((ChestShopPart)blockEntity).isChestShop()) {
                    world.updateListeners(pos, Blocks.AIR.getDefaultState(), world.getBlockState(pos), 3);
                    return (E) BlockPos.ORIGIN;
                }
            }
        }
        return (E) pos;
    }

}

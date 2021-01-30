package mods.banana.economy2.mixins.predicate;

import mods.banana.economy2.itemmodules.interfaces.NbtPredicateInterface;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.NbtPredicate;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NbtPredicate.class)
public class NbtPredicateMixin implements NbtPredicateInterface {
    @Shadow @Final @Nullable private CompoundTag tag;

    public CompoundTag getTag() { return tag; }
}

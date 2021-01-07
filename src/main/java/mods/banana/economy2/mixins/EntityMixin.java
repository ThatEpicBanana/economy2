package mods.banana.economy2.mixins;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract String getEntityName();

    @Shadow public abstract String getUuidAsString();

    @Shadow public abstract UUID getUuid();

    @Shadow
    protected UUID uuid;
}

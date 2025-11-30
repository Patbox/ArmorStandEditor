package eu.pb4.armorstandeditor.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor
    boolean isHasVisualFire();

    @Accessor
    void setHasVisualFire(boolean hasVisualFire);
}

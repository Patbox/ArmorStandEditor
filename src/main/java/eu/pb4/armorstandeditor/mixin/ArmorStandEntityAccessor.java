package eu.pb4.armorstandeditor.mixin;

import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArmorStandEntity.class)
public interface ArmorStandEntityAccessor {
    @Invoker
    void callSetSmall(boolean x);

    @Invoker
    void callSetShowArms(boolean x);

    @Invoker
    void callSetHideBasePlate(boolean x);

    @Accessor
    int getDisabledSlots();

    @Accessor("disabledSlots")
    void setDisabledSlots(int slots);
}

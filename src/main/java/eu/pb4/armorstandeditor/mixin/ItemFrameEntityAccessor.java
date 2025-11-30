package eu.pb4.armorstandeditor.mixin;

import net.minecraft.world.entity.decoration.ItemFrame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemFrame.class)
public interface ItemFrameEntityAccessor {
    @Accessor("fixed")
    boolean getFixed();

    @Accessor("fixed")
    void setFixed(boolean v);
}

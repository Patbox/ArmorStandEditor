package eu.pb4.armorstandeditor.gui;

import eu.pb4.armorstandeditor.util.TextUtils;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.core.Rotations;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Items;

public class ModifyPoseGui extends BaseWorldGui {
    private final BiConsumer<ArmorStand, Rotations> setter;
    private final Function<ArmorStand, Rotations> getter;
    private RotationType rotationType = RotationType.PITCH;

    public ModifyPoseGui(EditingContext context, int slot, BiConsumer<ArmorStand, Rotations> setter, Function<ArmorStand, Rotations> getter) {
        super(context, slot);
        this.setter = setter;
        this.getter = getter;
        this.rebuildUi();
        this.open();
    }

    @Override
    protected void buildUi() {
        if (this.rotationType == null) {
            this.rotationType = RotationType.PITCH;
        }

        var wool = switch (this.rotationType) {
            case PITCH -> Items.RED_WOOL;
            case YAW -> Items.GREEN_WOOL;
            case ROLL -> Items.BLUE_WOOL;
        };

        this.setSlot(0, baseElement(wool, "action.rotate.angle." + this.rotationType.name().toLowerCase(Locale.ROOT), false).setCallback((x, y, z, c) -> {
                    if (this.player.isShiftKeyDown()) {
                        return;
                    }
                    this.playSound(SoundEvents.UI_BUTTON_CLICK, 0.5f, 1f);
                    this.rotationType = RotationType.values()[(this.rotationType.ordinal() + RotationType.values().length + (y.isRight ? 1 : y.isLeft ? -1 : 0)) % RotationType.values().length];
                    this.buildUi();
                })
        );

        this.setSlot(2, baseElement(Items.IRON_NUGGET, TextUtils.gui("action.rotate", -this.context.rotationDelta * 0.5), false)
                .setCallback((x, y, z, c) -> {
                    this.rotate(-this.context.rotationDelta * 0.5);
                })
        );

        this.setSlot(3, baseElement(Items.IRON_INGOT, TextUtils.gui("action.rotate", -this.context.rotationDelta), false)
                .setCallback((x, y, z, c) -> {
                    this.rotate(-this.context.rotationDelta);
                })
        );
        

        this.setSlot(4, baseElement(Items.GOLD_INGOT, TextUtils.gui("action.rotate", this.context.rotationDelta), false)
                .setCallback((x, y, z, c) -> {
                    this.rotate(this.context.rotationDelta);
                })
        );

        this.setSlot(5, baseElement(Items.GOLD_NUGGET, TextUtils.gui("action.rotate", this.context.rotationDelta * 0.5), false)
                .setCallback((x, y, z, c) -> {
                    this.rotate(this.context.rotationDelta * 0.5);
                })
        );

        this.setSlot(7, baseElement(Items.LEVER, "action.pose.reset", false)
                .setCallback((x, y, z) -> {
                    this.playSound(SoundEvents.UI_BUTTON_CLICK, 0.5f, 1f);

                    var armorStand = this.context.armorStand;
                    this.setter.accept(armorStand, new Rotations(0,0,0));
                }));
    }

    @Override
    public boolean onSelectedSlotChange(int slot) {
        if (this.player.isShiftKeyDown()) {
            var current = this.getSelectedSlot();

            var delta = slot - current;

            this.context.rotationDelta = Mth.clamp(this.context.rotationDelta + delta, 0, 360);
            this.player.displayClientMessage(TextUtils.gui("action.rotate.set", this.context.rotationDelta), true);

            this.playSound(SoundEvents.NOTE_BLOCK_HAT, 0.5f, 1f);
            this.player.connection.send(new ClientboundSetHeldSlotPacket(this.selectedSlot));
            this.buildUi();

            return false;
        }

        return super.onSelectedSlotChange(slot);
    }

    private void rotate(double v) {
        if (this.player.isShiftKeyDown()) {
            return;
        }

        var base = this.getter.apply(this.context.armorStand);

        var out = switch (this.rotationType) {
            case PITCH -> new Rotations((float) (v + base.x()), base.y(), base.z());
            case YAW -> new Rotations(base.x(), (float) (v + base.y()), base.z());
            case ROLL -> new Rotations(base.x(), base.y(), (float) (v + base.z()));
        };

        this.setter.accept(this.context.armorStand, out);
    }

    @Override
    protected EditingContext.SwitchEntry asSwitchableUi() {
        return new EditingContext.SwitchEntry((context1, slot) -> new ModifyPoseGui(context1, slot, this.setter, this.getter), this.getSelectedSlot());
    }

    public static EditingContext.SwitchableUi create(BiConsumer<ArmorStand, Rotations> setter, Function<ArmorStand, Rotations> getter) {
        return (ctx, slot) -> new ModifyPoseGui(ctx, slot, setter, getter);
    }

    public enum RotationType {
        PITCH,
        YAW,
        ROLL
    }
}

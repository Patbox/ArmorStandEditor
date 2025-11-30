package eu.pb4.armorstandeditor.gui;

import eu.pb4.armorstandeditor.util.TextUtils;
import eu.pb4.common.protection.api.CommonProtection;
import java.util.Locale;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;

public class MoveGui extends BaseWorldGui {
    private CurrentAxis currentAxis = CurrentAxis.LOOK;
    private Direction playerLookingDirection;

    public MoveGui(EditingContext context, int slot) {
        super(context, slot);

        var vec = this.player.getViewVector(0);
        this.playerLookingDirection = Direction.getApproximateNearest(vec.x, vec.y, vec.z);

        this.rebuildUi();
        this.open();
    }

    @Override
    public void onTick() {
        if (this.currentAxis.axis == null) {
            var vec = this.player.getViewVector(0);
            var dir = Direction.getApproximateNearest(vec.x, vec.y, vec.z);

            if (dir != this.playerLookingDirection) {
                this.playerLookingDirection = dir;
                this.rebuildUi();
            }
        }
        super.onTick();
    }

    @Override
    protected void buildUi() {
        var wool = switch (this.currentAxis) {
            case LOOK -> Items.WHITE_WOOL;
            case X -> Items.RED_WOOL;
            case Y -> Items.GREEN_WOOL;
            case Z -> Items.BLUE_WOOL;
        };

        var moveBase = ((int) (this.context.moveBlockDelta * 100)) / 100d;

        this.setSlot(0, baseElement(wool,  TextUtils.gui("action.move." + this.currentAxis.name().toLowerCase(Locale.ROOT), TextUtils.direction(this.getDirection(true))), false).setCallback((x, y, z, c) -> {
                    if (this.player.isShiftKeyDown()) {
                        return;
                    }
                    this.playClickSound();
                    this.currentAxis = CurrentAxis.values()[(this.currentAxis.ordinal() + CurrentAxis.values().length + (y.isRight ? 1 : y.isLeft ? -1 : 0)) % CurrentAxis.values().length];
                    this.buildUi();
                })
        );


        this.setSlot(1, baseElement(Items.IRON_NUGGET, TextUtils.gui("action.move", moveBase * 0.5, TextUtils.direction(this.getDirection(false))),false)
                .setCallback((x, y, z, c) -> {
                    this.move(-moveBase * 0.5);
                })
        );

        this.setSlot(2, baseElement(Items.IRON_INGOT, TextUtils.gui("action.move", moveBase, TextUtils.direction(this.getDirection(false))), false)
                .setCallback((x, y, z, c) -> {
                    this.move(-moveBase);
                })
        );

        this.setSlot(3, baseElement(Items.COMPASS, TextUtils.gui("action.move.rotate", this.context.moveRotationDelta), false)
                .setCallback((x, y, z, c) -> {
                    if (this.player.isShiftKeyDown()) {
                        return;
                    }
                    var pos = this.context.armorStand.position();
                    this.context.armorStand.absSnapTo(pos.x, pos.y, pos.z, this.context.armorStand.getYRot() + this.context.moveRotationDelta * (y.isRight ? -1 : y.isLeft ? 1 : 0), 0);
                })
        );

        this.setSlot(4, baseElement(Items.GOLD_INGOT, TextUtils.gui("action.move", moveBase, TextUtils.direction(this.getDirection(true))), false)
                .setCallback((x, y, z, c) -> {
                    this.move(moveBase);
                })
        );

        this.setSlot(5, baseElement(Items.GOLD_NUGGET, TextUtils.gui("action.move", moveBase * 0.5, TextUtils.direction(this.getDirection(true))), false)
                .setCallback((x, y, z, c) -> {
                    this.move(moveBase * 0.5);
                })
        );

        this.setSlot(6, baseElement(Items.ENDER_PEARL, TextUtils.gui("action.move.teleport"), false)
                .setCallback((x, y, z, c) -> {
                    if (this.player.isShiftKeyDown()) {
                        return;
                    }
                    this.playClickSound();
                    this.context.armorStand.setPos(this.player.position());
                })
        );


        this.setSlot(7, baseElement(Items.ARROW, TextUtils.gui("action.move.rotate.copy_player"), false)
                .setCallback((x, y, z, c) -> {
                    if (this.player.isShiftKeyDown()) {
                        return;
                    }
                    this.playClickSound();
                    var pos = this.context.armorStand.position();
                    this.context.armorStand.absSnapTo(pos.x, pos.y, pos.z, this.player.getYRot(), 0);
                })
        );
    }

    private Direction getDirection(boolean positive) {
        if (this.currentAxis.axis == null) {
            return positive ? this.playerLookingDirection : this.playerLookingDirection.getOpposite();
        } else {
            return Direction.fromAxisAndDirection(this.currentAxis.axis, positive ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);
        }
    }

    @Override
    public boolean onSelectedSlotChange(int slot) {
        if (this.player.isShiftKeyDown()) {
            var current = this.getSelectedSlot();

            var delta = slot - current;

            if (current == 3) {
                this.context.moveRotationDelta = Mth.clamp(this.context.moveRotationDelta + delta, 0, 360);
                this.player.displayClientMessage(TextUtils.gui("action.move.rotate.set", this.context.moveRotationDelta), true);
            } else {
                double value;
                if ((this.context.moveBlockDelta == 0.1 && delta < 0) || this.context.moveBlockDelta < 0.1) {
                    value = ((int) (this.context.moveBlockDelta * 100d + delta)) / 100d;
                } else {
                    value = ((int) (this.context.moveBlockDelta * 10d + delta)) / 10d;
                }

                this.context.moveBlockDelta = Mth.clamp(value, 0, 5);
                this.player.displayClientMessage(TextUtils.gui("action.move.set", this.context.moveBlockDelta), true);

            }

            this.playSound(SoundEvents.NOTE_BLOCK_HAT, 0.5f, 1f);
            this.player.connection.send(new ClientboundSetHeldSlotPacket(this.selectedSlot));
            this.buildUi();

            return false;
        }

        return super.onSelectedSlotChange(slot);
    }

    private void move(double v) {
        if (this.player.isShiftKeyDown()) {
            return;
        }
        var pos = this.context.armorStand.position();
        if (this.currentAxis.axis != null) {
            this.context.armorStand.setPos(
                    pos.x + this.currentAxis.axis.choose(v, 0, 0),
                    pos.y + this.currentAxis.axis.choose(0, v, 0),
                    pos.z + this.currentAxis.axis.choose(0, 0, v)
            );
        } else {
            this.context.armorStand.setPos(
                    pos.x + this.playerLookingDirection.getStepX() * v,
                    pos.y + this.playerLookingDirection.getStepY() * v,
                    pos.z + this.playerLookingDirection.getStepZ() * v
            );
        }
        if (!CommonProtection.canInteractEntity(this.context.player.level(), this.context.armorStand, this.context.player.getGameProfile(), this.context.player)) {
            this.context.armorStand.setPos(pos);
        }
    }

    @Override
    protected EditingContext.SwitchEntry asSwitchableUi() {
        return new EditingContext.SwitchEntry(MoveGui::new, this.getSelectedSlot());
    }


    enum CurrentAxis {
        LOOK(null),
        X(Direction.Axis.X),
        Y(Direction.Axis.Y),
        Z(Direction.Axis.Z);

        private final Direction.Axis axis;

        CurrentAxis(Direction.Axis axis) {
            this.axis = axis;
        }
    }
}

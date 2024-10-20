package eu.pb4.armorstandeditor.gui;

import eu.pb4.armorstandeditor.EditorActions;
import eu.pb4.armorstandeditor.util.TextUtils;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ScaleGui extends BaseWorldGui {
    public ScaleGui(EditingContext context, int slot) {
        super(context, slot);
        this.rebuildUi();
        this.open();
    }

    @Override
    public void onTick() {
        super.onTick();
    }

    @Override
    protected void buildUi() {
        var moveBase = ((int) (this.context.scaleDelta * 100)) / 100d;

        this.setSlot(1, baseElement(Items.IRON_NUGGET, TextUtils.gui("action.scale.decrease", moveBase * 0.5f),false)
                .setCallback((x, y, z, c) -> {
                    this.changeScale(-moveBase * 0.5);
                })
        );

        this.setSlot(2, baseElement(Items.IRON_INGOT, TextUtils.gui("action.scale.decrease", moveBase), false)
                .setCallback((x, y, z, c) -> {
                    this.changeScale(-moveBase);
                })
        );

        this.updateMiddle();

        this.setSlot(4, baseElement(Items.GOLD_INGOT, TextUtils.gui("action.scale.increase", moveBase), false)
                .setCallback((x, y, z, c) -> {
                    this.changeScale(moveBase);
                })
        );

        this.setSlot(5, baseElement(Items.GOLD_NUGGET, TextUtils.gui("action.scale.increase", moveBase * 0.5), false)
                .setCallback((x, y, z, c) -> {
                    this.changeScale(moveBase * 0.5);
                })
        );

        this.setSlot(7, baseElement(Items.LEVER, "action.scale.reset", false)
                .setCallback((x, y, z) -> {
                    this.playSound(SoundEvents.UI_BUTTON_CLICK, 0.5f, 1f);
                    this.setScale(1);
                }));
    }

    private void updateMiddle() {
        this.setSlot(3, baseElement(Items.OAK_SIGN, TextUtils.gui("action.scale.value_or_set", String.format("%.02f", this.context.armorStand.getScale())), false)
                .setCallback((x, y, z) -> {
                    this.playSound(SoundEvents.UI_BUTTON_CLICK, 0.5f, 1f);
                    this.switchUi(EditingContext.SwitchEntry.ofChest(ScaleSetGui::new), true);
                })
        );
    }

    private void setScale(double scale) {
        this.context.armorStand.getAttributeInstance(EntityAttributes.SCALE).setBaseValue(scale);
        this.updateMiddle();
    }

    @Override
    public boolean onSelectedSlotChange(int slot) {
        if (this.player.isSneaking()) {
            var current = this.getSelectedSlot();

            var delta = slot - current;

            double value;
            if ((this.context.scaleDelta == 0.1 && delta < 0) || this.context.scaleDelta < 0.1) {
                value = ((int) (this.context.scaleDelta * 100d + delta)) / 100d;
            } else {
                value = ((int) (this.context.scaleDelta * 10d + delta)) / 10d;
            }

            this.context.scaleDelta = MathHelper.clamp(value, 0, 8);
            this.player.sendMessage(TextUtils.gui("action.scale.set_change", this.context.scaleDelta), true);
            this.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HAT, 0.5f, 1f);
            this.player.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(this.selectedSlot));
            this.buildUi();

            return false;
        }

        return super.onSelectedSlotChange(slot);
    }

    private void changeScale(double v) {
        if (this.player.isSneaking() || this.context == null) {
            return;
        }
        setScale(v + this.context.armorStand.getScale());
    }

    @Override
    protected EditingContext.SwitchEntry asSwitchableUi() {
        return new EditingContext.SwitchEntry(ScaleGui::new, this.getSelectedSlot());
    }
}

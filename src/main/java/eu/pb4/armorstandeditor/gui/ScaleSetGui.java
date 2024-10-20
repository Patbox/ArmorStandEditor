package eu.pb4.armorstandeditor.gui;

import eu.pb4.armorstandeditor.config.ConfigManager;
import eu.pb4.armorstandeditor.util.TextUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.Locale;

public class ScaleSetGui extends BaseAnvilGui {
    private double scaleValue;

    public ScaleSetGui(EditingContext context, int selectedSlot) {
        super(context, false);
        this.scaleValue = context.armorStand.getScale();
        this.rebuildUi();
        this.open();
    }
    private void setScale(double scale) {
        this.context.armorStand.getAttributeInstance(EntityAttributes.SCALE).setBaseValue(scale);
    }
    @Override
    protected void buildUi() {
        this.setTitle(TextUtils.gui("action.scale.set.title"));
        this.setDefaultInputValue(String.valueOf(context.armorStand.getScale()));

        ItemStack stack = Items.MAGMA_CREAM.getDefaultStack();
        stack.set(DataComponentTypes.CUSTOM_NAME, TextUtils.gui("action.scale.reset").setStyle(Style.EMPTY.withItalic(false)));

        this.setSlot(1, stack, (a, b, c, d) -> {
            this.playClickSound();
            setScale(1);
            this.openPreviousOrClose();
        });

        this.updateSlot2();
    }

    @Override
    public void onInput(String input) {
        super.onInput(input);
        try {
            var cfg = ConfigManager.getConfig().configData;
            this.scaleValue = MathHelper.clamp(Float.parseFloat(input), cfg.minimumScaleValue, cfg.maximalScaleValue);
            updateSlot2();
        } catch (Throwable ignored) {
            ItemStack stack2 = Items.SNOWBALL.getDefaultStack();
            stack2.set(DataComponentTypes.CUSTOM_NAME, TextUtils.gui("action.scale.set.invalid", input).setStyle(Style.EMPTY.withItalic(false)));
            this.setSlot(2, stack2, (a, b, c, d) -> {
                this.playClickSound();
                this.openPreviousOrClose();
            });
        }

    }

    private void updateSlot2() {
        ItemStack stack2 = Items.SLIME_BALL.getDefaultStack();
        stack2.set(DataComponentTypes.CUSTOM_NAME, TextUtils.gui("action.scale.set.set", this.scaleValue).setStyle(Style.EMPTY.withItalic(false)));
        this.setSlot(2, stack2, (a, b, c, d) -> {
            this.playClickSound();
            setScale(this.scaleValue);
            this.openPreviousOrClose();
        });
    }

    @Override
    protected EditingContext.SwitchEntry asSwitchableUi() {
        return EditingContext.SwitchEntry.ofChest(ScaleSetGui::new);
    }
}

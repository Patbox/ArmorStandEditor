package eu.pb4.armorstandeditor.gui;

import eu.pb4.armorstandeditor.config.ConfigManager;
import eu.pb4.armorstandeditor.util.TextUtils;
import java.util.Locale;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ScaleSetGui extends BaseAnvilGui {
    private double scaleValue;

    public ScaleSetGui(EditingContext context, int selectedSlot) {
        super(context, false);
        this.scaleValue = context.armorStand.getScale();
        this.rebuildUi();
        this.open();
    }
    private void setScale(double scale) {
        this.context.armorStand.getAttribute(Attributes.SCALE).setBaseValue(scale);
    }
    @Override
    protected void buildUi() {
        this.setTitle(TextUtils.gui("action.scale.set.title"));
        this.setDefaultInputValue(String.valueOf(context.armorStand.getScale()));

        ItemStack stack = Items.MAGMA_CREAM.getDefaultInstance();
        stack.set(DataComponents.CUSTOM_NAME, TextUtils.gui("action.scale.reset").setStyle(Style.EMPTY.withItalic(false)));

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
            this.scaleValue = Mth.clamp(Float.parseFloat(input), cfg.minimumScaleValue, cfg.maximalScaleValue);
            updateSlot2();
        } catch (Throwable ignored) {
            ItemStack stack2 = Items.SNOWBALL.getDefaultInstance();
            stack2.set(DataComponents.CUSTOM_NAME, TextUtils.gui("action.scale.set.invalid", input).setStyle(Style.EMPTY.withItalic(false)));
            this.setSlot(2, stack2, (a, b, c, d) -> {
                this.playClickSound();
                this.openPreviousOrClose();
            });
        }

    }

    private void updateSlot2() {
        ItemStack stack2 = Items.SLIME_BALL.getDefaultInstance();
        stack2.set(DataComponents.CUSTOM_NAME, TextUtils.gui("action.scale.set.set", this.scaleValue).setStyle(Style.EMPTY.withItalic(false)));
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

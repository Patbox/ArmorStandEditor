package eu.pb4.armorstandeditor.gui;

import eu.pb4.armorstandeditor.util.TextUtils;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class RenameGui extends BaseAnvilGui {
    public RenameGui(EditingContext context, int selectedSlot) {
        super(context, false);
        this.rebuildUi();

        this.open();
    }

    @Override
    protected void buildUi() {
        this.setTitle(TextUtils.gui("rename_title"));
        this.setDefaultInputValue(context.armorStand.getCustomName() != null ? context.armorStand.getCustomName().getString() : "");

        ItemStack stack = Items.MAGMA_CREAM.getDefaultStack();
        stack.set(DataComponentTypes.CUSTOM_NAME, TextUtils.gui("clearname").setStyle(Style.EMPTY.withItalic(false)));

        this.setSlot(1, stack, (a, b, c, d) -> {
            context.armorStand.setCustomName(null);
            context.armorStand.setCustomNameVisible(false);
            this.playClickSound();
            this.openPreviousOrClose();
        });

        this.updateSlot2();
    }

    @Override
    public void onInput(String input) {
        super.onInput(input);
        updateSlot2();
    }

    private void updateSlot2() {
        ItemStack stack2 = Items.SLIME_BALL.getDefaultStack();
        stack2.set(DataComponentTypes.CUSTOM_NAME, TextUtils.gui("setname", this.getInput()).setStyle(Style.EMPTY.withItalic(false)));
        this.setSlot(2, stack2, (a, b, c, d) -> {
            this.playClickSound();
            context.armorStand.setCustomName(this.getInput().isEmpty() ? null : Text.literal(this.getInput()));
            context.armorStand.setCustomNameVisible(!this.getInput().isEmpty());
            this.openPreviousOrClose();
        });
    }

    @Override
    protected EditingContext.SwitchEntry asSwitchableUi() {
        return EditingContext.SwitchEntry.ofChest(RenameGui::new);
    }
}

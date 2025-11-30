package eu.pb4.armorstandeditor.gui;

import eu.pb4.armorstandeditor.config.ArmorStandPreset;
import eu.pb4.armorstandeditor.config.PlayerData;
import eu.pb4.armorstandeditor.util.PlayerExt;
import eu.pb4.armorstandeditor.util.TextUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PresetSaveGui extends BaseAnvilGui {
    public PresetSaveGui(EditingContext context, int selectedSlot) {
        super(context, false);
        this.rebuildUi();

        this.open();
    }

    @Override
    protected void buildUi() {
        this.setTitle(TextUtils.gui("entry.preset_save"));
        this.setDefaultInputValue("My Preset");
        this.setSlot(2, closeButton());
        this.updateSlot();
    }

    @Override
    public void onInput(String input) {
        super.onInput(input);
        updateSlot();
    }

    private void updateSlot() {
        ItemStack stack2 = Items.SLIME_BALL.getDefaultInstance();
        stack2.set(DataComponents.CUSTOM_NAME, TextUtils.gui("entry.preset_save.save", this.getInput()).setStyle(Style.EMPTY.withItalic(false)));
        this.setSlot(1, stack2, (a, b, c, d) -> {
            this.playClickSound();
            var preset = new ArmorStandPreset(null, this.getInput(), this.player.getGameProfile().name());
            preset.fromData(((PlayerExt) this.player).ase$getArmorStandEditorData());
            var data = PlayerData.get(this.player);
            data.presets.add(preset);
            data.save(player);
            this.openPreviousOrClose();
        });
    }

    @Override
    protected EditingContext.SwitchEntry asSwitchableUi() {
        return EditingContext.SwitchEntry.ofChest(PresetSaveGui::new);
    }
}

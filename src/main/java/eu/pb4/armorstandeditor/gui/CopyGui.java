package eu.pb4.armorstandeditor.gui;

import eu.pb4.armorstandeditor.legacy.LegacyEditorGuis;
import eu.pb4.armorstandeditor.legacy.LegacyPlayerExt;
import eu.pb4.armorstandeditor.util.ArmorStandData;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class CopyGui extends BaseGui {
    public CopyGui(EditingContext context, int slot) {
        super(context, slot);
        this.rebuildUi();
        this.open();
    }

    @Override
    protected void buildUi() {
        var spei = (LegacyPlayerExt) this.player;

        this.setSlot(0,
                baseElement(Items.SLIME_BALL, "action.copy", false)
                        .setCallback((x, y, z, c) -> {
                            this.playClickSound();

                            spei.aselegacy$setArmorStandEditorData(new ArmorStandData(this.context.armorStand));
                            player.sendMessage(Text.translatable("text.armor_stand_editor.message.copied"), true);
                            this.rebuildUi();
                        })
        );

        if (spei.aselegacy$getArmorStandEditorData() != null) {
            this.setSlot(1,
                    baseElement(Items.MAGMA_CREAM, "action.paste", false)
                            .setCallback((x, y, z, c) -> {
                                this.playClickSound();

                                spei.aselegacy$getArmorStandEditorData().apply(this.context.armorStand, false);
                                player.sendMessage(Text.translatable("text.armor_stand_editor.message.pasted"), true);
                            })
            );

            if (this.player.isCreative()) {
                this.setSlot(2,
                        baseElement(Items.MAGMA_CREAM, "action.paste.inventory", false)
                                .setCallback((x, y, z, c) -> {
                                    if (this.player.isCreative()) {
                                        this.playClickSound();

                                        spei.aselegacy$getArmorStandEditorData().apply(this.context.armorStand, true);
                                        player.sendMessage(Text.translatable("text.armor_stand_editor.message.pasted"), true);
                                    }
                                })
                );
            }
        }

        this.setSlot(7,
                switchElement(Items.ARMOR_STAND, "presets", LegacyEditorGuis.getPresetsGui())
        );
    }

    @Override
    protected SwitchEntry asSwitchableUi() {
        return new SwitchEntry(CopyGui::new, this.getSelectedSlot());
    }
}

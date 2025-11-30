package eu.pb4.armorstandeditor.gui;

import eu.pb4.armorstandeditor.config.ConfigManager;
import eu.pb4.armorstandeditor.config.PlayerData;
import eu.pb4.armorstandeditor.util.ArmorStandData;
import eu.pb4.armorstandeditor.util.PlayerExt;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public class CopyGui extends BaseWorldGui {
    public CopyGui(EditingContext context, int slot) {
        super(context, slot);
        this.rebuildUi();
        this.open();
    }

    @Override
    protected void buildUi() {
        var spei = (PlayerExt) this.player;

        this.setSlot(0,
                baseElement(Items.SLIME_BALL, "action.copy", false)
                        .setCallback((x, y, z, c) -> {
                            this.playClickSound();

                            spei.ase$setArmorStandEditorData(new ArmorStandData(this.context.armorStand));
                            player.displayClientMessage(Component.translatable("text.armor_stand_editor.message.copied"), true);
                            this.rebuildUi();
                        })
        );

        if (spei.ase$getArmorStandEditorData() != null) {
            this.setSlot(1,
                    baseElement(Items.MAGMA_CREAM, "action.paste", false)
                            .setCallback((x, y, z, c) -> {
                                this.playClickSound();

                                spei.ase$getArmorStandEditorData().apply(this.context.armorStand, false);
                                player.displayClientMessage(Component.translatable("text.armor_stand_editor.message.pasted"), true);
                            })
            );

            if (this.player.isCreative() && spei.ase$getArmorStandEditorData().hasInventory) {
                this.setSlot(2,
                        baseElement(Items.MAGMA_CREAM, "action.paste.inventory", false)
                                .setCallback((x, y, z, c) -> {
                                    if (this.player.isCreative()) {
                                        this.playClickSound();

                                        spei.ase$getArmorStandEditorData().apply(this.context.armorStand, true);
                                        player.displayClientMessage(Component.translatable("text.armor_stand_editor.message.pasted"), true);
                                    }
                                })
                );
            }

            if (PlayerData.get(player).presets.size() < ConfigManager.getConfig().configData.presetLimit) {
                this.setSlot(5,
                        switchElement(Items.WRITABLE_BOOK, "preset_save", PresetSaveGui::new)
                );
            }
        }


        this.setSlot(6,
                switchElement(Items.BOOK, "player_presets", PresetSelectorGui::playerPresets)
        );
        this.setSlot(7,
                switchElement(Items.ARMOR_STAND, "global_presets", PresetSelectorGui::globalPresets)
        );
    }

    @Override
    protected EditingContext.SwitchEntry asSwitchableUi() {
        return new EditingContext.SwitchEntry(CopyGui::new, this.getSelectedSlot());
    }
}

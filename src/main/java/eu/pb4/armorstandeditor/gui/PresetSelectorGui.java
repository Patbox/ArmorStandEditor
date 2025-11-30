package eu.pb4.armorstandeditor.gui;

import eu.pb4.armorstandeditor.config.ArmorStandPreset;
import eu.pb4.armorstandeditor.config.ConfigManager;
import eu.pb4.armorstandeditor.config.PlayerData;
import eu.pb4.armorstandeditor.util.PlayerExt;
import eu.pb4.armorstandeditor.util.TextUtils;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;

public class PresetSelectorGui extends BaseChestGui {
    private final ArrayList<ArmorStandPreset> presets;
    private final Supplier<Collection<ArmorStandPreset>> presetSupplier;
    private final Consumer<ArmorStandPreset> remover;
    private int page = 0;

    public static void globalPresets(EditingContext context, int slot) {
        new PresetSelectorGui(context, ConfigManager.PRESETS::values, null);
    }

    public static void playerPresets(EditingContext context, int slot) {
        var data = PlayerData.get(context.player);
        new PresetSelectorGui(context, () -> data.presets, (p) -> {
            data.presets.remove(p);
            data.save(context.player);
        });
    }

    public PresetSelectorGui(EditingContext context, Supplier<Collection<ArmorStandPreset>> supplier, Consumer<ArmorStandPreset> remover) {
        super(context, MenuType.GENERIC_9x3, false);
        this.setTitle(TextUtils.gui("presets_title"));
        this.presets = new ArrayList<>(supplier.get());
        this.presetSupplier = supplier;
        this.remover = remover;

        this.buildUi();
        this.open();
    }

    @Override
    protected void buildUi() {
        for (int x = 0; x < 9; x++) {
            this.setSlot(x + 18, new GuiElementBuilder(Items.GRAY_STAINED_GLASS_PANE).hideTooltip());
        }
        updateDisplay();
    }

    private void updateDisplay() {
        final int maxPage = Math.ceilDiv(this.presets.size(), 2 * 9);

        for (int x = 0; x < 18; x++) {
            this.clearSlot(x);

            if (page * 18 + x < this.presets.size()) {
                final ArmorStandPreset preset = presets.get(page * 18 + x);
                var b = new GuiElementBuilder(Items.ARMOR_STAND)
                        .setName(Component.literal(preset.name).setStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.GREEN)))
                        .addLoreLine(TextUtils.gui("preset_author", preset.author).setStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.GRAY)))
                        .setCallback((t1, t2, t3) -> {
                            playClickSound();
                            if (remover != null && t2.isRight && t2.shift) {
                                remover.accept(preset);
                                this.presets.clear();
                                this.presets.addAll(this.presetSupplier.get());
                                rebuildUi();
                                return;
                            }

                            ((PlayerExt) this.player).ase$setArmorStandEditorData(preset.asData());
                            player.displayClientMessage(TextUtils.text("presets.selected", preset.name), true);
                            this.openPreviousOrClose();
                        });

                if (this.remover != null) {
                    b.addLoreLine(TextUtils.gui("presets.remove").withStyle(ChatFormatting.RED));
                }

                this.setSlot(x, b);
            }
        }

        this.setSlot(this.size - 5, closeButton());

        var previous = new GuiElementBuilder(page != 0 ? Items.ARROW : Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                .setName(Component.translatable("spectatorMenu.previous_page").setStyle(Style.EMPTY.withItalic(false)));
        if (this.page != 0) {
            previous.setCallback((index, type, action) -> {
                page--;
                playClickSound();
                updateDisplay();
            });
        }
        this.setSlot(this.size - 8, previous);

        var next = new GuiElementBuilder(page < maxPage - 1 ? Items.ARROW : Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                .setName(Component.translatable("spectatorMenu.next_page").setStyle(Style.EMPTY.withItalic(false)));

        if (this.page < maxPage - 1) {
            next.setCallback((index, type, action) -> {
                page++;
                playClickSound();
                updateDisplay();
            });
        }
        this.setSlot(this.size - 2, next);
    }

    @Override
    protected EditingContext.SwitchEntry asSwitchableUi() {
        var preset = this.presets;
        return EditingContext.SwitchEntry.ofChest((x, y) -> new PresetSelectorGui(x, this.presetSupplier, this.remover));
    }
}

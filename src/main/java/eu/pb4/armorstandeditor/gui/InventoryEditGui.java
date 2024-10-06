package eu.pb4.armorstandeditor.gui;

import eu.pb4.armorstandeditor.mixin.ArmorStandEntityAccessor;
import eu.pb4.armorstandeditor.util.ArmorStandInventory;
import eu.pb4.armorstandeditor.util.TextUtils;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class InventoryEditGui extends BaseChestGui {
    public InventoryEditGui(EditingContext context, int slot) {
        super(context, ScreenHandlerType.GENERIC_9X2, false);
        this.rebuildUi();
        this.open();
    }

    public static boolean isSlotUnlocked(ArmorStandEntity armorStandEntity, EquipmentSlot slot) {
        return (((ArmorStandEntityAccessor) armorStandEntity).getDisabledSlots() & 1 << slot.getArmorStandSlotId()) == 0;
    }

    @Override
    public void onTick() {
        if (context.checkClosed()) {
            this.close();
        }
        super.onTick();
    }

    @Override
    protected void buildUi() {
        this.setTitle(TextUtils.gui("inventory_title"));

        ArmorStandInventory inventory = new ArmorStandInventory(this.context.armorStand);
        for (int x = 0; x < inventory.size(); x++) {
            this.setSlotRedirect(x, new Slot(inventory, x, 0, 0));
            ArmorStandEntity ae = context.armorStand;
            ArmorStandEntityAccessor asea = (ArmorStandEntityAccessor) ae;
            boolean isUnlocked = isSlotUnlocked(ae, ArmorStandInventory.getEquipmentSlot(x));
            this.setSlot(x + 9, new GuiElementBuilder(isUnlocked ? Items.GREEN_STAINED_GLASS_PANE : Items.RED_STAINED_GLASS_PANE)
                    .setName(Text.translatable(isUnlocked ? "narrator.button.difficulty_lock.unlocked" : "narrator.button.difficulty_lock.locked")
                            .setStyle(Style.EMPTY.withItalic(false)))
                    .setCallback((index, type, action) -> {
                        EquipmentSlot slot = ArmorStandInventory.getEquipmentSlot(index - 9);

                        int disabledSlots = asea.getDisabledSlots();

                        boolean isUnlockedTmp = isSlotUnlocked(ae, slot);

                        if (isUnlockedTmp) {
                            disabledSlots |= 1 << slot.getArmorStandSlotId();
                            disabledSlots |= 1 << slot.getArmorStandSlotId() + 8;
                            disabledSlots |= 1 << slot.getArmorStandSlotId() + 16;
                        } else {
                            disabledSlots &= ~(1 << slot.getArmorStandSlotId());
                            disabledSlots &= ~(1 << slot.getArmorStandSlotId() + 8);
                            disabledSlots &= ~(1 << slot.getArmorStandSlotId() + 16);
                        }

                        asea.setDisabledSlots(disabledSlots);

                        playClickSound();

                        boolean isUnlocked2 = isSlotUnlocked(ae, slot);

                        ItemStack stack = new ItemStack(isUnlocked2 ? Items.GREEN_STAINED_GLASS_PANE : Items.RED_STAINED_GLASS_PANE);

                        stack.set(DataComponentTypes.CUSTOM_NAME, Text.translatable(isUnlocked2 ? "narrator.button.difficulty_lock.unlocked" : "narrator.button.difficulty_lock.locked")
                                .setStyle(Style.EMPTY.withItalic(false)));

                        ((GuiElement) this.getSlot(index)).setItemStack(stack);
                    })
            );
        }

        GuiElement empty = new GuiElementBuilder(Items.GRAY_STAINED_GLASS_PANE).hideTooltip().build();

        this.setSlot(6, empty);
        this.setSlot(7, empty);
        this.setSlot(8, empty);

        this.setSlot(15, empty);
        this.setSlot(16, empty);
        this.setSlot(17, closeButton());
    }

    @Override
    protected EditingContext.SwitchEntry asSwitchableUi() {
        return EditingContext.SwitchEntry.ofChest(RenameGui::new);
    }
}

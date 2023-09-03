package eu.pb4.armorstandeditor.legacy;

import eu.pb4.armorstandeditor.EditorActions;
import eu.pb4.armorstandeditor.config.ArmorStandPreset;
import eu.pb4.armorstandeditor.config.ConfigManager;
import eu.pb4.armorstandeditor.gui.BaseGui;
import eu.pb4.armorstandeditor.mixin.ArmorStandEntityAccessor;
import eu.pb4.armorstandeditor.mixin.ItemFrameEntityAccessor;
import eu.pb4.armorstandeditor.util.ArmorStandInventory;
import eu.pb4.armorstandeditor.util.ItemFrameInventory;
import eu.pb4.armorstandeditor.util.TextUtils;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LegacyEditorGuis {
    public static void openRenaming(ServerPlayerEntity player, Entity entity, Runnable callback) {
        ItemStack stack = Items.MAGMA_CREAM.getDefaultStack();
        stack.setCustomName(TextUtils.gui("clearname").setStyle(Style.EMPTY.withItalic(false)));

        ItemStack stack2 = Items.SLIME_BALL.getDefaultStack();
        stack2.setCustomName(TextUtils.gui("setname").setStyle(Style.EMPTY.withItalic(false)));

        AnvilInputGui gui = new AnvilInputGui(player, false) {
            @Override
            public void onInput(String input) {
                super.onInput(input);
                stack2.setCustomName(TextUtils.gui("setname", this.getInput()).setStyle(Style.EMPTY.withItalic(false)));
                this.setSlot(2, stack2, (index, type, action) -> {
                    entity.setCustomName(Text.literal(this.getInput()));
                    entity.setCustomNameVisible(true);
                    this.close(false);
                });
            }

            @Override
            public void onClose() {
                callback.run();
            }
        };

        gui.setTitle(TextUtils.gui("rename_title"));
        gui.setDefaultInputValue(entity.getCustomName() != null ? entity.getCustomName().getString() : "");


        gui.setSlot(1, stack, (index, type, action) -> {
            entity.setCustomName(Text.literal(""));
            entity.setCustomNameVisible(false);
            gui.close(false);
        });

        gui.setSlot(2, stack2, (index, type, action) -> {
            entity.setCustomName(Text.literal(gui.getInput()));
            entity.setCustomNameVisible(true);
            gui.close(false);
        });

        gui.open();
    }

    public static boolean isSlotUnlocked(ArmorStandEntity armorStandEntity, EquipmentSlot slot) {
        return (((ArmorStandEntityAccessor) armorStandEntity).getDisabledSlots() & 1 << slot.getArmorStandSlotId()) == 0;
    }

    public static void openInventoryEditor(ServerPlayerEntity player, LivingEntity entity, Runnable callback) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X2, player, false) {
            @Override
            public void onClose() {
                callback.run();
            }
            @Override
            public boolean onAnyClick(int index, ClickType type, SlotActionType action) {
                if(entity.isRemoved()){
                    close();
                    return false;
                }
                return super.onAnyClick(index, type, action);
            }
        };

        ArmorStandInventory inventory = new ArmorStandInventory(entity);

        gui.setTitle(TextUtils.gui("inventory_title"));
        for (int x = 0; x < inventory.size(); x++) {
            gui.setSlotRedirect(x, new Slot(inventory, x, 0, 0));
            if (entity instanceof ArmorStandEntity) {
                ArmorStandEntity ae = (ArmorStandEntity) entity;
                ArmorStandEntityAccessor asea = (ArmorStandEntityAccessor) ae;
                boolean isUnlocked = isSlotUnlocked(ae, ArmorStandInventory.getEquipmentSlot(x));
                gui.setSlot(x + 9, new GuiElementBuilder(isUnlocked ? Items.GREEN_STAINED_GLASS_PANE : Items.RED_STAINED_GLASS_PANE)
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

                            boolean isUnlocked2 = isSlotUnlocked(ae, slot);

                            ItemStack stack = new ItemStack(isUnlocked2 ? Items.GREEN_STAINED_GLASS_PANE : Items.RED_STAINED_GLASS_PANE);

                            stack.setCustomName(Text.translatable(isUnlocked2 ? "narrator.button.difficulty_lock.unlocked" : "narrator.button.difficulty_lock.locked")
                                    .setStyle(Style.EMPTY.withItalic(false)));

                            ((GuiElement) gui.getSlot(index)).setItemStack(stack);
                        })
                );
            } else {
                gui.setSlot(x + 9, new GuiElementBuilder(Items.RED_STAINED_GLASS_PANE)
                        .setName(TextUtils.gui("cantlockslots")
                                .setStyle(Style.EMPTY.withItalic(false).withColor(Formatting.RED))));
            }
        }

        GuiElement empty = new GuiElementBuilder(Items.GRAY_STAINED_GLASS_PANE).setName(Text.literal("")).build();

        gui.setSlot(6, empty);
        gui.setSlot(7, empty);
        gui.setSlot(8, empty);

        gui.setSlot(15, empty);
        gui.setSlot(16, empty);
        gui.setSlot(17, new GuiElementBuilder(Items.BARRIER)
                .setName(TextUtils.gui("close").setStyle(Style.EMPTY.withItalic(false)))
                .setCallback(((index, type, action) -> {
                    gui.close();
                }))
        );

        gui.open();
    }

    public static void openGui(ServerPlayerEntity player) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X5, player, false) {
            @Override
            public boolean onClick(int index, ClickType type, SlotActionType action, GuiElementInterface element) {
                setIcons(player, this);
                return super.onClick(index, type, action, element);
            }
        };
        gui.setTitle(TextUtils.gui("editor_title"));
        setIcons(player, gui);
        gui.open();
    }

    private static void setIcons(ServerPlayerEntity player, SimpleGui gui) {
        createIcon(player, gui, 0, Items.RED_WOOL, "x", 0);
        createIcon(player, gui, 1, Items.GREEN_WOOL, "y", 1);
        createIcon(player, gui, 2, Items.BLUE_WOOL, "z", 2);

        createIconCustomPower(player, gui, 5, Items.GOLD_INGOT);
        createIcon(player, gui, 6, Items.IRON_NUGGET, "small", 0.01f);
        createIcon(player, gui, 7, Items.IRON_INGOT, "medium", 0.1f);
        createIcon(player, gui, 8, Items.IRON_BLOCK, "big", 1f);

        createIcon(player, gui, 9, Items.MINECART, "move", EditorActions.MOVE);
        createIcon(player, gui, 10, Items.COMPASS, "rotate", EditorActions.ROTATE);
        createIcon(player, gui, 24, Items.PUFFERFISH, "size", EditorActions.TOGGLE_SIZE);
        createIcon(player, gui, 25, Items.APPLE, "gravity", EditorActions.TOGGLE_GRAVITY);
        createIcon(player, gui, 26, Items.GLASS, "visibility", EditorActions.TOGGLE_VISIBILITY);
        createIcon(player, gui, 34, Items.STICK, "arms", EditorActions.TOGGLE_ARMS);
        createIcon(player, gui, 35, Items.STONE_SLAB, "base", EditorActions.TOGGLE_BASE);

        createIcon(player, gui, 19, Items.LEATHER_HELMET, "head", EditorActions.MODIFY_HEAD);
        createIcon(player, gui, 27, Items.STICK, "left_arm", EditorActions.MODIFY_LEFT_ARM);
        createIcon(player, gui, 28, Items.LEATHER_CHESTPLATE, "body", EditorActions.MODIFY_BODY);
        createIcon(player, gui, 29, Items.STICK, "right_arm", EditorActions.MODIFY_RIGHT_ARM);

        createIcon(player, gui, 21, Items.GLISTERING_MELON_SLICE, "flip_pose", EditorActions.FLIP_POSE);
        createIcon(player, gui, 30, Items.LEVER, "reset_pose", EditorActions.RESET_POSE);

        createIcon(player, gui, 36, Items.LEATHER_BOOTS, "left_leg", EditorActions.MODIFY_LEFT_LEG);
        createIcon(player, gui, 37, Items.CHEST, "inventory", EditorActions.INVENTORY);
        createIcon(player, gui, 38, Items.LEATHER_BOOTS, "right_leg", EditorActions.MODIFY_RIGHT_LEG);
        createIcon(player, gui, 39, Items.NAME_TAG, "rename", EditorActions.RENAME);

        createIcon(player, gui, 43, Items.SLIME_BALL, "copy", EditorActions.COPY);
        createIcon(player, gui, 44, Items.MAGMA_CREAM, "paste", EditorActions.PASTE);

        gui.setSlot(42, new GuiElementBuilder(Items.MOJANG_BANNER_PATTERN)
                .setName(TextUtils.gui("name.legacy.presets").setStyle(Style.EMPTY.withItalic(false)))
                .hideFlags()
                .setCallback((x, y, z) -> openPresetSelector(player, () -> LegacyEditorGuis.openGui(player))
                ));
    }

    private static void createIcon(ServerPlayerEntity player, SimpleGui gui, int index, Item item, String text, int xyz) {
        ItemStack itemStack = item.getDefaultStack();
        itemStack.setCustomName(TextUtils.gui("name.legacy." + text).setStyle(Style.EMPTY.withItalic(false)));
        itemStack.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);
        itemStack.addHideFlag(ItemStack.TooltipSection.MODIFIERS);

        if (((LegacyPlayerExt) player).aselegacy$getArmorStandEditorXYZ() == xyz) {
            itemStack.addEnchantment(Enchantments.POWER, 1);
        }

        gui.setSlot(index, itemStack, (index2, type, actionType) -> {
            ((LegacyPlayerExt) player).aselegacy$setArmorStandEditorXYZ(xyz);
        });
    }

    private static void createIcon(ServerPlayerEntity player, SimpleGui gui, int index, Item item, String text, EditorActions action) {
        if (!action.canUse(player)) {
            return;
        }

        ItemStack itemStack = item.getDefaultStack();
        itemStack.setCustomName(TextUtils.gui("name.legacy." + text).setStyle(Style.EMPTY.withItalic(false)));
        itemStack.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);
        itemStack.addHideFlag(ItemStack.TooltipSection.MODIFIERS);

        if (((LegacyPlayerExt) player).aselegacy$getArmorStandEditorAction() == action) {
            itemStack.addEnchantment(Enchantments.POWER, 1);
        }

        gui.setSlot(index, itemStack, (index2, type, actionType) -> {
            ((LegacyPlayerExt) player).aselegacy$setArmorStandEditorAction(action);
        });
    }

    private static void createIcon(ServerPlayerEntity player, SimpleGui gui, int index, Item item, String text, float power) {
        ItemStack itemStack = item.getDefaultStack();
        itemStack.setCustomName(TextUtils.gui("name.legacy." + text).setStyle(Style.EMPTY.withItalic(false)));

        NbtList lore = new NbtList();
        lore.add(NbtString.of(Text.Serializer.toJson(
                TextUtils.gui("legacy.blocksdeg", (Math.round(power * 100) / 100f), Math.floor(power * 3000) / 100).setStyle(Style.EMPTY.withItalic(false).withColor(Formatting.GRAY))
        )));

        itemStack.getOrCreateNbt().getCompound("display").put("Lore", lore);

        itemStack.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);
        itemStack.addHideFlag(ItemStack.TooltipSection.MODIFIERS);

        if (((LegacyPlayerExt) player).aselegacy$getArmorStandEditorPower() == power) {
            itemStack.addEnchantment(Enchantments.POWER, 1);
        }

        gui.setSlot(index, itemStack, (index2, type, actionType) -> {
            ((LegacyPlayerExt) player).aselegacy$setArmorStandEditorPower(power);
        });
    }

    private static void createIconCustomPower(ServerPlayerEntity player, SimpleGui gui, int index, Item item) {
        ItemStack itemStack = item.getDefaultStack();
        itemStack.setCustomName(TextUtils.gui("name.legacy.custom_change").setStyle(Style.EMPTY.withItalic(false)));

        LegacyPlayerExt spe = (LegacyPlayerExt) player;
        float power = spe.aselegacy$getArmorStandEditorPower();

        NbtList lore = new NbtList();
        lore.add(NbtString.of(Text.Serializer.toJson(
                TextUtils.gui("legacy.blocksdeg", (Math.round(power * 100) / 100f), Math.floor(power * 3000) / 100).setStyle(Style.EMPTY.withItalic(false).withColor(Formatting.GRAY))
        )));
        itemStack.getOrCreateNbt().getCompound("display").put("Lore", lore);

        itemStack.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);
        itemStack.addHideFlag(ItemStack.TooltipSection.MODIFIERS);

        if (power != 1f && power != 0.01f && power != 0.1f) {
            itemStack.addEnchantment(Enchantments.POWER, 1);
        }

        gui.setSlot(index, itemStack, (index2, type, actionType) -> {
            if (!type.isMiddle) {
                float tmp = power + (0.01f * (type.shift ? 10 : 1) * (type.isLeft ? -1 : 1));
                float value = (Math.round(tmp * 100) / 100f);
                if (value > 0 && value < 5) {
                    spe.aselegacy$setArmorStandEditorPower(value);
                }
            }
        });
    }

    public static void openPresetSelector(ServerPlayerEntity player, Runnable runnable) {
        List<ArmorStandPreset> presets = new ArrayList<>(ConfigManager.PRESETS.values());

        final int presetsSize = presets.size();

        AtomicInteger page = new AtomicInteger();
        final int maxPage = (int) Math.ceil((double) presetsSize / 18);

        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false) {
            @Override
            public void onOpen() {
                super.onOpen();
                for (int x = 0; x < 9; x++) {
                    this.setSlot(x + 18, new GuiElementBuilder(Items.GRAY_STAINED_GLASS_PANE).setName(Text.literal("")));
                }
                updateDisplay();
            }

            private void updateDisplay() {
                for (int x = 0; x < 18; x++) {
                    this.clearSlot(x);

                    if (page.get() * 18 + x < presetsSize) {
                        final ArmorStandPreset preset = presets.get(page.get() * 18 + x);

                        this.setSlot(x, new GuiElementBuilder(Items.ARMOR_STAND)
                                .setName(Text.literal(preset.name).setStyle(Style.EMPTY.withItalic(false).withColor(Formatting.GREEN)))
                                .addLoreLine(TextUtils.gui("preset_author", preset.author).setStyle(Style.EMPTY.withItalic(false).withColor(Formatting.GRAY)))

                                .setCallback((t1, t2, t3) -> {
                                    ((LegacyPlayerExt) this.player).aselegacy$setArmorStandEditorData(preset.asData());
                                    ((LegacyPlayerExt) this.player).aselegacy$setArmorStandEditorAction(EditorActions.PASTE);
                                    player.sendMessage(TextUtils.text("presets.selected", preset.name), true);
                                    this.close();
                                }));
                    }
                }

                this.setSlot(this.size - 5, new GuiElementBuilder(Items.BARRIER)
                        .setName(Text.translatable("dataPack.validation.back").setStyle(Style.EMPTY.withItalic(false)))
                        .setCallback((index, type, action) -> {
                            this.close();
                        }));

                this.setSlot(this.size - 8, new GuiElementBuilder(page.get() != 0 ? Items.ARROW : Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                        .setName(Text.translatable("spectatorMenu.previous_page").setStyle(Style.EMPTY.withItalic(false)))
                        .setCallback((index, type, action) -> {
                            if (page.addAndGet(-1) < 0) {
                                page.set(0);
                            }
                            updateDisplay();
                        }));
                this.setSlot(this.size - 2, new GuiElementBuilder(page.get() != maxPage - 1 ? Items.ARROW : Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                        .setName(Text.translatable("spectatorMenu.next_page").setStyle(Style.EMPTY.withItalic(false)))
                        .setCallback((index, type, action) -> {
                            if (page.addAndGet(1) >= maxPage) {
                                page.set(maxPage - 1);
                            }
                            updateDisplay();
                        }));
            }

            @Override
            public void onClose() {
                super.onClose();
                runnable.run();
            }
        };
        gui.setTitle(TextUtils.gui("presets_title"));

        gui.open();
    }

    public static void openItemFrameEditor(ServerPlayerEntity player, ItemFrameEntity entity) {
        ItemFrameEntityAccessor ifa = (ItemFrameEntityAccessor) entity;

        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X1, player, false);

        ItemFrameInventory inventory = new ItemFrameInventory(entity);

        GuiElement empty = new GuiElementBuilder(Items.GRAY_STAINED_GLASS_PANE).setName(Text.literal("")).build();

        gui.setTitle(TextUtils.gui("item_frame_title"));

        gui.setSlotRedirect(0, new Slot(inventory, 0, 0, 0));
        gui.setSlot(1, empty);

        gui.setSlot(2, new GuiElementBuilder(ifa.getFixed() ? Items.GREEN_STAINED_GLASS_PANE : Items.RED_STAINED_GLASS_PANE)
                .setName(TextUtils.gui("name.if-fixed", ifa.getFixed())
                        .setStyle(Style.EMPTY.withItalic(false)))
                .setCallback((index, type, action) -> {

                    ifa.setFixed(!ifa.getFixed());

                    ItemStack stack = new ItemStack(ifa.getFixed() ? Items.GREEN_STAINED_GLASS_PANE : Items.RED_STAINED_GLASS_PANE);

                    stack.setCustomName(TextUtils.gui("name.if-fixed", ifa.getFixed())
                            .setStyle(Style.EMPTY.withItalic(false)));

                    ((GuiElement) gui.getSlot(index)).setItemStack(stack);
                }));

        gui.setSlot(3, new GuiElementBuilder(entity.isInvisible() ? Items.GREEN_STAINED_GLASS_PANE : Items.RED_STAINED_GLASS_PANE)
                .setName(TextUtils.gui("name.if-invisible", entity.isInvisible())
                        .setStyle(Style.EMPTY.withItalic(false)))
                .setCallback((index, type, action) -> {

                    entity.setInvisible(!entity.isInvisible());
                    ItemStack stack = new ItemStack(entity.isInvisible() ? Items.GREEN_STAINED_GLASS_PANE : Items.RED_STAINED_GLASS_PANE);

                    stack.setCustomName(TextUtils.gui("name.if-invisible", entity.isInvisible())
                            .setStyle(Style.EMPTY.withItalic(false)));

                    ((GuiElement) gui.getSlot(index)).setItemStack(stack);
                }));

        gui.setSlot(4, new GuiElementBuilder(Items.ARROW)
                .setName(TextUtils.gui("name.if-rotate", entity.getRotation())
                        .setStyle(Style.EMPTY.withItalic(false)))
                .setCallback((index, type, action) -> {
                    if (type.isLeft || type.isRight) {
                        int rotation = entity.getRotation() + (type.isLeft ? -1 : 1);

                        if (rotation < 0) {
                            rotation = 8 + rotation;
                        }

                        entity.setRotation(rotation % 8);

                        ItemStack stack = new ItemStack(Items.ARROW);

                        stack.setCustomName(TextUtils.gui("name.if-rotate", rotation)
                                .setStyle(Style.EMPTY.withItalic(false)));

                        ((GuiElement) gui.getSlot(index)).setItemStack(stack);
                    }
                }));

        for (int x = 5; x < 8; x++) {
            gui.setSlot(x, empty);
        }

        gui.setSlot(8, new GuiElementBuilder(Items.BARRIER)
                .setName(TextUtils.gui("close").setStyle(Style.EMPTY.withItalic(false)))
                .setCallback(((index, type, action) -> {gui.close();}))
        );

        gui.open();
    }

    public static BaseGui.SwitchableUi getInventoryEditor() {
        return (context, slot) -> LegacyEditorGuis.openInventoryEditor(context.player, context.armorStand, () -> context.interfaceList.remove(0).open(context));
    }

    public static BaseGui.SwitchableUi getRenamingGui() {
        return (context, slot) -> LegacyEditorGuis.openRenaming(context.player, context.armorStand, () -> context.interfaceList.remove(0).open(context));
    }

    public static BaseGui.SwitchableUi getPresetsGui() {
        return (context, slot) -> LegacyEditorGuis.openPresetSelector(context.player, () -> context.interfaceList.remove(0).open(context));
    }
}

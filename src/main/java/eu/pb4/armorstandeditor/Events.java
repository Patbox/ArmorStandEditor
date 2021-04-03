package eu.pb4.armorstandeditor;

import eu.pb4.armorstandeditor.config.Config;
import eu.pb4.armorstandeditor.config.ConfigManager;
import eu.pb4.armorstandeditor.mixin.ArmorStandEntityAccessor;
import eu.pb4.armorstandeditor.other.SPEInterface;
import eu.pb4.sgui.AnvilInputGui;
import eu.pb4.sgui.ClickType;
import eu.pb4.sgui.GuiElement;
import eu.pb4.sgui.SimpleGui;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.world.World;

public class Events {
    public static void registerEvents() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            Config config = ConfigManager.getConfig();
            ItemStack itemStack = player.getMainHandStack();
            if (entity instanceof ArmorStandEntity
                    && player instanceof ServerPlayerEntity
                    && Permissions.check(player, "armorstandeditor.use", config.configData.toggleAllPermissionOnByDefault)
                    && itemStack.getItem() == config.armorStandTool
                    && (!config.configData.requireIsArmorStandEditorTag || itemStack.getOrCreateTag().getBoolean("isArmorStandEditor"))) {
                Events.modifyArmorStand((ServerPlayerEntity) player, (ArmorStandEntity) entity, 1);
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            Config config = ConfigManager.getConfig();
            ItemStack itemStack = player.getMainHandStack();
            if (entity instanceof ArmorStandEntity
                    && player instanceof ServerPlayerEntity
                    && Permissions.check(player, "armorstandeditor.use", config.configData.toggleAllPermissionOnByDefault)
                    && itemStack.getItem() == config.armorStandTool
                    && (!config.configData.requireIsArmorStandEditorTag || itemStack.getOrCreateTag().getBoolean("isArmorStandEditor"))) {
                Events.modifyArmorStand((ServerPlayerEntity) player, (ArmorStandEntity) entity, -1);
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });

        UseItemCallback.EVENT.register((PlayerEntity player, World world, Hand hand) -> {
            Config config = ConfigManager.getConfig();
            ItemStack itemStack = player.getMainHandStack();
            if (player instanceof ServerPlayerEntity
                    && itemStack.getItem() == config.armorStandTool
                    && Permissions.check(player, "armorstandeditor.use", config.configData.toggleAllPermissionOnByDefault)
                    && (!config.configData.requireIsArmorStandEditorTag || itemStack.getOrCreateTag().getBoolean("isArmorStandEditor"))) {
                Events.openGui((ServerPlayerEntity) player);
                return TypedActionResult.success(player.getMainHandStack());
            }

            return TypedActionResult.pass(player.getMainHandStack());
        });
    }

    public static void modifyArmorStand(ServerPlayerEntity player, ArmorStandEntity armorStand, int val) {
        SPEInterface spei = (SPEInterface) player;
        ArmorStandEntityAccessor asea = (ArmorStandEntityAccessor) armorStand;

        double power = spei.getArmorStandEditorPower();

        int dX = spei.getArmorStandEditorXYZ() == 0 ? 1 : 0;
        int dY = spei.getArmorStandEditorXYZ() == 1 ? 1 : 0;
        int dZ = spei.getArmorStandEditorXYZ() == 2 ? 1 : 0;

        double posX = armorStand.getX();
        double posY = armorStand.getY();
        double posZ = armorStand.getZ();

        float angleChange = (float) (val * power * 30);
        EulerAngle angle;

        switch (spei.getArmorStandEditorAction()) {
            case MOVE:
                armorStand.teleport(posX + dX * power * val, posY + dY * power * val, posZ + dZ * power * val);
                break;
            case ROTATE:
                armorStand.setYaw(armorStand.getYaw(1.0F) + angleChange);
                armorStand.updatePositionAndAngles(posX, posY, posZ, armorStand.getYaw(1.0F), 0);
                break;
            case TOGGLE_GRAVITY:
                armorStand.setNoGravity(!armorStand.hasNoGravity());
                break;
            case TOGGLE_BASE:
                asea.callSetHideBasePlate(!armorStand.shouldHideBasePlate());
                break;
            case TOGGLE_SIZE:
                asea.callSetSmall(!armorStand.isSmall());
                break;
            case TOGGLE_ARMS:
                asea.callSetShowArms(!armorStand.shouldShowArms());
                break;
            case TOGGLE_VISIBILITY:
                armorStand.setInvisible(!armorStand.isInvisible());
                break;
            case MODIFY_HEAD:
                angle = armorStand.getHeadRotation();
                armorStand.setHeadRotation(new EulerAngle(angle.getPitch() + dX * angleChange, angle.getYaw() + dY * angleChange, angle.getRoll() + dZ * angleChange));
                break;
            case MODIFY_BODY:
                angle = armorStand.getBodyRotation();
                armorStand.setBodyRotation(new EulerAngle(angle.getPitch() + dX * angleChange, angle.getYaw() + dY * angleChange, angle.getRoll() + dZ * angleChange));
                break;
            case MODIFY_LEFT_ARM:
                angle = asea.getLeftArmRotation();
                armorStand.setLeftArmRotation(new EulerAngle(angle.getPitch() + dX * angleChange, angle.getYaw() + dY * angleChange, angle.getRoll() + dZ * angleChange));
                break;
            case MODIFY_RIGHT_ARM:
                angle = asea.getRightArmRotation();
                armorStand.setRightArmRotation(new EulerAngle(angle.getPitch() + dX * angleChange, angle.getYaw() + dY * angleChange, angle.getRoll() + dZ * angleChange));
                break;
            case MODIFY_LEFT_LEG:
                angle = asea.getLeftLegRotation();
                armorStand.setLeftLegRotation(new EulerAngle(angle.getPitch() + dX * angleChange, angle.getYaw() + dY * angleChange, angle.getRoll() + dZ * angleChange));
                break;
            case MODIFY_RIGHT_LEG:
                angle = asea.getRightLegRotation();
                armorStand.setRightLegRotation(new EulerAngle(angle.getPitch() + dX * angleChange, angle.getYaw() + dY * angleChange, angle.getRoll() + dZ * angleChange));
                break;
            case RESET_POSE:
                armorStand.setHeadRotation(new EulerAngle(0,0,0));
                armorStand.setBodyRotation(new EulerAngle(0,0,0));
                armorStand.setLeftArmRotation(new EulerAngle(0,0,0));
                armorStand.setRightArmRotation(new EulerAngle(0,0,0));
                armorStand.setLeftLegRotation(new EulerAngle(0,0,0));
                armorStand.setRightLegRotation(new EulerAngle(0,0,0));
                break;
            case COPY:
                ArmorStandEntity temp = new ArmorStandEntity(armorStand.world, 0, 0, 0);
                temp.copyFrom(armorStand);
                spei.setArmorStandEditorRefData(temp);
                spei.setArmorStandEditorAction(EditorActions.PASTE);
                player.sendMessage(new TranslatableText("armorstandeditor.message.copied"), true);
                break;
            case PASTE:
                if (spei.getArmorStandEditorRefData() != null) {
                    ArmorStandEntity base = spei.getArmorStandEditorRefData();
                    ArmorStandEntityAccessor base2 = (ArmorStandEntityAccessor) base;
                    armorStand.setYaw(base.yaw);
                    armorStand.updatePositionAndAngles(posX, posY, posZ, base.yaw, 0);
                    armorStand.setNoGravity(base.hasNoGravity());
                    asea.callSetHideBasePlate(base.shouldHideBasePlate());
                    asea.callSetSmall(base.isSmall());
                    asea.callSetShowArms(base.shouldShowArms());
                    armorStand.setInvisible(base.isInvisible());
                    armorStand.setHeadRotation(base.getHeadRotation());
                    armorStand.setBodyRotation(base.getBodyRotation());
                    armorStand.setLeftArmRotation(base2.getLeftArmRotation());
                    armorStand.setRightArmRotation(base2.getRightArmRotation());
                    armorStand.setLeftLegRotation(base2.getLeftLegRotation());
                    armorStand.setRightLegRotation(base2.getRightLegRotation());
                    armorStand.setCustomNameVisible(base.isCustomNameVisible());
                    armorStand.setCustomName(base.getCustomName());
                    if (player.isCreative()) {
                        armorStand.equipStack(EquipmentSlot.HEAD, base.getEquippedStack(EquipmentSlot.HEAD));
                        armorStand.equipStack(EquipmentSlot.CHEST, base.getEquippedStack(EquipmentSlot.CHEST));
                        armorStand.equipStack(EquipmentSlot.LEGS, base.getEquippedStack(EquipmentSlot.LEGS));
                        armorStand.equipStack(EquipmentSlot.FEET, base.getEquippedStack(EquipmentSlot.FEET));
                        armorStand.equipStack(EquipmentSlot.MAINHAND, base.getEquippedStack(EquipmentSlot.MAINHAND));
                        armorStand.equipStack(EquipmentSlot.OFFHAND, base.getEquippedStack(EquipmentSlot.OFFHAND));
                    }
                    player.sendMessage(new TranslatableText("armorstandeditor.message.pasted"), true);
                } else {

                }
                break;
            case INVENTORY:
                openInventoryEditor(player, armorStand);
                break;
            case RENAME:
                openRenaming(player, armorStand);
                break;
        }
    }

    public static void openRenaming(ServerPlayerEntity player, ArmorStandEntity entity) {
        ItemStack stack = Items.MAGMA_CREAM.getDefaultStack();
        stack.setCustomName(new TranslatableText("armorstandeditor.gui.clearname").setStyle(Style.EMPTY.withItalic(false)));

        ItemStack stack2 = Items.SLIME_BALL.getDefaultStack();
        stack2.setCustomName(new TranslatableText("armorstandeditor.gui.setname").setStyle(Style.EMPTY.withItalic(false)));

        AnvilInputGui gui = new AnvilInputGui(player, false) {
            @Override
            public void onInput(String input) {
                super.onInput(input);
                stack2.setCustomName(new TranslatableText("armorstandeditor.gui.setname", this.getInput()).setStyle(Style.EMPTY.withItalic(false)));
                this.setSlot(2, stack2, (index, type, action) -> {
                    entity.setCustomName(new LiteralText(this.getInput()));
                    entity.setCustomNameVisible(true);
                    this.close(false);
                });
            }
        };

        gui.setTitle(new TranslatableText("armorstandeditor.gui.rename_title"));
        gui.setDefaultInputValue(entity.getCustomName() != null ? entity.getCustomName().getString() : "");


        gui.setSlot(1, stack, (index, type, action) -> {
            entity.setCustomName(new LiteralText(""));
            entity.setCustomNameVisible(false);
            gui.close(false);
        });

        gui.setSlot(2, stack2, (index, type, action) -> {
            entity.setCustomName(new LiteralText(gui.getInput()));
            entity.setCustomNameVisible(true);
            gui.close(false);
        });

        gui.open();
    }

    public static void openInventoryEditor(ServerPlayerEntity player, ArmorStandEntity entity) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X1, player, false) {
            @Override
            public boolean onClick(int index, ClickType type, SlotActionType action, GuiElement element) {
                if (!entity.isAlive() || entity.world != player.world || player.distanceTo(entity) > 10) {
                    this.close();
                    return false;
                }

                if (index >= 0 && index < 6) {
                    EquipmentSlot slot;
                    switch (index) {
                        case 0:
                            slot = EquipmentSlot.HEAD;
                            break;
                        case 1:
                            slot = EquipmentSlot.CHEST;
                            break;
                        case 2:
                            slot = EquipmentSlot.LEGS;
                            break;
                        case 3:
                            slot = EquipmentSlot.FEET;
                            break;
                        case 4:
                            slot = EquipmentSlot.MAINHAND;
                            break;
                        case 5:
                            slot = EquipmentSlot.OFFHAND;
                            break;
                        default:
                            slot = EquipmentSlot.HEAD;
                    }


                    ItemStack armorItem = entity.getEquippedStack(slot);
                    ItemStack playerItem = player.inventory.getCursorStack();

                    player.inventory.setCursorStack(armorItem);
                    entity.equipStack(slot, playerItem);
                    this.setSlot(index, playerItem.copy());

                    return false;
                }
                return false;
            }
        };
        gui.setTitle(new TranslatableText("armorstandeditor.gui.inventory_title"));
        gui.setSlot(0, entity.getEquippedStack(EquipmentSlot.HEAD).copy());
        gui.setSlot(1, entity.getEquippedStack(EquipmentSlot.CHEST).copy());
        gui.setSlot(2, entity.getEquippedStack(EquipmentSlot.LEGS).copy());
        gui.setSlot(3, entity.getEquippedStack(EquipmentSlot.FEET).copy());
        gui.setSlot(4, entity.getEquippedStack(EquipmentSlot.MAINHAND).copy());
        gui.setSlot(5, entity.getEquippedStack(EquipmentSlot.OFFHAND).copy());

        gui.open();
    }

    public static void openGui(ServerPlayerEntity player) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X5, player, false) {
            @Override
            public boolean onClick(int index, ClickType type, SlotActionType action, GuiElement element) {
                setIcons(player, this);
                return super.onClick(index, type, action, element);
            }
        };
        gui.setTitle(new TranslatableText("armorstandeditor.gui.editor_title"));
        setIcons(player, gui);
        gui.open();
    }

    private static void setIcons(ServerPlayerEntity player, SimpleGui gui) {
        createIcon(player, gui, 0, Items.RED_WOOL, "x", 0);
        createIcon(player, gui, 1, Items.GREEN_WOOL, "y", 1);
        createIcon(player, gui, 2, Items.BLUE_WOOL, "z", 2);

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
        createIcon(player, gui, 30, Items.LEVER, "reset_pose", EditorActions.RESET_POSE);

        createIcon(player, gui, 36, Items.LEATHER_BOOTS, "left_leg", EditorActions.MODIFY_LEFT_LEG);
        createIcon(player, gui, 37, Items.CHEST, "inventory", EditorActions.INVENTORY);
        createIcon(player, gui, 38, Items.LEATHER_BOOTS, "right_leg", EditorActions.MODIFY_RIGHT_LEG);
        createIcon(player, gui, 39, Items.NAME_TAG, "rename", EditorActions.RENAME);

        createIcon(player, gui, 43, Items.SLIME_BALL, "copy", EditorActions.COPY);
        createIcon(player, gui, 44, Items.MAGMA_CREAM, "paste", EditorActions.PASTE);
    }

    private static void createIcon(ServerPlayerEntity player, SimpleGui gui, int index, Item item, String text, int xyz) {
        ItemStack itemStack = item.getDefaultStack();
        itemStack.setCustomName(new TranslatableText("armorstandeditor.gui.name." + text).setStyle(Style.EMPTY.withItalic(false)));
        itemStack.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);
        itemStack.addHideFlag(ItemStack.TooltipSection.MODIFIERS);

        if (((SPEInterface) player).getArmorStandEditorXYZ() == xyz) {
            itemStack.addEnchantment(Enchantments.POWER, 1);
        }

        gui.setSlot(index, itemStack, (index2, type, actionType) -> {
            ((SPEInterface) player).setArmorStandEditorXYZ(xyz);
        });
    }

    private static void createIcon(ServerPlayerEntity player, SimpleGui gui, int index, Item item, String text, EditorActions action) {
        if (!Permissions.check(player, "armorstandeditor" + action.permission, ConfigManager.getConfig().configData.toggleAllPermissionOnByDefault)) {
            return;
        }

        ItemStack itemStack = item.getDefaultStack();
        itemStack.setCustomName(new TranslatableText("armorstandeditor.gui.name." + text).setStyle(Style.EMPTY.withItalic(false)));
        itemStack.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);
        itemStack.addHideFlag(ItemStack.TooltipSection.MODIFIERS);

        if (((SPEInterface) player).getArmorStandEditorAction() == action) {
            itemStack.addEnchantment(Enchantments.POWER, 1);
        }

        gui.setSlot(index, itemStack, (index2, type, actionType) -> {
            ((SPEInterface) player).setArmorStandEditorAction(action);
        });
    }

    private static void createIcon(ServerPlayerEntity player, SimpleGui gui, int index, Item item, String text, float power) {
        ItemStack itemStack = item.getDefaultStack();
        itemStack.setCustomName(new TranslatableText("armorstandeditor.gui.name." + text).setStyle(Style.EMPTY.withItalic(false)));
        itemStack.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);
        itemStack.addHideFlag(ItemStack.TooltipSection.MODIFIERS);

        if (((SPEInterface) player).getArmorStandEditorPower() == power) {
            itemStack.addEnchantment(Enchantments.POWER, 1);
        }

        gui.setSlot(index, itemStack, (index2, type, actionType) -> {
            ((SPEInterface) player).setArmorStandEditorPower(power);
        });
    }
}

package eu.pb4.armorstandeditor.legacy;

import eu.pb4.armorstandeditor.EditorActions;
import eu.pb4.armorstandeditor.config.Config;
import eu.pb4.armorstandeditor.config.ConfigManager;
import eu.pb4.armorstandeditor.mixin.ArmorStandEntityAccessor;
import eu.pb4.armorstandeditor.util.ArmorStandData;
import eu.pb4.common.protection.api.CommonProtection;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.world.World;
import xyz.nucleoid.disguiselib.api.EntityDisguise;
@Deprecated
public class LegacyEvents {
    public static void registerEvents() {
        if (FabricLoader.getInstance().isModLoaded("disguiselib")) {
            AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
                if (world.isClient) {
                    return ActionResult.PASS;
                }

                if (!LegacyPlayerExt.useLegacy(player)) {
                    return ActionResult.PASS;
                }

                Config config = ConfigManager.getConfig();
                ItemStack itemStack = player.getMainHandStack();

                if (entity instanceof EntityDisguise disguise
                        && player instanceof ServerPlayerEntity
                        && EditorActions.OPEN_EDITOR.canUse(player)
                        && itemStack.getItem() == config.armorStandTool
                        && (!config.configData.requireIsArmorStandEditorTag || itemStack.get(DataComponentTypes.CUSTOM_DATA).getNbt().getBoolean("isArmorStandEditor"))) {
                    if (disguise.isDisguised() && disguise.getDisguiseType() == EntityType.ARMOR_STAND && Permissions.check(player, "armor_stand_editor.modify_disguised", 2)) {
                        LegacyEvents.modifyArmorStand((ServerPlayerEntity) player, (ArmorStandEntity) disguise.getDisguiseEntity(), 1, entity);
                        return ActionResult.SUCCESS;
                    } else if (entity instanceof ArmorStandEntity) {
                        LegacyEvents.modifyArmorStand((ServerPlayerEntity) player, (ArmorStandEntity) entity, 1, null);
                        return ActionResult.SUCCESS;
                    }
                }

                return ActionResult.PASS;
            });

            UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
                if (world.isClient) {
                    return ActionResult.PASS;
                }

                if (!LegacyPlayerExt.useLegacy(player)) {
                    return ActionResult.PASS;
                }

                Config config = ConfigManager.getConfig();
                ItemStack itemStack = player.getMainHandStack();
                if (entity instanceof EntityDisguise disguise
                        && player instanceof ServerPlayerEntity
                        && EditorActions.OPEN_EDITOR.canUse(player)
                        && itemStack.getItem() == config.armorStandTool
                        && (!config.configData.requireIsArmorStandEditorTag || itemStack.get(DataComponentTypes.CUSTOM_DATA).getNbt().getBoolean("isArmorStandEditor"))) {
                    if (disguise.isDisguised() && disguise.getDisguiseType() == EntityType.ARMOR_STAND) {
                        LegacyEvents.modifyArmorStand((ServerPlayerEntity) player, (ArmorStandEntity) disguise.getDisguiseEntity(), -1, entity);
                        return ActionResult.SUCCESS;
                    } else if (entity instanceof ArmorStandEntity) {
                        LegacyEvents.modifyArmorStand((ServerPlayerEntity) player, (ArmorStandEntity) entity, -1, null);
                        return ActionResult.SUCCESS;
                    }
                }

                return ActionResult.PASS;
            });
        } else {
            AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
                if (world.isClient) {
                    return ActionResult.PASS;
                }

                if (!LegacyPlayerExt.useLegacy(player)) {
                    return ActionResult.PASS;
                }

                Config config = ConfigManager.getConfig();
                ItemStack itemStack = player.getMainHandStack();
                if (entity instanceof ArmorStandEntity
                        && player instanceof ServerPlayerEntity
                        && EditorActions.OPEN_EDITOR.canUse(player)
                        && itemStack.getItem() == config.armorStandTool
                        && (!config.configData.requireIsArmorStandEditorTag || itemStack.get(DataComponentTypes.CUSTOM_DATA).getNbt().getBoolean("isArmorStandEditor"))) {

                    LegacyEvents.modifyArmorStand((ServerPlayerEntity) player, (ArmorStandEntity) entity, 1, null);

                    return ActionResult.SUCCESS;
                }

                return ActionResult.PASS;
            });

            UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
                if (world.isClient) {
                    return ActionResult.PASS;
                }

                if (!LegacyPlayerExt.useLegacy(player)) {
                    return ActionResult.PASS;
                }

                Config config = ConfigManager.getConfig();
                ItemStack itemStack = player.getMainHandStack();
                if (entity instanceof ArmorStandEntity
                        && player instanceof ServerPlayerEntity
                        && EditorActions.OPEN_EDITOR.canUse(player)
                        && itemStack.getItem() == config.armorStandTool
                        && (!config.configData.requireIsArmorStandEditorTag || itemStack.get(DataComponentTypes.CUSTOM_DATA).getNbt().getBoolean("isArmorStandEditor"))) {
                    LegacyEvents.modifyArmorStand((ServerPlayerEntity) player, (ArmorStandEntity) entity, -1, null);
                    return ActionResult.SUCCESS;
                }

                return ActionResult.PASS;
            });
        }


        UseItemCallback.EVENT.register((PlayerEntity player, World world, Hand hand) -> {
            if (world.isClient) {
                return TypedActionResult.pass(player.getStackInHand(hand));
            }

            if (!LegacyPlayerExt.useLegacy(player)) {
                return TypedActionResult.pass(player.getStackInHand(hand));
            }

            Config config = ConfigManager.getConfig();
            ItemStack itemStack = player.getMainHandStack();
            if (player instanceof ServerPlayerEntity
                    && itemStack.getItem() == config.armorStandTool
                    && EditorActions.OPEN_EDITOR.canUse(player)
                    && (!config.configData.requireIsArmorStandEditorTag || itemStack.get(DataComponentTypes.CUSTOM_DATA).getNbt().getBoolean("isArmorStandEditor"))) {
                LegacyEditorGuis.openGui((ServerPlayerEntity) player);
                return TypedActionResult.success(player.getMainHandStack());
            }

            return TypedActionResult.pass(player.getMainHandStack());
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient) {
                return ActionResult.PASS;
            }

            Config config = ConfigManager.getConfig();
            ItemStack itemStack = player.getMainHandStack();

            if (entity instanceof ItemFrameEntity
                    && player instanceof ServerPlayerEntity
                    && EditorActions.OPEN_ITEM_FRAME_EDITOR.canUse(player)
                    && itemStack.getItem() == config.armorStandTool
                    && (!config.configData.requireIsArmorStandEditorTag || itemStack.get(DataComponentTypes.CUSTOM_DATA).getNbt().getBoolean("isArmorStandEditor"))) {

                LegacyEditorGuis.openItemFrameEditor((ServerPlayerEntity) player, (ItemFrameEntity) entity);
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });
    }

    public static void modifyArmorStand(ServerPlayerEntity player, ArmorStandEntity armorStand, int val, Entity realEntity) {
        LegacyPlayerExt spei = (LegacyPlayerExt) player;
        ArmorStandEntityAccessor asea = (ArmorStandEntityAccessor) armorStand;

        double power = spei.aselegacy$getArmorStandEditorPower();

        int dX = spei.aselegacy$getArmorStandEditorXYZ() == 0 ? 1 : 0;
        int dY = spei.aselegacy$getArmorStandEditorXYZ() == 1 ? 1 : 0;
        int dZ = spei.aselegacy$getArmorStandEditorXYZ() == 2 ? 1 : 0;

        double posX = armorStand.getX();
        double posY = armorStand.getY();
        double posZ = armorStand.getZ();

        float angleChange = (float) (val * power * 30);
        EulerAngle angle;

        switch (spei.aselegacy$getArmorStandEditorAction()) {
            case MOVE:
                armorStand.teleport(posX + dX * power * val, posY + dY * power * val, posZ + dZ * power * val, false);
                if (!CommonProtection.canInteractEntity(player.getWorld(), armorStand, player.getGameProfile(), player)) {
                    armorStand.teleport(posX, posY, posZ, false);
                }
                break;
            case ROTATE:
                armorStand.setYaw(armorStand.getYaw() + angleChange);
                armorStand.updatePositionAndAngles(posX, posY, posZ, armorStand.getYaw(), 0);
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
                angle = armorStand.getLeftArmRotation();
                armorStand.setLeftArmRotation(new EulerAngle(angle.getPitch() + dX * angleChange, angle.getYaw() + dY * angleChange, angle.getRoll() + dZ * angleChange));
                break;
            case MODIFY_RIGHT_ARM:
                angle = armorStand.getRightArmRotation();
                armorStand.setRightArmRotation(new EulerAngle(angle.getPitch() + dX * angleChange, angle.getYaw() + dY * angleChange, angle.getRoll() + dZ * angleChange));
                break;
            case MODIFY_LEFT_LEG:
                angle = armorStand.getLeftLegRotation();
                armorStand.setLeftLegRotation(new EulerAngle(angle.getPitch() + dX * angleChange, angle.getYaw() + dY * angleChange, angle.getRoll() + dZ * angleChange));
                break;
            case MODIFY_RIGHT_LEG:
                angle = armorStand.getRightLegRotation();
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
            case FLIP_POSE:
                ArmorStandData data = new ArmorStandData(armorStand);
                armorStand.setHeadRotation(new EulerAngle(data.headRotation.getPitch(),360 - data.headRotation.getYaw(),360 - data.headRotation.getRoll()));
                armorStand.setBodyRotation(new EulerAngle(data.bodyRotation.getPitch(),360 - data.bodyRotation.getYaw(),360 - data.bodyRotation.getRoll()));
                armorStand.setRightArmRotation(new EulerAngle(data.leftArmRotation.getPitch(),360 - data.leftArmRotation.getYaw(),360 - data.leftArmRotation.getRoll()));
                armorStand.setLeftArmRotation(new EulerAngle(data.rightArmRotation.getPitch(),360 - data.rightArmRotation.getYaw(),360 - data.rightArmRotation.getRoll()));
                armorStand.setRightLegRotation(new EulerAngle(data.leftLegRotation.getPitch(),360 - data.leftLegRotation.getYaw(),360 - data.leftLegRotation.getRoll()));
                armorStand.setLeftLegRotation(new EulerAngle(data.rightLegRotation.getPitch(),360 - data.rightLegRotation.getYaw(),360 - data.rightLegRotation.getRoll()));

                break;
            case COPY:
                spei.aselegacy$setArmorStandEditorData(new ArmorStandData(armorStand));
                spei.aselegacy$setArmorStandEditorAction(EditorActions.PASTE);
                player.sendMessage(Text.translatable("text.armor_stand_editor.message.copied.legacy"), true);
                break;
            case PASTE:
                if (spei.aselegacy$getArmorStandEditorData() != null) {
                    ArmorStandData base = spei.aselegacy$getArmorStandEditorData();
                    base.apply(armorStand, player.isCreative());

                    if (realEntity != null) {
                        realEntity.setCustomNameVisible(base.customNameVisible);
                        if (base.customName != null) {
                            realEntity.setCustomName(base.customName);
                        }

                        if (player.isCreative() && realEntity instanceof LivingEntity livingEntity) {
                            livingEntity.equipStack(EquipmentSlot.HEAD, base.headItem);
                            livingEntity.equipStack(EquipmentSlot.CHEST, base.chestItem);
                            livingEntity.equipStack(EquipmentSlot.LEGS, base.legsItem);
                            livingEntity.equipStack(EquipmentSlot.FEET, base.feetItem);
                            livingEntity.equipStack(EquipmentSlot.MAINHAND, base.mainHandItem);
                            livingEntity.equipStack(EquipmentSlot.OFFHAND, base.offhandItem);
                        }
                    }

                    player.sendMessage(Text.translatable("text.armor_stand_editor.message.pasted"), true);
                }
                break;
            case INVENTORY:
                if (realEntity instanceof LivingEntity) {
                    LegacyEditorGuis.openInventoryEditor(player, (LivingEntity) realEntity, () -> {});
                } else {
                    LegacyEditorGuis.openInventoryEditor(player, armorStand, () -> {});
                }
                break;
            case RENAME:
                Entity nameTarget = realEntity != null ? realEntity : armorStand;

                LegacyEditorGuis.openRenaming(player, nameTarget, () -> {});
                break;
        }

        if (realEntity != null) {
            ((EntityDisguise) realEntity).disguiseAs(armorStand);
        }
    }
}

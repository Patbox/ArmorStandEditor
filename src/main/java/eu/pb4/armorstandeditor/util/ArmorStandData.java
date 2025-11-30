package eu.pb4.armorstandeditor.util;

import eu.pb4.armorstandeditor.mixin.ArmorStandEntityAccessor;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;

public class ArmorStandData {
    public float yaw = -1;
    public boolean noGravity = false;
    public boolean hidePlate = false;
    public boolean small = false;
    public boolean showArms = false;
    public boolean invisible = false;
    public Rotations headRotation = new Rotations(0,0,0);
    public Rotations bodyRotation = new Rotations(0,0,0);
    public Rotations leftArmRotation = new Rotations(0,0,0);
    public Rotations rightArmRotation = new Rotations(0,0,0);
    public Rotations leftLegRotation = new Rotations(0,0,0);
    public Rotations rightLegRotation = new Rotations(0,0,0);
    public boolean customNameVisible = false;
    public Component customName = null;

    public boolean hasInventory = false;
    public ItemStack headItem = ItemStack.EMPTY;
    public ItemStack feetItem = ItemStack.EMPTY;
    public ItemStack legsItem = ItemStack.EMPTY;
    public ItemStack chestItem = ItemStack.EMPTY;
    public ItemStack mainHandItem = ItemStack.EMPTY;
    public ItemStack offhandItem = ItemStack.EMPTY;
    public int disabledSlots = 0;
    public float scale = 1;

    public ArmorStandData() {}

    public ArmorStandData(ArmorStand armorStand) {
        this.copyFrom(armorStand);
    }

    public void copyFrom(ArmorStand armorStand) {
        ArmorStandEntityAccessor asea = (ArmorStandEntityAccessor) armorStand;
        this.yaw = armorStand.getYRot();
        this.noGravity = armorStand.isNoGravity();
        this.hidePlate = !armorStand.showBasePlate();
        this.small = armorStand.isSmall();
        this.showArms = armorStand.showArms();
        this.invisible = armorStand.isInvisible();
        this.headRotation = armorStand.getHeadPose();
        this.bodyRotation = armorStand.getBodyPose();
        this.leftArmRotation = armorStand.getLeftArmPose();
        this.rightArmRotation = armorStand.getRightArmPose();
        this.leftLegRotation = armorStand.getLeftLegPose();
        this.rightLegRotation = armorStand.getRightLegPose();

        this.scale = armorStand.getScale();

        this.customNameVisible = armorStand.isCustomNameVisible() && armorStand.hasCustomName();
        if (armorStand.getCustomName() != null) {
            this.customName = armorStand.getCustomName().copy();
        }

        this.hasInventory = true;
        this.headItem = armorStand.getItemBySlot(EquipmentSlot.HEAD).copy();
        this.chestItem = armorStand.getItemBySlot(EquipmentSlot.CHEST).copy();
        this.legsItem = armorStand.getItemBySlot(EquipmentSlot.LEGS).copy();
        this.feetItem = armorStand.getItemBySlot(EquipmentSlot.FEET).copy();
        this.mainHandItem = armorStand.getItemBySlot(EquipmentSlot.MAINHAND).copy();
        this.offhandItem = armorStand.getItemBySlot(EquipmentSlot.OFFHAND).copy();


        this.disabledSlots = asea.getDisabledSlots();
    }

    public void apply(ArmorStand armorStand, boolean modifyInventory) {
        ArmorStandEntityAccessor asea = (ArmorStandEntityAccessor) armorStand;
        if (this.yaw != -1) {
            armorStand.setYRot(this.yaw);
            double posX = armorStand.getX();
            double posY = armorStand.getY();
            double posZ = armorStand.getZ();
            armorStand.absSnapTo(posX, posY, posZ, this.yaw, 0);
        }
        armorStand.setNoGravity(this.noGravity);
        armorStand.noPhysics = this.noGravity;
        armorStand.setNoBasePlate(this.hidePlate);
        asea.callSetSmall(this.small);
        asea.callSetShowArms(this.showArms);
        armorStand.setInvisible(this.invisible);
        armorStand.setHeadPose(this.headRotation);
        armorStand.setBodyPose(this.bodyRotation);
        armorStand.setLeftArmPose(this.leftArmRotation);
        armorStand.setRightArmPose(this.rightArmRotation);
        armorStand.setLeftLegPose(this.leftLegRotation);
        armorStand.setRightLegPose(this.rightLegRotation);

        armorStand.setCustomNameVisible(this.customNameVisible);
        armorStand.setCustomName(this.customName);
        asea.setDisabledSlots(this.disabledSlots);

        armorStand.getAttribute(Attributes.SCALE).setBaseValue(scale);

        if (modifyInventory && this.hasInventory) {
            armorStand.setItemSlot(EquipmentSlot.HEAD, this.headItem);
            armorStand.setItemSlot(EquipmentSlot.CHEST, this.chestItem);
            armorStand.setItemSlot(EquipmentSlot.LEGS, this.legsItem);
            armorStand.setItemSlot(EquipmentSlot.FEET, this.feetItem);
            armorStand.setItemSlot(EquipmentSlot.MAINHAND, this.mainHandItem);
            armorStand.setItemSlot(EquipmentSlot.OFFHAND, this.offhandItem);
        }
    }
}

package eu.pb4.armorstandeditor.gui;

import eu.pb4.armorstandeditor.util.ArmorStandData;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.EulerAngle;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PoseListGui extends BaseWorldGui {
    private static final List<Entry> ENTRIES = List.of(
            Entry.part("head", Items.LEATHER_HELMET, ArmorStandEntity::setHeadRotation, ArmorStandEntity::getHeadRotation),
            Entry.part("left_arm", Items.STICK, ArmorStandEntity::setLeftArmRotation, ArmorStandEntity::getLeftArmRotation),
            Entry.part("body", Items.IRON_CHESTPLATE, ArmorStandEntity::setBodyRotation, ArmorStandEntity::getBodyRotation),
            Entry.part("right_arm", Items.BLAZE_ROD, ArmorStandEntity::setRightArmRotation, ArmorStandEntity::getRightArmRotation),
            Entry.part("left_leg", Items.LEATHER_BOOTS, ArmorStandEntity::setLeftLegRotation, ArmorStandEntity::getLeftLegRotation),
            Entry.part("right_leg", Items.GOLDEN_BOOTS, ArmorStandEntity::setRightLegRotation, ArmorStandEntity::getRightLegRotation)
    );



    public PoseListGui(EditingContext context, int slot) {
        super(context, slot);
        this.rebuildUi();
        this.open();
    }

    @Override
    protected void buildUi() {
        for (var entry : ENTRIES) {
            this.addSlot(switchElement(entry.icon, entry.name, entry.opener));
        }

        this.addSlot(baseElement(Items.GLISTERING_MELON_SLICE, "action.pose.flip", false)
                .setCallback((x, y, z) -> {
                    this.playClickSound();

                    var armorStand = this.context.armorStand;
                    ArmorStandData data = new ArmorStandData(armorStand);
                    armorStand.setHeadRotation(new EulerAngle(data.headRotation.pitch(),360 - data.headRotation.yaw(),360 - data.headRotation.roll()));
                    armorStand.setBodyRotation(new EulerAngle(data.bodyRotation.pitch(),360 - data.bodyRotation.yaw(),360 - data.bodyRotation.roll()));
                    armorStand.setRightArmRotation(new EulerAngle(data.leftArmRotation.pitch(),360 - data.leftArmRotation.yaw(),360 - data.leftArmRotation.roll()));
                    armorStand.setLeftArmRotation(new EulerAngle(data.rightArmRotation.pitch(),360 - data.rightArmRotation.yaw(),360 - data.rightArmRotation.roll()));
                    armorStand.setRightLegRotation(new EulerAngle(data.leftLegRotation.pitch(),360 - data.leftLegRotation.yaw(),360 - data.leftLegRotation.roll()));
                    armorStand.setLeftLegRotation(new EulerAngle(data.rightLegRotation.pitch(),360 - data.rightLegRotation.yaw(),360 - data.rightLegRotation.roll()));


                    var itemStack = armorStand.getEquippedStack(EquipmentSlot.MAINHAND);
                    armorStand.equipStack(EquipmentSlot.MAINHAND, armorStand.getEquippedStack(EquipmentSlot.OFFHAND));
                    armorStand.equipStack(EquipmentSlot.OFFHAND, itemStack);
                }));

        this.addSlot(baseElement(Items.LEVER, "action.pose.reset", false)
                .setCallback((x, y, z) -> {
                    this.playClickSound();

                    var armorStand = this.context.armorStand;
                    armorStand.setHeadRotation(new EulerAngle(0,0,0));
                    armorStand.setBodyRotation(new EulerAngle(0,0,0));
                    armorStand.setLeftArmRotation(new EulerAngle(0,0,0));
                    armorStand.setRightArmRotation(new EulerAngle(0,0,0));
                    armorStand.setLeftLegRotation(new EulerAngle(0,0,0));
                    armorStand.setRightLegRotation(new EulerAngle(0,0,0));
                }));
    }

    @Override
    protected EditingContext.SwitchEntry asSwitchableUi() {
        return new EditingContext.SwitchEntry(PoseListGui::new, this.getSelectedSlot());
    }


    private record Entry(String name, Item icon, EditingContext.SwitchableUi opener) {
        protected static Entry part(String name, Item icon, BiConsumer<ArmorStandEntity, EulerAngle> setter, Function<ArmorStandEntity, EulerAngle> getter) {
            return new Entry("part." + name, icon, ModifyPoseGui.create(setter, getter));
        }
    }
}

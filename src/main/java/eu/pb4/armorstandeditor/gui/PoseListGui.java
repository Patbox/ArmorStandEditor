package eu.pb4.armorstandeditor.gui;

import eu.pb4.armorstandeditor.util.ArmorStandData;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.core.Rotations;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class PoseListGui extends BaseWorldGui {
    private static final List<Entry> ENTRIES = List.of(
            Entry.part("head", Items.LEATHER_HELMET, ArmorStand::setHeadPose, ArmorStand::getHeadPose),
            Entry.part("left_arm", Items.STICK, ArmorStand::setLeftArmPose, ArmorStand::getLeftArmPose),
            Entry.part("body", Items.IRON_CHESTPLATE, ArmorStand::setBodyPose, ArmorStand::getBodyPose),
            Entry.part("right_arm", Items.BLAZE_ROD, ArmorStand::setRightArmPose, ArmorStand::getRightArmPose),
            Entry.part("left_leg", Items.LEATHER_BOOTS, ArmorStand::setLeftLegPose, ArmorStand::getLeftLegPose),
            Entry.part("right_leg", Items.GOLDEN_BOOTS, ArmorStand::setRightLegPose, ArmorStand::getRightLegPose)
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
                    armorStand.setHeadPose(new Rotations(data.headRotation.x(),360 - data.headRotation.y(),360 - data.headRotation.z()));
                    armorStand.setBodyPose(new Rotations(data.bodyRotation.x(),360 - data.bodyRotation.y(),360 - data.bodyRotation.z()));
                    armorStand.setRightArmPose(new Rotations(data.leftArmRotation.x(),360 - data.leftArmRotation.y(),360 - data.leftArmRotation.z()));
                    armorStand.setLeftArmPose(new Rotations(data.rightArmRotation.x(),360 - data.rightArmRotation.y(),360 - data.rightArmRotation.z()));
                    armorStand.setRightLegPose(new Rotations(data.leftLegRotation.x(),360 - data.leftLegRotation.y(),360 - data.leftLegRotation.z()));
                    armorStand.setLeftLegPose(new Rotations(data.rightLegRotation.x(),360 - data.rightLegRotation.y(),360 - data.rightLegRotation.z()));


                    var itemStack = armorStand.getItemBySlot(EquipmentSlot.MAINHAND);
                    armorStand.setItemSlot(EquipmentSlot.MAINHAND, armorStand.getItemBySlot(EquipmentSlot.OFFHAND));
                    armorStand.setItemSlot(EquipmentSlot.OFFHAND, itemStack);
                }));

        this.addSlot(baseElement(Items.LEVER, "action.pose.reset", false)
                .setCallback((x, y, z) -> {
                    this.playClickSound();

                    var armorStand = this.context.armorStand;
                    armorStand.setHeadPose(new Rotations(0,0,0));
                    armorStand.setBodyPose(new Rotations(0,0,0));
                    armorStand.setLeftArmPose(new Rotations(0,0,0));
                    armorStand.setRightArmPose(new Rotations(0,0,0));
                    armorStand.setLeftLegPose(new Rotations(0,0,0));
                    armorStand.setRightLegPose(new Rotations(0,0,0));
                }));
    }

    @Override
    protected EditingContext.SwitchEntry asSwitchableUi() {
        return new EditingContext.SwitchEntry(PoseListGui::new, this.getSelectedSlot());
    }


    private record Entry(String name, Item icon, EditingContext.SwitchableUi opener) {
        protected static Entry part(String name, Item icon, BiConsumer<ArmorStand, Rotations> setter, Function<ArmorStand, Rotations> getter) {
            return new Entry("part." + name, icon, ModifyPoseGui.create(setter, getter));
        }
    }
}

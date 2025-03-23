package eu.pb4.armorstandeditor.config;

import eu.pb4.armorstandeditor.util.ArmorStandData;
import net.minecraft.util.math.EulerAngle;

public class ArmorStandPreset {
    public String id = "preset-" + ((int) (Math.random() * 100000000));
    public String name = "Unnamed";
    public String author = "Unknown";

    public boolean noGravity = false;
    public boolean hidePlate = false;
    public boolean small = false;
    public boolean showArms = false;
    public boolean invisible = false;
    public float scale = 1;
    public float[] headRotation = new float[]{0, 0, 0};
    public float[] bodyRotation = new float[]{0, 0, 0};
    public float[] leftArmRotation = new float[]{0, 0, 0};
    public float[] rightArmRotation = new float[]{0, 0, 0};
    public float[] leftLegRotation = new float[]{0, 0, 0};
    public float[] rightLegRotation = new float[]{0, 0, 0};

    public ArmorStandPreset() {}

    public ArmorStandPreset(String id, String name, String author) {
        this.id = id;
        this.name = name;
        this.author = author;
    }

    public ArmorStandData asData() {
        ArmorStandData data = new ArmorStandData();

        data.hasInventory = false;
        data.noGravity = this.noGravity;
        data.hidePlate = this.hidePlate;
        data.small = this.small;
        data.showArms = this.showArms;
        data.invisible = this.invisible;
        data.scale = this.scale;
        data.headRotation = new EulerAngle(this.headRotation[0], this.headRotation[1], this.headRotation[2]);
        data.bodyRotation = new EulerAngle(this.bodyRotation[0], this.bodyRotation[1], this.bodyRotation[2]);
        data.leftArmRotation = new EulerAngle(this.leftArmRotation[0], this.leftArmRotation[1], this.leftArmRotation[2]);
        data.rightArmRotation = new EulerAngle(this.rightArmRotation[0], this.rightArmRotation[1], this.rightArmRotation[2]);
        data.leftLegRotation = new EulerAngle(this.leftLegRotation[0], this.leftLegRotation[1], this.leftLegRotation[2]);
        data.rightLegRotation = new EulerAngle(this.rightLegRotation[0], this.rightLegRotation[1], this.rightLegRotation[2]);

        return data;
    }

    public void fromData(ArmorStandData data) {
        this.noGravity = data.noGravity;
        this.hidePlate = data.hidePlate;
        this.small = data.small;
        this.showArms = data.showArms;
        this.invisible = data.invisible;
        this.scale = data.scale;
        this.headRotation = new float[]{data.headRotation.pitch(), data.headRotation.yaw(), data.headRotation.roll()};
        this.bodyRotation = new float[]{data.bodyRotation.pitch(), data.bodyRotation.yaw(), data.bodyRotation.roll()};
        this.leftArmRotation = new float[]{data.leftArmRotation.pitch(), data.leftArmRotation.yaw(), data.leftArmRotation.roll()};
        this.rightArmRotation = new float[]{data.rightArmRotation.pitch(), data.rightArmRotation.yaw(), data.rightArmRotation.roll()};
        this.leftLegRotation = new float[]{data.leftLegRotation.pitch(), data.leftLegRotation.yaw(), data.leftLegRotation.roll()};
        this.rightLegRotation = new float[]{data.rightLegRotation.pitch(), data.rightLegRotation.yaw(), data.rightLegRotation.roll()};
    }



}

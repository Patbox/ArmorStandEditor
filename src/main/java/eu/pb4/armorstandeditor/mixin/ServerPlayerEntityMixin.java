package eu.pb4.armorstandeditor.mixin;


import eu.pb4.armorstandeditor.EditorActions;
import eu.pb4.armorstandeditor.other.SPEInterface;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements SPEInterface {
    private EditorActions armorStandEditorAction = EditorActions.MOVE;
    private float armorStandEditorPower = 1;
    private int armorStandEditorXYZ = 0;
    private ArmorStandEntity armorStandEditorRefDat = null;


    public EditorActions getArmorStandEditorAction() {
        return this.armorStandEditorAction;
    }

    public void setArmorStandEditorAction(EditorActions action) {
        this.armorStandEditorAction = action;
    }

    public float getArmorStandEditorPower() {
        return this.armorStandEditorPower;
    }

    public void setArmorStandEditorPower(float power) {
        this.armorStandEditorPower = power;
    }

    public int getArmorStandEditorXYZ() {
        return this.armorStandEditorXYZ;
    }

    public void setArmorStandEditorXYZ(int xyz) {
        this.armorStandEditorXYZ = xyz;
    }

    public ArmorStandEntity getArmorStandEditorRefData() {
        return this.armorStandEditorRefDat;
    }

    public void setArmorStandEditorRefData(ArmorStandEntity data) {
        this.armorStandEditorRefDat = data;
    }
}

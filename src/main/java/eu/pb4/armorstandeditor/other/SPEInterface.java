package eu.pb4.armorstandeditor.other;


import eu.pb4.armorstandeditor.EditorActions;
import net.minecraft.entity.decoration.ArmorStandEntity;

public interface SPEInterface {
    EditorActions getArmorStandEditorAction();
    void setArmorStandEditorAction(EditorActions action);
    float getArmorStandEditorPower();
    void setArmorStandEditorPower(float power);
    int getArmorStandEditorXYZ();
    void setArmorStandEditorXYZ(int xyz);
    ArmorStandEntity getArmorStandEditorRefData();
    void setArmorStandEditorRefData(ArmorStandEntity entity);
}

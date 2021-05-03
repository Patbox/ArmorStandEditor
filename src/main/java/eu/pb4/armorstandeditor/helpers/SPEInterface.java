package eu.pb4.armorstandeditor.helpers;

import eu.pb4.armorstandeditor.EditorActions;

public interface SPEInterface {
    EditorActions getArmorStandEditorAction();
    void setArmorStandEditorAction(EditorActions action);
    float getArmorStandEditorPower();
    void setArmorStandEditorPower(float power);
    int getArmorStandEditorXYZ();
    void setArmorStandEditorXYZ(int xyz);
    ArmorStandData getArmorStandEditorData();
    void setArmorStandEditorData(ArmorStandData data);
}

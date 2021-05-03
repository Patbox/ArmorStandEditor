package eu.pb4.armorstandeditor.config;


import java.util.ArrayList;

public class ConfigData {
    public int CONFIG_VERSION_DONT_TOUCH_THIS = 1;

    public String armorStandTool = "minecraft:flint";
    public boolean requireIsArmorStandEditorTag = false;
    public boolean toggleAllPermissionOnByDefault = true;
    public boolean holdingToolSpawnsParticles = true;
    public ArrayList<String> blackListedBuildInPresets = new ArrayList<>();
}

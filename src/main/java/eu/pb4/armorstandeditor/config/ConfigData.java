package eu.pb4.armorstandeditor.config;


import eu.pb4.armorstandeditor.EditorActions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigData {
    public int CONFIG_VERSION_DONT_TOUCH_THIS = 2;

    public String armorStandTool = "minecraft:flint";
    public boolean requireIsArmorStandEditorTag = false;
    public boolean holdingToolSpawnsParticles = true;
    public boolean useLegacyUiByDefault = false;
    public ArrayList<String> blackListedBuildInPresets = new ArrayList<>();
    public Set<String> allowedByDefault = Arrays.stream(EditorActions.values()).map(x -> x.permission).collect(Collectors.toSet());
}

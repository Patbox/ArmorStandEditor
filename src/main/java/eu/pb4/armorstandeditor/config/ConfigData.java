package eu.pb4.armorstandeditor.config;


import com.google.gson.annotations.SerializedName;
import eu.pb4.armorstandeditor.EditorActions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigData {
    public int CONFIG_VERSION_DONT_TOUCH_THIS = 3;

    public String armorStandTool = "minecraft:flint";
    public boolean requireIsArmorStandEditorTag = false;
    public boolean holdingToolSpawnsParticles = true;
    public boolean useLegacyUiByDefault = false;

    @SerializedName("minimal_scale_value")
    public float minimumScaleValue = 0.0625f;
    @SerializedName("maximal_scale_value")
    public float maximalScaleValue = 16;
    @SerializedName("player_preset_limit")
    public int presetLimit = 16;
    public ArrayList<String> blackListedBuildInPresets = new ArrayList<>();
    public Set<String> allowedByDefault = Arrays.stream(EditorActions.values()).map(x -> x.permission).collect(Collectors.toSet());

    public void update() {
        if (CONFIG_VERSION_DONT_TOUCH_THIS <= 2) {
            if (this.allowedByDefault.contains(EditorActions.TOGGLE_SIZE.permission)) {
                this.allowedByDefault.add(EditorActions.SCALE.permission);
            }
        }

        CONFIG_VERSION_DONT_TOUCH_THIS = 3;
    }
}

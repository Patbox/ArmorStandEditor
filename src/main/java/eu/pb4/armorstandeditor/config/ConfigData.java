package eu.pb4.armorstandeditor.config;


import com.google.gson.annotations.SerializedName;
import eu.pb4.armorstandeditor.EditorActions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigData {
    public int CONFIG_VERSION_DONT_TOUCH_THIS = 5;

    @SerializedName(value = "armor_stand_tool", alternate = "armorStandTool")
    public String armorStandTool = "minecraft:flint";
    @SerializedName(value = "require_nbt_tag_for_editor", alternate = "requireIsArmorStandEditorTag")
    public boolean requireNbtTagForEditor = false;
    @SerializedName(value = "render_target_particles", alternate = "holdingToolSpawnsParticles")
    public boolean renderTargetParticles = true;
    @SerializedName("minimal_scale_value")
    public float minimumScaleValue = 0.0625f;
    @SerializedName("maximal_scale_value")
    public float maximalScaleValue = 16;
    @SerializedName("player_preset_limit")
    public int presetLimit = 16;
    @SerializedName(value = "blocked_builtin_presets", alternate = "blackListedBuildInPresets")
    public ArrayList<String> blockedBuiltinPresets = new ArrayList<>();
    @SerializedName(value = "allowed_actions", alternate = "allowedByDefault")
    public Set<String> defaultAllowedActions = Arrays.stream(EditorActions.values()).map(x -> x.permission).collect(Collectors.toSet());

    public void update() {
        if (CONFIG_VERSION_DONT_TOUCH_THIS <= 3) {
            if (this.defaultAllowedActions.contains(EditorActions.TOGGLE_SIZE.permission)) {
                this.defaultAllowedActions.add(EditorActions.SCALE.permission);
            }
            if (this.defaultAllowedActions.contains(EditorActions.TOGGLE_VISIBILITY.permission)) {
                this.defaultAllowedActions.add(EditorActions.TOGGLE_VISUAL_FIRE.permission);
            }
        }

        if (CONFIG_VERSION_DONT_TOUCH_THIS <= 4) {
            if (this.defaultAllowedActions.contains(EditorActions.TOGGLE_VISIBILITY.permission)) {
                this.defaultAllowedActions.add(EditorActions.TOGGLE_INVULNERABILITY.permission);
            }
        }


        CONFIG_VERSION_DONT_TOUCH_THIS = 5;
    }
}

package eu.pb4.armorstandeditor;

import eu.pb4.armorstandeditor.config.ConfigManager;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

public enum EditorActions {
    OPEN_EDITOR("open_editor"),
    OPEN_ITEM_FRAME_EDITOR("open_item_frame_editor"),
    MOVE("move"),
    ROTATE("move"),

    TOGGLE_PROPERTIES("change_properties"),
    TOGGLE_SIZE("toggle_size"),
    TOGGLE_ARMS("toggle_arms"),
    TOGGLE_VISIBILITY("toggle_visibility"),
    TOGGLE_GRAVITY("toggle_no_gravity"),
    TOGGLE_BASE("toggle_base"),
    TOGGLE_VISUAL_FIRE("toggle_visual_fire"),

    SCALE("scale"),

    MODIFY_POSE("modify_pose"),
    MODIFY_LEFT_ARM("modify_pose"),
    MODIFY_RIGHT_ARM("modify_pose"),
    MODIFY_LEFT_LEG("modify_pose"),
    MODIFY_RIGHT_LEG("modify_pose"),
    MODIFY_HEAD("modify_pose"),
    MODIFY_BODY("modify_pose"),

    RESET_POSE("modify_pose"),
    FLIP_POSE("modify_pose"),

    COPY("copy"),
    PASTE("paste"),
    INVENTORY("inventory"),
    RENAME("rename");

    public final String permission;

    EditorActions(String permission) {
        this.permission = permission;
    }

    public boolean canUse(PlayerEntity player) {
        return Permissions.check(player, "armor_stand_editor.action." + this.permission,
                ConfigManager.getConfig().configData.allowedByDefault.contains(this.permission) ? 0 : 2
        );
    }
}

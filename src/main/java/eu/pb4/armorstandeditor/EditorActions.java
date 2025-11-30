package eu.pb4.armorstandeditor;

import eu.pb4.armorstandeditor.config.ConfigManager;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.entity.player.Player;

public enum EditorActions {
    OPEN_EDITOR("open_editor"),
    OPEN_ITEM_FRAME_EDITOR("open_item_frame_editor"),
    MOVE("move"),
    TOGGLE_PROPERTIES("change_properties"),
    TOGGLE_SIZE("toggle_size"),
    TOGGLE_ARMS("toggle_arms"),
    TOGGLE_VISIBILITY("toggle_visibility"),
    TOGGLE_GRAVITY("toggle_no_gravity"),
    TOGGLE_BASE("toggle_base"),
    TOGGLE_VISUAL_FIRE("toggle_visual_fire"),
    TOGGLE_INVULNERABILITY("toggle_invulnerability"),

    SCALE("scale"),

    MODIFY_POSE("modify_pose"),

    COPY("copy"),
    INVENTORY("inventory"),
    RENAME("rename");

    public final String permission;

    EditorActions(String permission) {
        this.permission = permission;
    }

    public boolean canUse(Player player) {
        var defaultLevel = ConfigManager.getConfig().configData.defaultAllowedActions.contains(this.permission) ? PermissionLevel.ALL : PermissionLevel.GAMEMASTERS;
        return Permissions.check(player, "armor_stand_editor.action." + this.permission, defaultLevel);
    }
}

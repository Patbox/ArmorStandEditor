package eu.pb4.armorstandeditor;

public enum EditorActions {
    MOVE("edit.move"),
    ROTATE("edit.rotate"),

    TOGGLE_SIZE("edit.size"),
    TOGGLE_ARMS("edit.arms"),
    TOGGLE_VISIBILITY("edit.visibility"),
    TOGGLE_GRAVITY("edit.gravity"),
    TOGGLE_BASE("edit.base"),

    MODIFY_LEFT_ARM("edit.left_arm"),
    MODIFY_RIGHT_ARM("edit.right_arm"),
    MODIFY_LEFT_LEG("edit.left_leg"),
    MODIFY_RIGHT_LEG("edit.right_leg"),
    MODIFY_HEAD("edit.head"),
    MODIFY_BODY("edit.body"),

    RESET_POSE("edit.reset_pose"),
    FLIP_POSE("edit.flip_pose"),

    COPY("edit.copy"),
    PASTE("edit.paste"),
    INVENTORY("edit.inventory"),
    RENAME("edit.rename");


    public final String permission;

    EditorActions(String permission) {
        this.permission = permission;
    }
}

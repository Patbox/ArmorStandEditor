![](https://i.imgur.com/bWlrGhT.png)

# Armor Stand Editor
It's a simple, in game, server side Armor Stand editor. 
It's great way to allow users to add details in a simple way.
Additionally, it's fully survival friendly, so it can be safely 
used on survival (and alike) servers.

## Config
```json5
{
  "CONFIG_VERSION_DONT_TOUCH_THIS": 1,
  "armorStandTool": "minecraft:flint",   // Item that acts as editor, can be from a mod
  "requireIsArmorStandEditorTag": false, // Makes item require nbt tag isArmorStandEditor of item
  "toggleAllPermissionOnByDefault": true // Grants everyone permissions to every option in editor,
}
```
Additionally, command /armorstandeditor uses `armorstandeditor.commands.<subcommand or main>`.
Format of editors permissions looks like `armorstandeditor.edit.<type>`, [look at this file](https://github.com/Patbox/ArmorStandEditor/blob/master/src/main/java/eu/pb4/armorstandeditor/EditorActions.java)

If you want to allow others to modify entities disguised as Armor Stands, give them `armorstandeditor.useDisguised` permission
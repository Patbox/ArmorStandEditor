![](https://i.imgur.com/bWlrGhT.png)

# Armor Stand Editor
It's a simple, in game, server side Armor Stand editor. 
It's great way to allow users to add details in a simple way.
Additionally, it's fully survival friendly, so it can be safely 
used on survival (and alike) servers.

## Usage
Click with flint (or other configured item) in your hand anywhere to select action you want to perform.
After doing so, click on armor stand to apply it. It's simple as that!

![](https://i.imgur.com/Hh8IzWT.png)

## Config
```json5
{
  "CONFIG_VERSION_DONT_TOUCH_THIS": 1,
  "armorStandTool": "minecraft:flint",   // Item that acts as editor, can be from a mod
  "requireIsArmorStandEditorTag": false, // Makes item require nbt tag isArmorStandEditor of item
  "toggleAllPermissionOnByDefault": true,// Grants everyone permissions to every option in editor,
  "holdingToolSpawnsParticles": true,    // If true, player will see particles around armor stand while holding tool
  "blackListedBuildInPresets": []        // Removes default presets
}
```
Additionally, command /armorstandeditor uses `armorstandeditor.commands.<subcommand or main>`.
Format of editors permissions looks like `armorstandeditor.edit.<type>`, [look at this file](https://github.com/Patbox/ArmorStandEditor/blob/master/src/main/java/eu/pb4/armorstandeditor/EditorActions.java)

For Armor Stand Editing you can give players `armorstandeditor.use` permission and for Item Flames `armorstandeditor.useItemFrame`
If you want to allow others to modify entities disguised as Armor Stands, give them `armorstandeditor.useDisguised` permission,

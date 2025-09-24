# Armor Stand Editor
![](https://i.imgur.com/bWlrGhT.png)

Armor Stand Editor a simple to use mod, allowing you to pose/modify Armor Stand in game,
without need to use commands or external websites.
It was designed with survival usage in mind, so you can use it on your survival world/server without having
to worry about it breaking the experience.

It is compatible with Fabric and Quilt and can work purely server side, allowing vanilla
players to use all capabilities of it, while still being fully functional in singleplayer. 

## Usage
First you need to get flint (or other configured item). Then you just click with it Armor Stand 
you want to modify. Your hotbar will get replaced with setting selector (only visually, your inventory is untouched).
By using right mouse button (use/place action), you can enter/activate action bound to it (which is described
in its name). To change angle/value of movement/pose related options, sneak while having it selected and use scroll
to change value up or down.

![](https://i.imgur.com/rXyXQBQ.png)

To go back you can click the barrier/icon on right side.
To close it completely, press Q (drop item).

![](https://i.imgur.com/uDIaSBm.gif)

## Showcase
- Showcase by Patbox: https://www.youtube.com/watch?v=E7eCwKVZeqY
- Polish showcase by Patbox: https://www.youtube.com/watch?v=6aqd5d9NkeU

## Config
```json5
{
  "CONFIG_VERSION_DONT_TOUCH_THIS": 2,
  "armorStandTool": "minecraft:flint",   // Item that acts as editor, can be from a mod
  "requireIsArmorStandEditorTag": false, // Makes item require nbt tag isArmorStandEditor of item
  "holdingToolSpawnsParticles": true,    // If true, player will see particles around armor stand while holding tool
  "useLegacyUiByDefault": false,         // Forces usage of legacy ui, not recommended
  "blackListedBuildInPresets": [],       // Removes default presets
  "allowedByDefault": [/*...*/]          // Actions allowed by default
}
```

## Permissions
Additionally, command /armorstandeditor uses `armorstandeditor.commands.<subcommand or main>`.
Format of editors permissions looks like `armorstandeditor.edit.<type>`, [look at this file](https://github.com/Patbox/ArmorStandEditor/blob/master/src/main/java/eu/pb4/armorstandeditor/EditorActions.java)

For Armor Stand Editing you can give players `armorstandeditor.use` permission and for Item Frames `armorstandeditor.useItemFrame`
If you want to allow others to modify entities disguised as Armor Stands, give them `armorstandeditor.useDisguised` permission,

By default, players can edit them as they want.

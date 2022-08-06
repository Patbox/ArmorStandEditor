package eu.pb4.armorstandeditor.gui;

import eu.pb4.armorstandeditor.EditorActions;
import eu.pb4.armorstandeditor.legacy.LegacyEditorGuis;
import net.minecraft.item.Items;

public class MainGui extends BaseGui {
    public MainGui(EditingContext context, int slot) {
        super(context, slot);
        this.rebuildUi();
        this.open();
    }

    @Override
    protected void buildUi() {
        this.addSlot(EditorActions.MOVE, switchElement(Items.MINECART, "type.move", MoveGui::new));
        this.addSlot(EditorActions.MODIFY_POSE, switchElement(Items.STICK, "type.pose", PoseListGui::new));
        this.addSlot(EditorActions.INVENTORY, switchElement(Items.CHEST, "type.inventory", LegacyEditorGuis.getInventoryEditor()));
        this.addSlot(EditorActions.RENAME, switchElement(Items.NAME_TAG, "type.rename", LegacyEditorGuis.getRenamingGui()));
        this.addSlot(EditorActions.TOGGLE_PROPERTIES, switchElement(Items.FEATHER, "type.properties", PropertyGui::new));
        this.addSlot(EditorActions.COPY, switchElement(Items.SLIME_BALL, "type.copying", CopyGui::new));
    }

    @Override
    protected SwitchEntry asSwitchableUi() {
        return new SwitchEntry(MainGui::new, this.getSelectedSlot());
    }
}

package eu.pb4.armorstandeditor.gui;

import eu.pb4.armorstandeditor.EditorActions;
import net.minecraft.world.item.Items;

public class MainGui extends BaseWorldGui {
    public MainGui(EditingContext context, int slot) {
        super(context, slot);
        this.rebuildUi();
        this.open();
    }

    @Override
    protected void buildUi() {
        this.addSlot(EditorActions.MOVE, switchElement(Items.MINECART, "type.move", MoveGui::new));
        this.addSlot(EditorActions.MODIFY_POSE, switchElement(Items.STICK, "type.pose", PoseListGui::new));
        this.addSlot(EditorActions.INVENTORY, switchElement(Items.OAK_CHEST_BOAT, "type.inventory", InventoryEditGui::new));
        this.addSlot(EditorActions.RENAME, switchElement(Items.NAME_TAG, "type.rename", RenameGui::new));
        this.addSlot(EditorActions.TOGGLE_PROPERTIES, switchElement(Items.FEATHER, "type.properties", PropertyGui::new));
        this.addSlot(EditorActions.COPY, switchElement(Items.SLIME_BALL, "type.copying", CopyGui::new));
    }

    @Override
    protected EditingContext.SwitchEntry asSwitchableUi() {
        return new EditingContext.SwitchEntry(MainGui::new, this.getSelectedSlot());
    }
}

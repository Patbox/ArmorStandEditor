package eu.pb4.armorstandeditor.gui;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public final class EditingContext {
    public final ServerPlayerEntity player;
    public final ArmorStandEntity armorStand;
    public final List<SwitchEntry> interfaceList = new ArrayList<>();
    public double moveBlockDelta = 1;
    public double scaleDelta = 0.2;
    public int moveRotationDelta = 30;
    public int rotationDelta = 30;


    public EditingContext(ServerPlayerEntity player, ArmorStandEntity armorStand) {
        this.player = player;
        this.armorStand = armorStand;
    }

    public void close() {

    }

    public boolean checkClosed() {
        return this.armorStand != null && (this.armorStand.isRemoved() || this.armorStand.getPos().squaredDistanceTo(player.getPos()) > 48 * 48);
    }

    @FunctionalInterface
    public interface SwitchableUi {
        void openUi(EditingContext context, int selectedSlot);
    }

    public record SwitchEntry(SwitchableUi ui, int currentSlot) {
        public static SwitchEntry ofChest(SwitchableUi ui) {
            return new SwitchEntry(ui, -1);
        }

        public void open(EditingContext context) {
            ui.openUi(context, currentSlot);
        }
    }
}

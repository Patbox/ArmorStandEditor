package eu.pb4.armorstandeditor.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;

public final class EditingContext {
    public final ServerPlayer player;
    public final ArmorStand armorStand;
    public final List<SwitchEntry> interfaceList = new ArrayList<>();
    public double moveBlockDelta = 1;
    public double scaleDelta = 0.2;
    public int moveRotationDelta = 30;
    public int rotationDelta = 30;


    public EditingContext(ServerPlayer player, ArmorStand armorStand) {
        this.player = player;
        this.armorStand = armorStand;
    }

    public void close() {

    }

    public boolean checkClosed() {
        return this.armorStand != null && (this.armorStand.isRemoved() || this.armorStand.position().distanceToSqr(player.position()) > 48 * 48);
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

package eu.pb4.armorstandeditor.gui;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public final class EditingContext {
    public final ServerPlayerEntity player;
    public final ArmorStandEntity armorStand;
    public final List<BaseGui.SwitchEntry> interfaceList = new ArrayList<>();
    public BaseGui currentUi;
    public double moveBlockDelta = 1;
    public int moveRotationDelta = 30;
    public int rotationDelta = 30;


    public EditingContext(ServerPlayerEntity player, ArmorStandEntity armorStand) {
        this.player = player;
        this.armorStand = armorStand;
    }

    public void close() {

    }
}

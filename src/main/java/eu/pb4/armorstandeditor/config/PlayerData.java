package eu.pb4.armorstandeditor.config;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.JsonDataStorage;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    public static final JsonDataStorage<PlayerData> STORAGE = new JsonDataStorage<>("armor_stand_editor", PlayerData.class);
    public static PlayerData get(ServerPlayerEntity player) {
        var x = PlayerDataApi.getCustomDataFor(player, STORAGE);
        if (x == null) {
            return new PlayerData();
        }
        return x;
    }
    public int version = 1;
    public List<ArmorStandPreset> presets = new ArrayList<>();


    public void save(ServerPlayerEntity player) {
        PlayerDataApi.setCustomDataFor(player, STORAGE, this);
    }
}

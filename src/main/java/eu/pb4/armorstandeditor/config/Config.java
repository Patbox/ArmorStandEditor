package eu.pb4.armorstandeditor.config;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registries;


public class Config {
    public final ConfigData configData;
    public final Item armorStandTool;

    public Config(ConfigData data) {
        this.configData = data;
        this.armorStandTool = Registries.ITEM.get(Identifier.tryParse(data.armorStandTool));
    }
}

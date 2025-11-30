package eu.pb4.armorstandeditor.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;


public class Config {
    public final ConfigData configData;
    public final Item armorStandTool;

    public Config(ConfigData data) {
        this.configData = data;
        this.armorStandTool = BuiltInRegistries.ITEM.getValue(Identifier.tryParse(data.armorStandTool));
    }
}

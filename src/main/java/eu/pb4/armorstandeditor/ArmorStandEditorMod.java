package eu.pb4.armorstandeditor;

import eu.pb4.armorstandeditor.config.ConfigManager;
import eu.pb4.armorstandeditor.helpers.GeneralCommands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArmorStandEditorMod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("Armor Stand Editor");
	public static String VERSION = FabricLoader.getInstance().getModContainer("armor-stand-editor").get().getMetadata().getVersion().getFriendlyString();

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
			ConfigManager.loadConfig();
		});
		GeneralCommands.register();
		Events.registerEvents();
	}
}

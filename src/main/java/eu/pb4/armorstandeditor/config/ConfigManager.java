package eu.pb4.armorstandeditor.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.pb4.armorstandeditor.ArmorStandEditorMod;

import java.io.*;
import java.nio.file.Paths;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static Config CONFIG;

    public static Config getConfig() {
        return CONFIG;
    }

    public static boolean loadConfig() {
        Config oldConfig = CONFIG;
        boolean success;

        CONFIG = null;
        try {
            File configDir = Paths.get("", "config", "armor-stand-editor").toFile();

            configDir.mkdirs();

            File configFile = new File(configDir, "config.json");

            ConfigData configData = configFile.exists() ? GSON.fromJson(new InputStreamReader(new FileInputStream(configFile), "UTF-8"), ConfigData.class) : new ConfigData();

            CONFIG = new Config(configData);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile), "UTF-8"));
            writer.write(GSON.toJson(configData));
            writer.close();


            success = true;
        }
        catch(IOException exception) {
            success = false;
            CONFIG = oldConfig;
            ArmorStandEditorMod.LOGGER.error("Something went wrong while reading config!");
            exception.printStackTrace();
        }

        return success;
    }
}

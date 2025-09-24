package eu.pb4.armorstandeditor.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.pb4.armorstandeditor.ArmorStandEditorMod;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Pattern;

public class ConfigManager {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final HashMap<String, ArmorStandPreset> PRESETS = new HashMap<>();
    public static final Pattern INVALID_CHAR = Pattern.compile("[^a-z0-9_]");

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
            File presetsDir = Paths.get("", "config", "armor-stand-editor", "presets").toFile();

            presetsDir.mkdirs();

            File configFile = new File(configDir, "config.json");

            ConfigData configData = configFile.exists() ? GSON.fromJson(new InputStreamReader(new FileInputStream(configFile), "UTF-8"), ConfigData.class) : new ConfigData();

            configData.update();


            CONFIG = new Config(configData);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile), "UTF-8"));
            writer.write(GSON.toJson(configData));
            writer.close();

            PRESETS.clear();

            Optional<ModContainer> containerOptional = FabricLoader.getInstance().getModContainer("armor-stand-editor");

            if (containerOptional.isPresent()) {
                ModContainer container = containerOptional.get();
                Path buildInPresets = container.getPath("presets");

                Files.walkFileTree(buildInPresets, new FileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        ArmorStandPreset preset = GSON.fromJson(new InputStreamReader(Files.newInputStream(file), "UTF-8"), ArmorStandPreset.class);

                        if (!CONFIG.configData.blockedBuiltinPresets.contains(preset.id)
                                && !CONFIG.configData.blockedBuiltinPresets.contains(preset.id.substring(1))
                                && !CONFIG.configData.blockedBuiltinPresets.contains("buildin/" + preset.id.substring(1))) {
                            PRESETS.put(preset.id, preset);
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }
                });
            } else {
                ArmorStandEditorMod.LOGGER.error("Something went really badly while getting buildin presets. Did someone change modid? :irritater:");
            }

            for (String name : presetsDir.list((file, name) -> name.endsWith(".json"))) {
                File file2 = new File(presetsDir, name);

                ArmorStandPreset preset = GSON.fromJson(new InputStreamReader(new FileInputStream(file2), "UTF-8"), ArmorStandPreset.class);
                String tmp = name.substring(0, name.length() - 5);
                file2.delete();

                preset.id = tmp.replaceAll(INVALID_CHAR.pattern(), "_");
                PRESETS.put(preset.id, preset);

                BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2), "UTF-8"));
                writer2.write(GSON.toJson(preset));
                writer2.close();
            }

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

    public static boolean savePreset(ArmorStandPreset preset) {
        try {
            File presetsDir = Paths.get("", "config", "armor-stand-editor", "presets").toFile();

            presetsDir.mkdirs();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(presetsDir, preset.id + ".json")), "UTF-8"));
            writer.write(GSON.toJson(preset));
            writer.close();
            PRESETS.put(preset.id, preset);

            return true;
        } catch (Exception e) {
            ArmorStandEditorMod.LOGGER.error("Couldn't save preset " + preset.id);
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deletePreset(String id) {
        try {
            File preset = Paths.get("", "config", "armor-stand-editor", "presets", id + ".json").toFile();

            if (id.startsWith("$")) {
                return false;
            }
            PRESETS.remove(id);

            if (preset.exists()) {
                preset.delete();
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

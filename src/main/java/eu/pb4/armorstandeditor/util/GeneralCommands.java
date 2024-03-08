package eu.pb4.armorstandeditor.util;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.armorstandeditor.GenericModInfo;
import eu.pb4.armorstandeditor.config.ArmorStandPreset;
import eu.pb4.armorstandeditor.config.ConfigManager;
import eu.pb4.armorstandeditor.legacy.LegacyPlayerExt;
import eu.pb4.playerdata.api.PlayerDataApi;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtByte;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Iterator;

import static net.minecraft.server.command.CommandManager.literal;

public class GeneralCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    literal("armorstandeditor")
                            .requires(Permissions.require("armor_stand_editor.commands.main", true))
                            .executes(GeneralCommands::about)
                            .then(literal("reload")
                                    .requires(Permissions.require("armor_stand_editor.commands.reload", 4))
                                    .executes(GeneralCommands::reloadConfig)
                            )
                            .then(literal("give")
                                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                                            .requires(Permissions.require("armor_stand_editor.commands.give", 2))
                                            .executes(GeneralCommands::giveTool)
                            ))
                            .then(literal("save-preset")
                                    .then(CommandManager.argument("id", StringArgumentType.word())
                                            .then(CommandManager.argument("name", StringArgumentType.greedyString())
                                                    .requires(Permissions.require("armor_stand_editor.commands.save_preset", 3))
                                                    .executes(GeneralCommands::savePreset)
                                            )
                                    ))
                            .then(literal("delete-preset")
                                    .then(CommandManager.argument("id", StringArgumentType.word())
                                            .requires(Permissions.require("armor_stand_editor.commands.delete_preset", 3))
                                            .executes(GeneralCommands::deletePreset)
                                    ))
                            .then(literal("list-preset")
                                            .requires(Permissions.require("armor_stand_editor.commands.list_presets", 3))
                                            .executes(GeneralCommands::listPresets)
                                    )
                            .then(literal("switchui")
                                    .requires(Permissions.require("armor_stand_editor.commands.switch_ui", 0))
                                    .executes(GeneralCommands::switchUi)
                            )
            );
            });
        }

    private static int switchUi(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        boolean current = LegacyPlayerExt.useLegacy(context.getSource().getPlayerOrThrow());
        PlayerDataApi.setGlobalDataFor(context.getSource().getPlayer(), LegacyPlayerExt.LEGACY_UI, NbtByte.of(!current));
        context.getSource().sendFeedback(() -> TextUtils.command("switchui." + (current ? "main" : "legacy")), false);
        return 0;
    }

    private static int savePreset(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();

        var spei = (PlayerExt) player;

        String id = context.getArgument("id", String.class);
        String name = context.getArgument("name", String.class);

        if (ConfigManager.INVALID_CHAR.matcher(id).matches()) {
            context.getSource().sendFeedback(() -> TextUtils.command("invalid-id", id).formatted(Formatting.RED), false);
            return 0;
        }

        if (spei.ase$getArmorStandEditorData() != null) {
            ArmorStandPreset preset = new ArmorStandPreset(id, name, player.getGameProfile().getName());
            preset.fromData(spei.ase$getArmorStandEditorData());

            ConfigManager.savePreset(preset);

            context.getSource().sendFeedback(() -> TextUtils.command("save-preset.success", name, id), false);
        } else {
            context.getSource().sendFeedback(() -> TextUtils.command("save-preset.fail", name, id).formatted(Formatting.RED), false);
        }

        return 0;
    }

    private static int deletePreset(CommandContext<ServerCommandSource> context) {
        String id = context.getArgument("id", String.class);

        if (ConfigManager.INVALID_CHAR.matcher(id).matches()) {
            context.getSource().sendFeedback(() -> TextUtils.command("invalid-id", id).formatted(Formatting.RED), false);
            return 0;
        }

        if (ConfigManager.deletePreset(id)) {
            context.getSource().sendFeedback(() -> TextUtils.command("delete-preset.success", id), false);
        } else {
            context.getSource().sendFeedback(() -> TextUtils.command("delete-preset.fail", id).formatted(Formatting.RED), false);
        }

        return 0;
    }

    private static int listPresets(CommandContext<ServerCommandSource> context) {
        MutableText text = Text.literal("").formatted(Formatting.DARK_GRAY);

        Iterator<ArmorStandPreset> iterator = ConfigManager.PRESETS.values().iterator();

        while (iterator.hasNext()) {
            ArmorStandPreset preset = iterator.next();
            if (preset.id.startsWith("$")) {
                text.append(Text.literal(preset.name)
                        .formatted(Formatting.YELLOW)
                        .append(Text.literal(" (").formatted(Formatting.GRAY)
                                .append(Text.literal("buildin/" + preset.id.substring(1)).formatted(Formatting.RED))
                                .append(Text.literal(")").formatted(Formatting.GRAY))));
            } else {
                text.append(Text.literal(preset.name)
                        .formatted(Formatting.WHITE)
                        .append(Text.literal(" (").formatted(Formatting.GRAY)
                                .append(Text.literal(preset.id).formatted(Formatting.BLUE))
                                .append(Text.literal(")").formatted(Formatting.GRAY))));
            }

            if (iterator.hasNext()) {
                text.append(Text.literal(", "));
            }
        }

        context.getSource().sendFeedback(() -> text, false);

        return 0;
    }

    private static int giveTool(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ItemStack itemStack = ConfigManager.getConfig().armorStandTool.getDefaultStack();
        itemStack.get(DataComponentTypes.CUSTOM_DATA).getNbt().putBoolean("isArmorStandEditor", true);
        EntitySelector entitySelector = context.getArgument("targets", EntitySelector.class);

        for (ServerPlayerEntity player : entitySelector.getPlayers(context.getSource())) {
            player.getInventory().offerOrDrop(itemStack);
            context.getSource().sendFeedback(() -> TextUtils.command("give", player.getDisplayName()), true);
        }


        return 1;
    }

    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        if (ConfigManager.loadConfig()) {
            context.getSource().sendFeedback(() -> Text.literal("Reloaded config!"), false);
        } else {
            context.getSource().sendError(Text.literal("Error accrued while reloading config!").formatted(Formatting.RED));
        }
        return 1;
    }

    private static int about(CommandContext<ServerCommandSource> context) {
        //context.getSource().sendFeedback(Text.literal("Armor Stand Editor - ").formatted(Formatting.GOLD).append(Text.literal(ArmorStandEditorMod.VERSION).formatted(Formatting.WHITE)), false);
        for (var t : context.getSource().isExecutedByPlayer() ? GenericModInfo.getAboutFull() : GenericModInfo.getAboutConsole()) {
            context.getSource().sendFeedback(() -> t, false);
        }

        return 1;
    }
}

package eu.pb4.armorstandeditor.helpers;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.armorstandeditor.ArmorStandEditorMod;
import eu.pb4.armorstandeditor.config.ArmorStandPreset;
import eu.pb4.armorstandeditor.config.ConfigManager;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.Iterator;

import static net.minecraft.server.command.CommandManager.literal;

public class GeneralCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(
                    literal("armorstandeditor")
                            .requires(Permissions.require("armorstandeditor.commands.main", true))
                            .executes(GeneralCommands::about)
                            .then(literal("reload")
                                    .requires(Permissions.require("armorstandeditor.commands.reload", 4))
                                    .executes(GeneralCommands::reloadConfig)
                            )
                            .then(literal("give")
                                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                                            .requires(Permissions.require("armorstandeditor.commands.give", 2))
                                            .executes(GeneralCommands::giveTool)
                            ))
                            .then(literal("save-preset")
                                    .then(CommandManager.argument("id", StringArgumentType.word())
                                            .then(CommandManager.argument("name", StringArgumentType.greedyString())
                                                    .requires(Permissions.require("armorstandeditor.commands.save-preset", 3))
                                                    .executes(GeneralCommands::savePreset)
                                            )
                                    ))
                            .then(literal("delete-preset")
                                    .then(CommandManager.argument("id", StringArgumentType.word())
                                            .requires(Permissions.require("armorstandeditor.commands.delete-preset", 3))
                                            .executes(GeneralCommands::deletePreset)
                                    ))
                            .then(literal("list-preset")
                                            .requires(Permissions.require("armorstandeditor.commands.list-presets", 3))
                                            .executes(GeneralCommands::listPresets)
                                    )
            );
            });
        }

    private static int savePreset(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        SPEInterface spei = (SPEInterface) player;

        String id = context.getArgument("id", String.class);
        String name = context.getArgument("name", String.class);

        if (ConfigManager.INVALID_CHAR.matcher(id).matches()) {
            context.getSource().sendFeedback(new TranslatableText("armorstandeditor.command.invalid-id", id).formatted(Formatting.RED), false);
            return 0;
        }

        if (spei.getArmorStandEditorData() != null) {
            ArmorStandPreset preset = new ArmorStandPreset(id, name, player.getGameProfile().getName());
            preset.fromData(spei.getArmorStandEditorData());

            ConfigManager.savePreset(preset);

            context.getSource().sendFeedback(new TranslatableText("armorstandeditor.command.save-preset.success", name, id), false);
        } else {
            context.getSource().sendFeedback(new TranslatableText("armorstandeditor.command.save-preset.fail", name, id).formatted(Formatting.RED), false);
        }

        return 0;
    }

    private static int deletePreset(CommandContext<ServerCommandSource> context) {
        String id = context.getArgument("id", String.class);

        if (ConfigManager.INVALID_CHAR.matcher(id).matches()) {
            context.getSource().sendFeedback(new TranslatableText("armorstandeditor.command.invalid-id", id).formatted(Formatting.RED), false);
            return 0;
        }

        if (ConfigManager.deletePreset(id)) {
            context.getSource().sendFeedback(new TranslatableText("armorstandeditor.command.delete-preset.success", id), false);
        } else {
            context.getSource().sendFeedback(new TranslatableText("armorstandeditor.command.delete-preset.fail", id).formatted(Formatting.RED), false);
        }

        return 0;
    }

    private static int listPresets(CommandContext<ServerCommandSource> context) {
        MutableText text = new LiteralText("").formatted(Formatting.DARK_GRAY);

        Iterator<ArmorStandPreset> iterator = ConfigManager.PRESETS.values().iterator();

        while (iterator.hasNext()) {
            ArmorStandPreset preset = iterator.next();
            if (preset.id.startsWith("$")) {
                text.append(new LiteralText(preset.name)
                        .formatted(Formatting.YELLOW)
                        .append(new LiteralText(" (").formatted(Formatting.GRAY)
                                .append(new LiteralText("buildin/" + preset.id.substring(1)).formatted(Formatting.RED))
                                .append(new LiteralText(")").formatted(Formatting.GRAY))));
            } else {
                text.append(new LiteralText(preset.name)
                        .formatted(Formatting.WHITE)
                        .append(new LiteralText(" (").formatted(Formatting.GRAY)
                                .append(new LiteralText(preset.id).formatted(Formatting.BLUE))
                                .append(new LiteralText(")").formatted(Formatting.GRAY))));
            }

            if (iterator.hasNext()) {
                text.append(new LiteralText(", "));
            }
        }

        context.getSource().sendFeedback(text, false);

        return 0;
    }

    private static int giveTool(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ItemStack itemStack = ConfigManager.getConfig().armorStandTool.getDefaultStack();
        itemStack.getOrCreateTag().putBoolean("isArmorStandEditor", true);
        EntitySelector entitySelector = context.getArgument("targets", EntitySelector.class);

        for (ServerPlayerEntity player : entitySelector.getPlayers(context.getSource())) {
            player.getInventory().offerOrDrop(itemStack);
            context.getSource().sendFeedback(new TranslatableText("armorstandeditor.command.give", player.getDisplayName()), true);
        }


        return 1;
    }

    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        if (ConfigManager.loadConfig()) {
            context.getSource().sendFeedback(new LiteralText("Reloaded config!"), false);
        } else {
            context.getSource().sendError(new LiteralText("Error accrued while reloading config!").formatted(Formatting.RED));
        }
        return 1;
    }

    private static int about(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(new LiteralText("Armor Stand Editor - ").formatted(Formatting.GOLD).append(new LiteralText(ArmorStandEditorMod.VERSION).formatted(Formatting.WHITE)), false);
        return 1;
    }
}

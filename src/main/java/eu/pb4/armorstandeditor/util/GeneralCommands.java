package eu.pb4.armorstandeditor.util;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.armorstandeditor.GenericModInfo;
import eu.pb4.armorstandeditor.config.ArmorStandPreset;
import eu.pb4.armorstandeditor.config.ConfigManager;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import java.util.Iterator;

import static net.minecraft.commands.Commands.literal;

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
                                    .then(Commands.argument("targets", EntityArgument.players())
                                            .requires(Permissions.require("armor_stand_editor.commands.give", 2))
                                            .executes(GeneralCommands::giveTool)
                            ))
                            .then(literal("save-preset")
                                    .then(Commands.argument("id", StringArgumentType.word())
                                            .then(Commands.argument("name", StringArgumentType.greedyString())
                                                    .requires(Permissions.require("armor_stand_editor.commands.save_preset", 3))
                                                    .executes(GeneralCommands::savePreset)
                                            )
                                    ))
                            .then(literal("delete-preset")
                                    .then(Commands.argument("id", StringArgumentType.word())
                                            .requires(Permissions.require("armor_stand_editor.commands.delete_preset", 3))
                                            .executes(GeneralCommands::deletePreset)
                                    ))
                            .then(literal("list-preset")
                                            .requires(Permissions.require("armor_stand_editor.commands.list_presets", 3))
                                            .executes(GeneralCommands::listPresets)
                                    )
            );
            });
        }



    private static int savePreset(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        var spei = (PlayerExt) player;

        String id = context.getArgument("id", String.class);
        String name = context.getArgument("name", String.class);

        if (ConfigManager.INVALID_CHAR.matcher(id).matches()) {
            context.getSource().sendSuccess(() -> TextUtils.command("invalid-id", id).withStyle(ChatFormatting.RED), false);
            return 0;
        }

        if (spei.ase$getArmorStandEditorData() != null) {
            ArmorStandPreset preset = new ArmorStandPreset(id, name, player.getGameProfile().name());
            preset.fromData(spei.ase$getArmorStandEditorData());

            ConfigManager.savePreset(preset);

            context.getSource().sendSuccess(() -> TextUtils.command("save-preset.success", name, id), false);
        } else {
            context.getSource().sendSuccess(() -> TextUtils.command("save-preset.fail", name, id).withStyle(ChatFormatting.RED), false);
        }

        return 0;
    }

    private static int deletePreset(CommandContext<CommandSourceStack> context) {
        String id = context.getArgument("id", String.class);

        if (ConfigManager.INVALID_CHAR.matcher(id).matches()) {
            context.getSource().sendSuccess(() -> TextUtils.command("invalid-id", id).withStyle(ChatFormatting.RED), false);
            return 0;
        }

        if (ConfigManager.deletePreset(id)) {
            context.getSource().sendSuccess(() -> TextUtils.command("delete-preset.success", id), false);
        } else {
            context.getSource().sendSuccess(() -> TextUtils.command("delete-preset.fail", id).withStyle(ChatFormatting.RED), false);
        }

        return 0;
    }

    private static int listPresets(CommandContext<CommandSourceStack> context) {
        MutableComponent text = Component.literal("").withStyle(ChatFormatting.DARK_GRAY);

        Iterator<ArmorStandPreset> iterator = ConfigManager.PRESETS.values().iterator();

        while (iterator.hasNext()) {
            ArmorStandPreset preset = iterator.next();
            if (preset.id.startsWith("$")) {
                text.append(Component.literal(preset.name)
                        .withStyle(ChatFormatting.YELLOW)
                        .append(Component.literal(" (").withStyle(ChatFormatting.GRAY)
                                .append(Component.literal("buildin/" + preset.id.substring(1)).withStyle(ChatFormatting.RED))
                                .append(Component.literal(")").withStyle(ChatFormatting.GRAY))));
            } else {
                text.append(Component.literal(preset.name)
                        .withStyle(ChatFormatting.WHITE)
                        .append(Component.literal(" (").withStyle(ChatFormatting.GRAY)
                                .append(Component.literal(preset.id).withStyle(ChatFormatting.BLUE))
                                .append(Component.literal(")").withStyle(ChatFormatting.GRAY))));
            }

            if (iterator.hasNext()) {
                text.append(Component.literal(", "));
            }
        }

        context.getSource().sendSuccess(() -> text, false);

        return 0;
    }

    private static int giveTool(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ItemStack itemStack = ConfigManager.getConfig().armorStandTool.getDefaultInstance();
        var nbt = new CompoundTag();
        nbt.putBoolean("isArmorStandEditor", true);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
        EntitySelector entitySelector = context.getArgument("targets", EntitySelector.class);

        for (ServerPlayer player : entitySelector.findPlayers(context.getSource())) {
            player.getInventory().placeItemBackInInventory(itemStack);
            context.getSource().sendSuccess(() -> TextUtils.command("give", player.getDisplayName()), true);
        }


        return 1;
    }

    private static int reloadConfig(CommandContext<CommandSourceStack> context) {
        if (ConfigManager.loadConfig()) {
            context.getSource().sendSuccess(() -> Component.literal("Reloaded config!"), false);
        } else {
            context.getSource().sendFailure(Component.literal("Error accrued while reloading config!").withStyle(ChatFormatting.RED));
        }
        return 1;
    }

    private static int about(CommandContext<CommandSourceStack> context) {
        //context.getSource().sendFeedback(Text.literal("Armor Stand Editor - ").formatted(Formatting.GOLD).append(Text.literal(ArmorStandEditorMod.VERSION).formatted(Formatting.WHITE)), false);
        for (var t : context.getSource().isPlayer() ? GenericModInfo.getAboutFull() : GenericModInfo.getAboutConsole()) {
            context.getSource().sendSuccess(() -> t, false);
        }

        return 1;
    }
}

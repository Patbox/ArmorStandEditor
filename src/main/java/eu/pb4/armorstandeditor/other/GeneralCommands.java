package eu.pb4.armorstandeditor.other;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.armorstandeditor.ArmorStandEditorMod;
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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public class GeneralCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(
                    literal("armorstandeditor")
                            .requires(Permissions.require("armorstandeditor.commands.main", true))
                            .executes(GeneralCommands::about)
                            .then(literal("reload")
                                    .requires(Permissions.require("armorstandeditor.commands.reload", 3))
                                    .executes(GeneralCommands::reloadConfig)
                            )
                            .then(literal("give")
                                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                                            .requires(Permissions.require("armorstandeditor.commands.give", 1))
                                            .executes(GeneralCommands::giveTool)
                            ))
                );
            });
        }

    private static int giveTool(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ItemStack itemStack = ConfigManager.getConfig().armorStandTool.getDefaultStack();
        itemStack.getOrCreateTag().putBoolean("isArmorStandEditor", true);
        EntitySelector entitySelector = context.getArgument("targets", EntitySelector.class);

        for (ServerPlayerEntity player : entitySelector.getPlayers(context.getSource())) {
            player.inventory.offerOrDrop(player.world, itemStack);
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

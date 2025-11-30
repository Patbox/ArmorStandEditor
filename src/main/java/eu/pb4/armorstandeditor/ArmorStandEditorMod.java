package eu.pb4.armorstandeditor;

import eu.pb4.armorstandeditor.config.ConfigManager;
import eu.pb4.armorstandeditor.config.PlayerData;
import eu.pb4.armorstandeditor.gui.EditingContext;
import eu.pb4.armorstandeditor.gui.ItemFrameEditorGui;
import eu.pb4.armorstandeditor.gui.MainGui;
import eu.pb4.armorstandeditor.util.GeneralCommands;
import eu.pb4.armorstandeditor.util.TextUtils;
import eu.pb4.common.protection.api.CommonProtection;
import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.sgui.api.GuiHelpers;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.disguiselib.api.EntityDisguise;

import java.util.stream.Collectors;

public class ArmorStandEditorMod implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("Armor Stand Editor");
    public static String VERSION = FabricLoader.getInstance().getModContainer("armor-stand-editor").get().getMetadata().getVersion().getFriendlyString();

    @Override
    public void onInitialize() {
        GenericModInfo.build(FabricLoader.getInstance().getModContainer("armor-stand-editor").get());

        PlayerDataApi.register(PlayerData.STORAGE);

        ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
            ConfigManager.loadConfig();
        });

        ServerLifecycleEvents.SERVER_STARTED.register((s) -> {
            CardboardWarning.checkAndAnnounce();
        });
        GeneralCommands.register();

        final var checkDisguise = FabricLoader.getInstance().isModLoaded("disguiselib");

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide()) {
                return InteractionResult.PASS;
            }

            ItemStack itemStack = player.getMainHandItem();

            if (player instanceof ServerPlayer serverPlayer &&
                    entity instanceof ItemFrame frameEntity
                    && hasCorrectToolAndPermsItemFrame(serverPlayer, itemStack, entity)
            ) {
                new ItemFrameEditorGui(serverPlayer, frameEntity);
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide()) {
                return InteractionResult.PASS;
            }

            ItemStack itemStack = player.getItemInHand(hand);
            if (player instanceof ServerPlayer serverPlayer
                    && hasCorrectToolAndPerms(serverPlayer, itemStack, entity)
            ) {


                if (checkDisguise) {
                    if (entity instanceof EntityDisguise disguise && disguise.isDisguised()) {
                        entity = disguise.getDisguiseEntity();
                    }
                }

                if (entity instanceof ArmorStand armorStandEntity) {
                    new MainGui(new EditingContext(serverPlayer, armorStandEntity), 0);
                    player.displayClientMessage(TextUtils.text("open_info", Component.keybind("key.drop")), true);
                    return InteractionResult.SUCCESS;
                }
            }

            return InteractionResult.PASS;
        });

    }

    public static boolean hasCorrectToolAndPerms(ServerPlayer player, ItemStack itemStack, @Nullable Entity entity) {
        var config = ConfigManager.getConfig();
        return GuiHelpers.getCurrentGui(player) == null
                && EditorActions.OPEN_EDITOR.canUse(player)
                && !player.isSpectator()
                && itemStack.getItem() == config.armorStandTool
                && (!config.configData.requireNbtTagForEditor
                || itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBooleanOr("isArmorStandEditor", false))
                && (!(entity instanceof ArmorStand armorStandEntity) || !armorStandEntity.isMarker())
                && (entity == null || CommonProtection.canInteractEntity(entity.level(), entity, player.getGameProfile(), player));
    }

    public static boolean hasCorrectToolAndPermsItemFrame(ServerPlayer player, ItemStack itemStack, @Nullable Entity entity) {
        var config = ConfigManager.getConfig();
        return GuiHelpers.getCurrentGui(player) == null
                && EditorActions.OPEN_ITEM_FRAME_EDITOR.canUse(player)
                && !player.isSpectator()
                && itemStack.getItem() == config.armorStandTool
                && (!config.configData.requireNbtTagForEditor
                || itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBooleanOr("isArmorStandEditor", false))
                && (entity == null ||  CommonProtection.canInteractEntity(entity.level(), entity, player.getGameProfile(), player));
    }
}

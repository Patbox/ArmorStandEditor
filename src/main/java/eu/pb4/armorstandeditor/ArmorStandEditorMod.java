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
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.disguiselib.api.EntityDisguise;

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
            if (world.isClient) {
                return ActionResult.PASS;
            }

            ItemStack itemStack = player.getMainHandStack();

            if (player instanceof ServerPlayerEntity serverPlayer &&
                    entity instanceof ItemFrameEntity frameEntity
                    && hasCorrectToolAndPermsItemFrame(serverPlayer, itemStack, entity)
            ) {
                new ItemFrameEditorGui(serverPlayer, frameEntity);
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient) {
                return ActionResult.PASS;
            }

            ItemStack itemStack = player.getStackInHand(hand);
            if (player instanceof ServerPlayerEntity serverPlayer
                    && hasCorrectToolAndPerms(serverPlayer, itemStack, entity)
            ) {


                if (checkDisguise) {
                    if (entity instanceof EntityDisguise disguise && disguise.isDisguised()) {
                        entity = disguise.getDisguiseEntity();
                    }
                }

                if (entity instanceof ArmorStandEntity armorStandEntity) {
                    new MainGui(new EditingContext(serverPlayer, armorStandEntity), 0);
                    player.sendMessage(TextUtils.text("open_info", Text.keybind("key.drop")), true);
                    return ActionResult.SUCCESS;
                }
            }

            return ActionResult.PASS;
        });

    }

    public static boolean hasCorrectToolAndPerms(ServerPlayerEntity player, ItemStack itemStack, @Nullable Entity entity) {
        var config = ConfigManager.getConfig();
        return GuiHelpers.getCurrentGui(player) == null
                && EditorActions.OPEN_EDITOR.canUse(player)
                && !player.isSpectator()
                && itemStack.getItem() == config.armorStandTool
                && (!config.configData.requireIsArmorStandEditorTag
                || itemStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).getNbt().getBoolean("isArmorStandEditor", false))
                && (!(entity instanceof ArmorStandEntity armorStandEntity) || !armorStandEntity.isMarker())
                && (entity == null || CommonProtection.canInteractEntity(entity.getWorld(), entity, player.getGameProfile(), player));
    }

    public static boolean hasCorrectToolAndPermsItemFrame(ServerPlayerEntity player, ItemStack itemStack, @Nullable Entity entity) {
        var config = ConfigManager.getConfig();
        return GuiHelpers.getCurrentGui(player) == null
                && EditorActions.OPEN_ITEM_FRAME_EDITOR.canUse(player)
                && !player.isSpectator()
                && itemStack.getItem() == config.armorStandTool
                && (!config.configData.requireIsArmorStandEditorTag
                || itemStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).getNbt().getBoolean("isArmorStandEditor", false))
                && (entity == null ||  CommonProtection.canInteractEntity(entity.getWorld(), entity, player.getGameProfile(), player));
    }
}

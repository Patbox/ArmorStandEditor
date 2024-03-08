package eu.pb4.armorstandeditor;

import eu.pb4.armorstandeditor.config.Config;
import eu.pb4.armorstandeditor.config.ConfigManager;
import eu.pb4.armorstandeditor.gui.EditingContext;
import eu.pb4.armorstandeditor.gui.MainGui;
import eu.pb4.armorstandeditor.util.GeneralCommands;
import eu.pb4.armorstandeditor.legacy.LegacyEvents;
import eu.pb4.armorstandeditor.legacy.LegacyPlayerExt;
import eu.pb4.armorstandeditor.util.TextUtils;
import eu.pb4.common.protection.api.CommonProtection;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.disguiselib.api.EntityDisguise;

public class ArmorStandEditorMod implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("Armor Stand Editor");
    public static String VERSION = FabricLoader.getInstance().getModContainer("armor-stand-editor").get().getMetadata().getVersion().getFriendlyString();

    @Override
    public void onInitialize() {
        GenericModInfo.build(FabricLoader.getInstance().getModContainer("armor-stand-editor").get());

        ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
            ConfigManager.loadConfig();
        });

        ServerLifecycleEvents.SERVER_STARTED.register((s) -> {
            CardboardWarning.checkAndAnnounce();
        });
        GeneralCommands.register();
        LegacyEvents.registerEvents();


        final var checkDisguise = FabricLoader.getInstance().isModLoaded("disguiselib");

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient) {
                return ActionResult.PASS;
            }

            if (LegacyPlayerExt.useLegacy(player)) {
                return ActionResult.PASS;
            }

            Config config = ConfigManager.getConfig();
            ItemStack itemStack = player.getStackInHand(hand);
            if (player instanceof ServerPlayerEntity
                    && EditorActions.OPEN_EDITOR.canUse(player)
                    && itemStack.getItem() == config.armorStandTool
                    && (!config.configData.requireIsArmorStandEditorTag || itemStack.get(DataComponentTypes.CUSTOM_DATA).getNbt().getBoolean("isArmorStandEditor"))
                    && CommonProtection.canInteractEntity(world, entity, player.getGameProfile(), player)
            ) {


                if (checkDisguise) {
                    if (entity instanceof EntityDisguise disguise && disguise.isDisguised()) {
                        entity = disguise.getDisguiseEntity();
                    }
                }

                if (entity instanceof ArmorStandEntity) {
                    new MainGui(new EditingContext((ServerPlayerEntity) player, (ArmorStandEntity) entity), 0);
                    player.sendMessage(TextUtils.text("open_info", Text.keybind("key.drop")), true);
                    return ActionResult.SUCCESS;
                }
            }

            return ActionResult.PASS;
        });

    }
}

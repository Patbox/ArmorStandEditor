package eu.pb4.armorstandeditor.mixin;


import com.mojang.authlib.GameProfile;
import eu.pb4.armorstandeditor.config.ConfigManager;
import eu.pb4.armorstandeditor.gui.BaseWorldGui;
import eu.pb4.armorstandeditor.util.ArmorStandData;
import eu.pb4.armorstandeditor.util.PlayerExt;
import eu.pb4.sgui.api.GuiHelpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.ARGB;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player implements PlayerExt {
    @Shadow
    public ServerGamePacketListenerImpl connection;

    @Unique
    private long ase$tickTimer = 0;

    @Unique
    private ArmorStandData ase$armorStandEditorData = null;

    public ServerPlayerEntityMixin(Level world, GameProfile profile)
    {
        super(world, profile);
    }

    @Inject(method = "hurtServer", at = @At("TAIL"))
    private void ase$closeOnDamage(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (GuiHelpers.getCurrentGui((ServerPlayer) (Object) this) instanceof BaseWorldGui baseGui) {
            baseGui.close();
        }
    }

    public ArmorStandData ase$getArmorStandEditorData() {
        return this.ase$armorStandEditorData;
    }

    public void ase$setArmorStandEditorData(ArmorStandData data) {
        this.ase$armorStandEditorData = data;
    }

    @Inject(method = "doTick", at = @At("HEAD"))
    private void ase$showInvisible(CallbackInfo ci) {
        try {
            if (ConfigManager.getConfig().configData.renderTargetParticles) {
                ase$tickTimer++;
                if (ase$tickTimer > 10 && this.getMainHandItem().getItem() == ConfigManager.getConfig().armorStandTool) {
                    ase$tickTimer = 0;
                    var armorStands = this.level().getEntitiesOfClass(ArmorStand.class, new AABB(this.blockPosition().offset(10, 10, 10).getCenter(), this.blockPosition().offset(-10, -10, -10).getCenter()), entity -> !entity.isMarker());

                    ParticleOptions particleEffect = new DustParticleOptions(ARGB.colorFromFloat(0, 0.8f, 0.2f, 0.2f), 1f);

                    for (ArmorStand armorStand : armorStands) {
                        this.connection.send(new ClientboundLevelParticlesPacket(particleEffect,
                                false,
                                false,
                                armorStand.getX(),
                                armorStand.getY() + armorStand.getBbHeight() / 2,
                                armorStand.getZ(),
                                0.2f, 0.2f, 0.2f, 0.1f, 3));
                    }

                    List<ItemFrame> itemFrames = this.level().getEntitiesOfClass(ItemFrame.class, new AABB(this.blockPosition().offset(10, 10, 10).getCenter(), this.blockPosition().offset(-10, -10, -10).getCenter()), entity -> true);

                    ParticleOptions particleEffect2 = new DustParticleOptions(ARGB.colorFromFloat(0, 0.2f, 0.8f, 0.2f), 1f);

                    for (ItemFrame itemFrame : itemFrames) {
                        this.connection.send(new ClientboundLevelParticlesPacket(particleEffect2,
                                false,
                                false,
                                itemFrame.getX(),
                                itemFrame.getY() + itemFrame.getBbHeight() / 2,
                                itemFrame.getZ(),
                                0.2f, 0.2f, 0.2f, 0.1f, 3));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

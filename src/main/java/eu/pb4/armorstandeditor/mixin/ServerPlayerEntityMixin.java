package eu.pb4.armorstandeditor.mixin;


import com.mojang.authlib.GameProfile;
import eu.pb4.armorstandeditor.config.ConfigManager;
import eu.pb4.armorstandeditor.gui.BaseGui;
import eu.pb4.sgui.api.GuiHelpers;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Shadow public ServerPlayNetworkHandler networkHandler;

    @Unique
    private long ase$tickTimer = 0;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "damage", at = @At("TAIL"))
    private void ase$closeOnDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (GuiHelpers.getCurrentGui((ServerPlayerEntity) (Object) this) instanceof BaseGui baseGui) {
            baseGui.close();
        }
    }

    @Inject(method = "playerTick", at = @At("HEAD"))
    private void ase$showInvisible(CallbackInfo ci) {
        try {
            if (ConfigManager.getConfig().configData.holdingToolSpawnsParticles) {
                ase$tickTimer++;
                if (ase$tickTimer > 10 && this.getMainHandStack().getItem() == ConfigManager.getConfig().armorStandTool) {
                    ase$tickTimer = 0;
                    List<ArmorStandEntity> armorStands = this.world.getEntitiesByClass(ArmorStandEntity.class, new Box(this.getBlockPos().add(10, 10, 10), this.getBlockPos().add(-10, -10, -10)), entity -> true);

                    ParticleEffect particleEffect = new DustParticleEffect(new Vector3f(0.8f, 0.2f, 0.2f), 1f);

                    for (ArmorStandEntity armorStand : armorStands) {
                        this.networkHandler.sendPacket(new ParticleS2CPacket(particleEffect,
                                false,
                                armorStand.getX(),
                                armorStand.getY() + armorStand.getHeight() / 2,
                                armorStand.getZ(),
                                0.2f, 0.2f, 0.2f, 0.1f, 3));
                    }

                    List<ItemFrameEntity> itemFrames = this.world.getEntitiesByClass(ItemFrameEntity.class, new Box(this.getBlockPos().add(10, 10, 10), this.getBlockPos().add(-10, -10, -10)), entity -> true);

                    ParticleEffect particleEffect2 = new DustParticleEffect(new Vector3f(0.2f, 0.8f, 0.2f), 1f);

                    for (ItemFrameEntity itemFrame : itemFrames) {
                        this.networkHandler.sendPacket(new ParticleS2CPacket(particleEffect2,
                                false,
                                itemFrame.getX(),
                                itemFrame.getY() + itemFrame.getHeight() / 2,
                                itemFrame.getZ(),
                                0.2f, 0.2f, 0.2f, 0.1f, 3));
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}

package eu.pb4.armorstandeditor.mixin;


import com.mojang.authlib.GameProfile;
import eu.pb4.armorstandeditor.config.ConfigManager;
import eu.pb4.armorstandeditor.helpers.ArmorStandData;
import eu.pb4.armorstandeditor.EditorActions;
import eu.pb4.armorstandeditor.helpers.SPEInterface;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements SPEInterface {
    @Shadow public ServerPlayNetworkHandler networkHandler;
    private EditorActions armorStandEditorAction = EditorActions.MOVE;
    private float armorStandEditorPower = 1;
    private int armorStandEditorXYZ = 0;
    private ArmorStandData armorStandEditorData = null;


    public EditorActions getArmorStandEditorAction() {
        return this.armorStandEditorAction;
    }

    public void setArmorStandEditorAction(EditorActions action) {
        this.armorStandEditorAction = action;
    }

    public float getArmorStandEditorPower() {
        return this.armorStandEditorPower;
    }

    public void setArmorStandEditorPower(float power) {
        this.armorStandEditorPower = power;
    }

    public int getArmorStandEditorXYZ() {
        return this.armorStandEditorXYZ;
    }

    public void setArmorStandEditorXYZ(int xyz) {
        this.armorStandEditorXYZ = xyz;
    }

    public ArmorStandData getArmorStandEditorData() {
        return this.armorStandEditorData;
    }

    public void setArmorStandEditorData(ArmorStandData data) {
        this.armorStandEditorData = data;
    }

    private long tickTimer = 0;

    @Inject(method = "playerTick", at = @At("HEAD"))
    private void showInvisible(CallbackInfo ci) {
        try {
            if (ConfigManager.getConfig().configData.holdingToolSpawnsParticles) {
                tickTimer++;
                if (tickTimer > 10 && this.getMainHandStack().getItem() == ConfigManager.getConfig().armorStandTool) {
                    tickTimer = 0;
                    List<ArmorStandEntity> armorStands = this.world.getEntitiesByClass(ArmorStandEntity.class, new Box(this.getBlockPos().add(10, 10, 10), this.getBlockPos().add(-10, -10, -10)), null);

                    ParticleEffect particleEffect = new DustParticleEffect(0.8f, 0.2f, 0.2f, 1f);

                    for (ArmorStandEntity armorStand : armorStands) {
                        this.networkHandler.sendPacket(new ParticleS2CPacket(particleEffect,
                                false,
                                armorStand.getX(),
                                armorStand.getY() + armorStand.getHeight() / 2,
                                armorStand.getZ(),
                                0.2f, 0.2f, 0.2f, 0.1f, 3));
                    }

                    List<ItemFrameEntity> itemFrames = this.world.getEntitiesByClass(ItemFrameEntity.class, new Box(this.getBlockPos().add(10, 10, 10), this.getBlockPos().add(-10, -10, -10)), null);

                    ParticleEffect particleEffect2 = new DustParticleEffect(0.2f, 0.8f, 0.2f, 1f);

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





    // Ignore
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

}

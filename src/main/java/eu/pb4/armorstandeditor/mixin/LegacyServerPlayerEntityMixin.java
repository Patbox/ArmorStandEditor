package eu.pb4.armorstandeditor.mixin;


import com.mojang.authlib.GameProfile;
import eu.pb4.armorstandeditor.EditorActions;
import eu.pb4.armorstandeditor.util.ArmorStandData;
import eu.pb4.armorstandeditor.legacy.LegacyPlayerExt;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerEntity.class)
public abstract class LegacyServerPlayerEntityMixin extends PlayerEntity implements LegacyPlayerExt {
    private EditorActions aselegacy$armorStandEditorAction = EditorActions.MOVE;
    private float aselegacy$armorStandEditorPower = 1;
    private int aselegacy$armorStandEditorXYZ = 0;
    private ArmorStandData aselegacy$armorStandEditorData = null;

    public LegacyServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }

    public EditorActions aselegacy$getArmorStandEditorAction() {
        return this.aselegacy$armorStandEditorAction;
    }

    public void aselegacy$setArmorStandEditorAction(EditorActions action) {
        this.aselegacy$armorStandEditorAction = action;
    }

    public float aselegacy$getArmorStandEditorPower() {
        return this.aselegacy$armorStandEditorPower;
    }

    public void aselegacy$setArmorStandEditorPower(float power) {
        this.aselegacy$armorStandEditorPower = power;
    }

    public int aselegacy$getArmorStandEditorXYZ() {
        return this.aselegacy$armorStandEditorXYZ;
    }

    public void aselegacy$setArmorStandEditorXYZ(int xyz) {
        this.aselegacy$armorStandEditorXYZ = xyz;
    }

    public ArmorStandData aselegacy$getArmorStandEditorData() {
        return this.aselegacy$armorStandEditorData;
    }

    public void aselegacy$setArmorStandEditorData(ArmorStandData data) {
        this.aselegacy$armorStandEditorData = data;
    }
}

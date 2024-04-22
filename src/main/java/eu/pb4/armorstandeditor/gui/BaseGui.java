package eu.pb4.armorstandeditor.gui;

import eu.pb4.armorstandeditor.EditorActions;
import eu.pb4.armorstandeditor.util.TextUtils;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.HotbarGui;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public abstract class BaseGui extends HotbarGui {
    @Nullable
    protected EditingContext context;
    private final int currentBlockClickTick;

    public BaseGui(EditingContext context, int selectedSlot) {
        super(context.player);
        this.setSelectedSlot(selectedSlot);
        this.context = context;
        context.currentUi = this;
        this.currentBlockClickTick = context.player.age;
    }

    @Override
    public void onTick() {
        this.checkClosed();
        super.onTick();
    }

    private void checkClosed() {
        if (this.context != null && (this.context.armorStand.isRemoved() || this.context.armorStand.squaredDistanceTo(this.player) > 48 * 48)) {
            this.close();
        }
    }

    @Override
    public boolean onClickBlock(BlockHitResult hitResult) {
        this.checkClosed();
        if (this.player.age - this.currentBlockClickTick >= 5) {
            return super.onClickBlock(hitResult);
        }
        return false;
    }

    @Override
    public void onClickItem() {
        this.checkClosed();
        if (this.player.age - this.currentBlockClickTick >= 5) {
            super.onClickItem();
        }
    }

    @Override
    public boolean onHandSwing() {
        if (this.player.age - this.currentBlockClickTick >= 5) {
            return super.onHandSwing();
        }
        return false;
    }

    protected void rebuildUi() {
        for (int i = 0; i < this.size; i++) {
            this.clearSlot(i);
        }
        this.buildUi();
        this.setSlot(8, new GuiElementBuilder(Items.BARRIER)
                .setName(TextUtils.gui(context.interfaceList.isEmpty() ? "close" : "back"))
                .hideDefaultTooltip()
                .setCallback((x, y, z, c) -> {
                    this.playClickSound();
                    if (this.context == null || this.context.interfaceList.isEmpty()) {
                        this.close();
                    } else {
                        this.switchUi(this.context.interfaceList.remove(0), false);
                    }
                })
        );

        this.setSlot(37, this.player.getEquippedStack(EquipmentSlot.HEAD).copy());
        this.setSlot(38, this.player.getEquippedStack(EquipmentSlot.CHEST).copy());
        this.setSlot(39, this.player.getEquippedStack(EquipmentSlot.LEGS).copy());
        this.setSlot(40, this.player.getEquippedStack(EquipmentSlot.FEET).copy());
    }

    @Override
    public void setSelectedSlot(int value) {
        this.selectedSlot = MathHelper.clamp(value, 0, 8);
    }

    protected void playClickSound() {
        this.playSound(SoundEvents.UI_BUTTON_CLICK, 0.5f, 1f);
    }

    @Override
    public boolean onClick(int index, ClickType type, SlotActionType action, GuiElementInterface element) {
        if (type == ClickType.DROP || type == ClickType.CTRL_DROP) {
            this.close();
            return true;
        }

        return super.onClick(index, type, action, element);
    }

    protected void addSlot(EditorActions actions, GuiElementBuilder builder) {
        if (actions.canUse(this.player)) {
            this.addSlot(builder);
        }
    }

    protected void setSlot(int slot, EditorActions actions, GuiElementBuilder builder) {
        if (actions.canUse(this.player)) {
            this.addSlot(builder);
        }
    }

    protected abstract void buildUi();

    protected abstract SwitchEntry asSwitchableUi();

    protected GuiElementBuilder baseElement(Item item, String name, boolean selected) {
        var builder = new GuiElementBuilder(item)
                .setName(TextUtils.gui(name).formatted(Formatting.WHITE))
                .hideDefaultTooltip();

        if (selected) {
            builder.glow();
        }

        return builder;
    }

    protected GuiElementBuilder baseElement(Item item, MutableText text, boolean selected) {
        var builder = new GuiElementBuilder(item)
                .setName(text.formatted(Formatting.WHITE))
                .hideDefaultTooltip();

        if (selected) {
            builder.glow();
        }

        return builder;
    }

    protected GuiElementBuilder switchElement(Item item, String name, SwitchableUi ui) {
        return new GuiElementBuilder(item)
                .setName(TextUtils.gui("entry." + name).formatted(Formatting.WHITE))
                .hideDefaultTooltip()
                .setCallback(switchCallback(ui));
    }

    protected GuiElementInterface.ClickCallback switchCallback(SwitchableUi ui) {
        return (x, y, z, c) -> {
            this.playSound(SoundEvents.UI_BUTTON_CLICK, 0.5f, 1f);
            this.switchUi(new SwitchEntry(ui, 0), true);
        };
    }

    public void switchUi(SwitchEntry uiOpener, boolean addSelf) {
        var context = this.context;
        if (addSelf) {
            context.interfaceList.add(0, this.asSwitchableUi());
        }
        this.context = null;
        uiOpener.open(context);
    }

    @Override
    public void onClose() {
        if (this.context != null) {
            this.context.close();
        }
    }

    protected void playSound(RegistryEntry<SoundEvent> sound, float volume, float pitch) {
        this.player.networkHandler.sendPacket(new PlaySoundS2CPacket(sound, SoundCategory.MASTER, this.player.getX(), this.player.getY(), this.player.getZ(), volume, pitch, 0));
    }

    @FunctionalInterface
    public interface SwitchableUi {
        void openUi(EditingContext context, int selectedSlot);
    }

    public record SwitchEntry(SwitchableUi ui, int currentSlot) {
        public void open(EditingContext context) {
            ui.openUi(context, currentSlot);
        }
    }


}

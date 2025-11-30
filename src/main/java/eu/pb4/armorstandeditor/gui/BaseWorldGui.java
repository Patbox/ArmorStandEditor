package eu.pb4.armorstandeditor.gui;

import eu.pb4.armorstandeditor.EditorActions;
import eu.pb4.armorstandeditor.util.TextUtils;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.HotbarGui;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.BlockHitResult;

public abstract class BaseWorldGui extends HotbarGui {
    protected EditingContext context;
    private final int currentBlockClickTick;

    public BaseWorldGui(EditingContext context, int selectedSlot) {
        super(context.player);
        this.setSelectedSlot(selectedSlot);
        this.context = context;
        this.currentBlockClickTick = context.player.tickCount;
    }

    @Override
    public void onTick() {
        this.checkClosed();
        super.onTick();
    }

    private void checkClosed() {
        if (this.context.checkClosed()) {
            this.close();
        }
    }

    @Override
    public boolean onClickBlock(BlockHitResult hitResult) {
        this.checkClosed();
        if (this.player.tickCount - this.currentBlockClickTick >= 5) {
            return super.onClickBlock(hitResult);
        }
        return false;
    }

    @Override
    public void onClickItem() {
        this.checkClosed();
        if (this.player.tickCount - this.currentBlockClickTick >= 5) {
            super.onClickItem();
        }
    }

    @Override
    public boolean onHandSwing() {
        if (this.player.tickCount - this.currentBlockClickTick >= 5) {
            return super.onHandSwing();
        }
        return false;
    }

    protected void rebuildUi() {
        for (int i = 0; i < this.size; i++) {
            this.clearSlot(i);
        }
        this.buildUi();
        this.setSlot(8, new GuiElementBuilder()
                .model(Items.BARRIER)
                .setName(TextUtils.gui(context.interfaceList.isEmpty() ? "close" : "back"))
                .setRarity(Rarity.COMMON)
                .hideDefaultTooltip()
                .setCallback((x, y, z, c) -> {
                    this.playClickSound();
                    if (this.context == null || this.context.interfaceList.isEmpty()) {
                        this.close();
                    } else {
                        this.switchUi(this.context.interfaceList.removeFirst(), false);
                    }
                })
        );

        this.setSlot(37, this.player.getItemBySlot(EquipmentSlot.HEAD).copy());
        this.setSlot(38, this.player.getItemBySlot(EquipmentSlot.CHEST).copy());
        this.setSlot(39, this.player.getItemBySlot(EquipmentSlot.LEGS).copy());
        this.setSlot(40, this.player.getItemBySlot(EquipmentSlot.FEET).copy());
    }

    @Override
    public void setSelectedSlot(int value) {
        this.selectedSlot = Mth.clamp(value, 0, 8);
    }

    protected void playClickSound() {
        this.playSound(SoundEvents.UI_BUTTON_CLICK, 0.5f, 1f);
    }

    @Override
    public boolean onClick(int index, ClickType type, net.minecraft.world.inventory.ClickType action, GuiElementInterface element) {
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

    protected abstract EditingContext.SwitchEntry asSwitchableUi();

    protected GuiElementBuilder baseElement(Item item, String name, boolean selected) {
        var builder = new GuiElementBuilder()
                .model(item)
                .setName(TextUtils.gui(name).withStyle(ChatFormatting.WHITE))
                .hideDefaultTooltip();

        if (selected) {
            builder.glow();
        }

        return builder;
    }

    protected GuiElementBuilder baseElement(Item item, MutableComponent text, boolean selected) {
        var builder = new GuiElementBuilder()
                .model(item)
                .setName(text.withStyle(ChatFormatting.WHITE))
                .hideDefaultTooltip();

        if (selected) {
            builder.glow();
        }

        return builder;
    }

    protected GuiElementBuilder switchElement(Item item, String name, EditingContext.SwitchableUi ui) {
        return new GuiElementBuilder()
                .model(item)
                .setName(TextUtils.gui("entry." + name).withStyle(ChatFormatting.WHITE))
                .hideDefaultTooltip()
                .setCallback(switchCallback(ui));
    }

    protected GuiElementInterface.ClickCallback switchCallback(EditingContext.SwitchableUi ui) {
        return (x, y, z, c) -> {
            this.playSound(SoundEvents.UI_BUTTON_CLICK, 0.5f, 1f);
            this.switchUi(new EditingContext.SwitchEntry(ui, 0), true);
        };
    }

    public void switchUi(EditingContext.SwitchEntry uiOpener, boolean addSelf) {
        var context = this.context;
        if (addSelf) {
            context.interfaceList.addFirst(this.asSwitchableUi());
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

    protected void playSound(Holder<SoundEvent> sound, float volume, float pitch) {
        this.player.connection.send(new ClientboundSoundPacket(sound, SoundSource.MASTER, this.player.getX(), this.player.getY(), this.player.getZ(), volume, pitch, 0));
    }
}

package eu.pb4.armorstandeditor.gui;

import eu.pb4.armorstandeditor.EditorActions;
import eu.pb4.armorstandeditor.util.TextUtils;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

public abstract class BaseAnvilGui extends AnvilInputGui {
    protected EditingContext context;
    public BaseAnvilGui(EditingContext context, boolean withPlayerSlots) {
        super(context.player, withPlayerSlots);
        this.context = context;
    }

    @Override
    public void onTick() {
        this.checkClosed();
        super.onTick();
    }

    @Override
    public void onInput(String input) {
        this.checkClosed();
        super.onInput(input);
    }

    private void checkClosed() {
        if (this.context.checkClosed()) {
            this.close();
        }
    }

    protected void rebuildUi() {
        for (int i = 0; i < this.size; i++) {
            this.clearSlot(i);
        }
        this.buildUi();
    }

    protected GuiElementBuilder closeButton() {
        return new GuiElementBuilder()
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
                });
    }

    protected void openPreviousOrClose() {
        if (this.context == null || this.context.interfaceList.isEmpty()) {
            this.close();
        } else {
            this.switchUi(this.context.interfaceList.removeFirst(), false);
        }
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
        var builder = new GuiElementBuilder(item)
                .setName(TextUtils.gui(name).withStyle(ChatFormatting.WHITE))
                .hideDefaultTooltip();

        if (selected) {
            builder.glow();
        }

        return builder;
    }

    protected GuiElementBuilder baseElement(Item item, MutableComponent text, boolean selected) {
        var builder = new GuiElementBuilder(item)
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
        if (uiOpener.currentSlot() == -1) {
            this.close(false);
        }

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

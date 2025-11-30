package eu.pb4.armorstandeditor.gui;

import eu.pb4.armorstandeditor.util.ItemFrameInventory;
import eu.pb4.armorstandeditor.util.TextUtils;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemFrameEditorGui extends SimpleGui {
    private final ItemFrame entity;

    public ItemFrameEditorGui(ServerPlayer player, ItemFrame frameEntity) {
        super(MenuType.GENERIC_9x1, player, false);
        this.entity = frameEntity;

        var inventory = new ItemFrameInventory(entity);
        var ifa = (eu.pb4.armorstandeditor.mixin.ItemFrameEntityAccessor) entity;

        var empty = new GuiElementBuilder(Items.GRAY_STAINED_GLASS_PANE).hideTooltip().build();

        this.setTitle(TextUtils.gui("item_frame_title"));

        this.setSlotRedirect(0, new Slot(inventory, 0, 0, 0));
        this.setSlot(1, empty);

        this.setSlot(2, new GuiElementBuilder(ifa.getFixed() ? Items.GREEN_STAINED_GLASS_PANE : Items.RED_STAINED_GLASS_PANE)
                .setName(TextUtils.gui("name.if-fixed", String.valueOf(ifa.getFixed()))
                        .setStyle(Style.EMPTY.withItalic(false)))
                .setCallback((index, type, action) -> {

                    ifa.setFixed(!ifa.getFixed());

                    ItemStack stack = new ItemStack(ifa.getFixed() ? Items.GREEN_STAINED_GLASS_PANE : Items.RED_STAINED_GLASS_PANE);

                    stack.set(DataComponents.CUSTOM_NAME, TextUtils.gui("name.if-fixed", String.valueOf(ifa.getFixed()))
                            .setStyle(Style.EMPTY.withItalic(false)));
                    playSoundToPlayer(SoundEvents.UI_BUTTON_CLICK.value(), 0.5f, 1f);

                    ((GuiElement) this.getSlot(index)).setItemStack(stack);
                }));

        this.setSlot(3, new GuiElementBuilder(entity.isInvisible() ? Items.GREEN_STAINED_GLASS_PANE : Items.RED_STAINED_GLASS_PANE)
                .setName(TextUtils.gui("name.if-invisible", String.valueOf(entity.isInvisible()))
                        .setStyle(Style.EMPTY.withItalic(false)))
                .setCallback((index, type, action) -> {

                    entity.setInvisible(!entity.isInvisible());
                    ItemStack stack = new ItemStack(entity.isInvisible() ? Items.GREEN_STAINED_GLASS_PANE : Items.RED_STAINED_GLASS_PANE);

                    stack.set(DataComponents.CUSTOM_NAME, TextUtils.gui("name.if-invisible", String.valueOf(entity.isInvisible()))
                            .setStyle(Style.EMPTY.withItalic(false)));
                    playSoundToPlayer(SoundEvents.UI_BUTTON_CLICK.value(), 0.5f, 1f);

                    ((GuiElement) this.getSlot(index)).setItemStack(stack);
                }));

        this.setSlot(4, new GuiElementBuilder(Items.ARROW)
                .setName(TextUtils.gui("name.if-rotate", entity.getRotation())
                        .setStyle(Style.EMPTY.withItalic(false)))
                .setCallback((index, type, action) -> {
                    if (type.isLeft || type.isRight) {
                        int rotation = entity.getRotation() + (type.isLeft ? -1 : 1);

                        if (rotation < 0) {
                            rotation = 8 + rotation;
                        }

                        entity.setRotation(rotation % 8);

                        ItemStack stack = new ItemStack(Items.ARROW);

                        stack.set(DataComponents.CUSTOM_NAME, TextUtils.gui("name.if-rotate", rotation)
                                .setStyle(Style.EMPTY.withItalic(false)));
                        playSoundToPlayer(SoundEvents.UI_BUTTON_CLICK.value(),0.5f, 1f);

                        ((GuiElement) this.getSlot(index)).setItemStack(stack);
                    }
                }));

        for (int x = 5; x < 8; x++) {
            this.setSlot(x, empty);
        }

        this.setSlot(8, new GuiElementBuilder(Items.BARRIER)
                .setName(TextUtils.gui("close").setStyle(Style.EMPTY.withItalic(false)))
                .setCallback(((index, type, action) -> {
                    playSoundToPlayer(SoundEvents.UI_BUTTON_CLICK.value(),0.5f, 1f);
                    this.close();
                }))
        );

        this.open();
    }

    @Override
    public void onTick() {
        if (entity.isRemoved() || entity.position().distanceToSqr(player.position()) > 24 * 24) {
            this.close();
        }
        super.onTick();
    }

    private void playSoundToPlayer(SoundEvent event, float volume, float pitch) {
        player.connection.send(new ClientboundSoundEntityPacket(BuiltInRegistries.SOUND_EVENT.createIntrusiveHolder(event), SoundSource.UI, this.player, volume, pitch, this.player.getRandom().nextLong()));
    }
}

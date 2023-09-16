package eu.pb4.armorstandeditor.gui;

import eu.pb4.armorstandeditor.EditorActions;
import eu.pb4.armorstandeditor.util.TextUtils;
import eu.pb4.armorstandeditor.mixin.ArmorStandEntityAccessor;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PropertyGui extends BaseGui {
    private static final List<Entry> ENTRIES = List.of(
            Entry.of(EditorActions.TOGGLE_VISIBILITY, "invisible", Items.GLASS, ArmorStandEntity::setInvisible, ArmorStandEntity::isInvisible),
            Entry.of(EditorActions.TOGGLE_GRAVITY,"no_gravity", Items.FEATHER, PropertyGui::setNoGravity, ArmorStandEntity::hasNoGravity),
            Entry.ofa(EditorActions.TOGGLE_ARMS, "arms", Items.STICK, ArmorStandEntityAccessor::callSetShowArms, ArmorStandEntity::shouldShowArms),
            Entry.ofa(EditorActions.TOGGLE_BASE, "hide_base", Items.SMOOTH_STONE_SLAB, ArmorStandEntityAccessor::callSetHideBasePlate, ArmorStandEntity::shouldHideBasePlate),
            Entry.ofa(EditorActions.TOGGLE_SIZE, "small", Items.PUFFERFISH, ArmorStandEntityAccessor::callSetSmall, ArmorStandEntity::isSmall)
    );

    private static void setNoGravity(ArmorStandEntity armorStandEntity, Boolean aBoolean) {
        armorStandEntity.setNoGravity(aBoolean);
        armorStandEntity.noClip = armorStandEntity.isMarker() || aBoolean;
    }


    public PropertyGui(EditingContext context, int slot) {
        super(context, slot);
        this.rebuildUi();
        this.open();
    }

    @Override
    protected void buildUi() {
        for (var entry : ENTRIES) {
            this.addSlot(entry.action, entry(entry));
        }
    }

    @Override
    protected SwitchEntry asSwitchableUi() {
        return new SwitchEntry(PropertyGui::new, this.getSelectedSlot());
    }

    protected GuiElementBuilder entry(Entry entry) {
        var value = entry.getter.apply(this.context.armorStand);
        return baseElement(entry.icon, TextUtils.gui(entry.name, value ? TextUtils.ENABLED : TextUtils.DISABLED), value)
                .setCallback((x, y, z, c) -> {
                    entry.setter.accept(this.context.armorStand, !value);
                    this.rebuildUi();
                    this.playSound(SoundEvents.UI_BUTTON_CLICK, 0.5f, 1f);
                });
    }

    private record Entry(EditorActions action, String name, Item icon, BiConsumer<ArmorStandEntity, Boolean> setter, Function<ArmorStandEntity, Boolean> getter) {
        protected static Entry of(EditorActions action, String name, Item icon, BiConsumer<ArmorStandEntity, Boolean> setter, Function<ArmorStandEntity, Boolean> getter) {
            return new Entry(action, "property." + name, icon, setter, getter);
        }

        protected static Entry ofa(EditorActions action, String name, Item icon, BiConsumer<ArmorStandEntityAccessor, Boolean> setter, Function<ArmorStandEntity, Boolean> getter) {
            return new Entry(action, "property." + name, icon, (x, y) -> setter.accept((ArmorStandEntityAccessor) x, y), getter);
        }
    }
}

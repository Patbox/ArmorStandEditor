package eu.pb4.armorstandeditor.gui;

import eu.pb4.armorstandeditor.EditorActions;
import eu.pb4.armorstandeditor.mixin.EntityAccessor;
import eu.pb4.armorstandeditor.util.TextUtils;
import eu.pb4.armorstandeditor.mixin.ArmorStandEntityAccessor;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PropertyGui extends BaseWorldGui {
    private static final List<Entry> ENTRIES = List.of(
            Entry.of(EditorActions.TOGGLE_VISIBILITY, "invisible", Items.GLASS, ArmorStandEntity::setInvisible, ArmorStandEntity::isInvisible),
            Entry.of(EditorActions.TOGGLE_GRAVITY,"no_gravity", Items.FEATHER, PropertyGui::setNoGravity, ArmorStandEntity::hasNoGravity),
            Entry.of(EditorActions.TOGGLE_INVULNERABILITY, "invulnerable", Items.SHIELD, Entity::setInvulnerable, Entity::isInvulnerable),
            Entry.ofa(EditorActions.TOGGLE_ARMS, "arms", Items.STICK, ArmorStandEntityAccessor::callSetShowArms, ArmorStandEntity::shouldShowArms),
            Entry.ofa(EditorActions.TOGGLE_BASE, "hide_base", Items.SMOOTH_STONE_SLAB, ArmorStandEntityAccessor::callSetHideBasePlate, a -> !a.shouldShowBasePlate()),
            Entry.ofe(EditorActions.TOGGLE_VISUAL_FIRE, "visual_fire", Items.BLAZE_POWDER, EntityAccessor::setHasVisualFire, EntityAccessor::isHasVisualFire),
            Entry.ofa(EditorActions.TOGGLE_SIZE, "small", Items.PUFFERFISH, ArmorStandEntityAccessor::callSetSmall, ArmorStandEntity::isSmall),
            Entry.ofMenu(EditorActions.SCALE, "scale", Items.BROWN_MUSHROOM, ScaleGui::new)
    );

    private static void setNoGravity(ArmorStandEntity armorStandEntity, boolean aBoolean) {
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
    protected EditingContext.SwitchEntry asSwitchableUi() {
        return new EditingContext.SwitchEntry(PropertyGui::new, this.getSelectedSlot());
    }

    protected GuiElementBuilder entry(Entry entry) {
        var value = entry.getter.apply(this.context.armorStand);
        return baseElement(entry.icon, TextUtils.gui(entry.name, value ? TextUtils.ENABLED : TextUtils.DISABLED), value)
                .setCallback((x, y, z, c) -> {
                    this.playSound(SoundEvents.UI_BUTTON_CLICK, 0.5f, 1f);
                    if (entry.ui != null) {
                        this.switchUi(new EditingContext.SwitchEntry(entry.ui, 0), true);
                        return;
                    }
                    entry.setter.accept(this.context.armorStand, !value);
                    this.rebuildUi();
                });
    }

    private record Entry(EditorActions action, String name, Item icon, BiConsumer<ArmorStandEntity, Boolean> setter, Function<ArmorStandEntity, Boolean> getter, @Nullable
    EditingContext.SwitchableUi ui) {
        private static Entry ofMenu(EditorActions action, String name, Item icon, EditingContext.SwitchableUi ui) {
            return new Entry(action, "property." + name, icon, (a, b) -> {}, (a) -> false, ui);
        }

        private static Entry of(EditorActions action, String name, Item icon, BiConsumer<ArmorStandEntity, Boolean> setter, Function<ArmorStandEntity, Boolean> getter) {
            return new Entry(action, "property." + name, icon, setter, getter, null);
        }

        private static Entry ofa(EditorActions action, String name, Item icon, BiConsumer<ArmorStandEntityAccessor, Boolean> setter, Function<ArmorStandEntity, Boolean> getter) {
            return new Entry(action, "property." + name, icon, (x, y) -> setter.accept((ArmorStandEntityAccessor) x, y), getter, null);
        }

        private static Entry ofe(EditorActions action, String name, Item icon, BiConsumer<EntityAccessor, Boolean> setter, Function<EntityAccessor, Boolean> getter) {
            return new Entry(action, "property." + name, icon, (x, y) -> setter.accept((EntityAccessor) x, y), (x) -> getter.apply((EntityAccessor) x), null);
        }
    }
}

package eu.pb4.armorstandeditor.util;

import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record ArmorStandInventory(LivingEntity entity) implements Container {

    public static EquipmentSlot getEquipmentSlot(int index) {
        return switch (index) {
            case 1 -> EquipmentSlot.CHEST;
            case 2 -> EquipmentSlot.LEGS;
            case 3 -> EquipmentSlot.FEET;
            case 4 -> EquipmentSlot.MAINHAND;
            case 5 -> EquipmentSlot.OFFHAND;
            default -> EquipmentSlot.HEAD;
        };
    }

    @Override
    public int getContainerSize() {
        return 6;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    public List<ItemStack> getItems() {
        return NonNullList.of(
                ItemStack.EMPTY,
                entity.getItemBySlot(EquipmentSlot.HEAD),
                entity.getItemBySlot(EquipmentSlot.CHEST),
                entity.getItemBySlot(EquipmentSlot.LEGS),
                entity.getItemBySlot(EquipmentSlot.FEET),
                entity.getItemBySlot(EquipmentSlot.MAINHAND),
                entity.getItemBySlot(EquipmentSlot.OFFHAND)
        );
    }

    @Override
    public ItemStack getItem(int slot) {
        if (entity.isRemoved()) {
            return ItemStack.EMPTY;
        }

        return entity.getItemBySlot(getEquipmentSlot(slot));
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (entity.isRemoved()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = ContainerHelper.removeItem(this.getItems(), slot, amount);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (entity.isRemoved()) {
            return ItemStack.EMPTY;
        }

        return ContainerHelper.takeItem(this.getItems(), slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (entity.isRemoved()) {
            return;
        }
        this.entity.setItemSlot(getEquipmentSlot(slot), stack);
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {

    }
}

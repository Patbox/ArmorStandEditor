package eu.pb4.armorstandeditor.util;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public record ArmorStandInventory(LivingEntity entity) implements Inventory {

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
    public int size() {
        return 6;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public int getMaxCount(ItemStack stack) {
        return 1;
    }

    public List<ItemStack> getItems() {
        return DefaultedList.copyOf(
                ItemStack.EMPTY,
                entity.getEquippedStack(EquipmentSlot.HEAD),
                entity.getEquippedStack(EquipmentSlot.CHEST),
                entity.getEquippedStack(EquipmentSlot.LEGS),
                entity.getEquippedStack(EquipmentSlot.FEET),
                entity.getEquippedStack(EquipmentSlot.MAINHAND),
                entity.getEquippedStack(EquipmentSlot.OFFHAND)
        );
    }

    @Override
    public ItemStack getStack(int slot) {
        if (entity.isRemoved()) {
            return ItemStack.EMPTY;
        }

        return entity.getEquippedStack(getEquipmentSlot(slot));
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (entity.isRemoved()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = Inventories.splitStack(this.getItems(), slot, amount);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (entity.isRemoved()) {
            return ItemStack.EMPTY;
        }

        return Inventories.removeStack(this.getItems(), slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (entity.isRemoved()) {
            return;
        }
        this.entity.equipStack(getEquipmentSlot(slot), stack);
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {

    }
}

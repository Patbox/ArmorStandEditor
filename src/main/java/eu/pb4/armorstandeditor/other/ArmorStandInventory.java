package eu.pb4.armorstandeditor.other;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class ArmorStandInventory implements Inventory {
    private final LivingEntity entity;

    public ArmorStandInventory(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public int size() {
        return 6;
    }

    @Override
    public boolean isEmpty() {
        return false;
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

    public EquipmentSlot getEquipmentSlot(int index) {
        EquipmentSlot slot;
        switch (index) {
            case 0:
                slot = EquipmentSlot.HEAD;
                break;
            case 1:
                slot = EquipmentSlot.CHEST;
                break;
            case 2:
                slot = EquipmentSlot.LEGS;
                break;
            case 3:
                slot = EquipmentSlot.FEET;
                break;
            case 4:
                slot = EquipmentSlot.MAINHAND;
                break;
            case 5:
                slot = EquipmentSlot.OFFHAND;
                break;
            default:
                slot = EquipmentSlot.HEAD;
        }

        return slot;
    }

    @Override
    public ItemStack getStack(int slot) {
        return entity.getEquippedStack(this.getEquipmentSlot(slot));
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(this.getItems(), slot, amount);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.getItems(), slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.entity.equipStack(this.getEquipmentSlot(slot), stack);
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

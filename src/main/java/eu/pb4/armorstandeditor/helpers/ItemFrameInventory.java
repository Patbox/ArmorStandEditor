package eu.pb4.armorstandeditor.helpers;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class ItemFrameInventory implements Inventory {
    private final ItemFrameEntity entity;

    public ItemFrameInventory(ItemFrameEntity entity) {
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
        return DefaultedList.copyOf(ItemStack.EMPTY, this.entity.getHeldItemStack());
    }

    @Override
    public ItemStack getStack(int slot) {
        return entity.getHeldItemStack();
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
        this.entity.setHeldItemStack(stack);
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
        this.entity.setHeldItemStack(ItemStack.EMPTY);
    }
}

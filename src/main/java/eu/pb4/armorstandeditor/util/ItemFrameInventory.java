package eu.pb4.armorstandeditor.util;

import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemFrameInventory implements Container {
    private final ItemFrame entity;

    public ItemFrameInventory(ItemFrame entity) {
        this.entity = entity;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public List<ItemStack> getItems() {
        return NonNullList.of(ItemStack.EMPTY, this.entity.getItem());
    }

    @Override
    public ItemStack getItem(int slot) {
        if (this.entity.isRemoved()) {
            return ItemStack.EMPTY;
        }
        return entity.getItem();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (this.entity.isRemoved()) {
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
        if (this.entity.isRemoved()) {
            return ItemStack.EMPTY;
        }
        return ContainerHelper.takeItem(this.getItems(), slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (this.entity.isRemoved()) {
            return;
        }
        this.entity.setItem(stack);
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
        if (this.entity.isRemoved()) {
            return;
        }
        this.entity.setItem(ItemStack.EMPTY);
    }
}

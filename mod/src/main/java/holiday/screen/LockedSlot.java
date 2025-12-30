package holiday.screen;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

/**
 * A slot used for rendering only. It's better to be safe than sorry.
 */
public class LockedSlot extends Slot {
    public LockedSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public void onQuickTransfer(ItemStack newItem, ItemStack original) {
        return;
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        return;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack takeStack(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canTakeItems(PlayerEntity player) {
        return false;
    }

    @Override
    public Optional<ItemStack> tryTakeStackRange(int min, int max, PlayerEntity player) {
        return Optional.empty();
    }

    @Override
    public ItemStack takeStackRange(int min, int max, PlayerEntity player) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertStack(ItemStack stack) {
        return stack;
    }

    @Override
    public ItemStack insertStack(ItemStack stack, int count) {
        return this.insertStack(stack);
    }

    @Override
    public boolean canTakePartial(PlayerEntity player) {
        return false;
    }
}

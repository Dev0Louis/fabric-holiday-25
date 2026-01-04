package holiday.block.entity;

import holiday.mixin.HopperBlockEntityAccessor;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

public class GoldenHopperBlockEntity extends HopperBlockEntity {

    private int transferCooldown;

    public GoldenHopperBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("block.holiday-server-mod.golden_hopper");
    }

    @Override
    public BlockEntityType<?> getType() {
        return HolidayServerBlockEntityTypes.GOLDEN_HOPPER;
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, GoldenHopperBlockEntity hopper) {
        hopper.transferCooldown--;
        if (hopper.noCooldown()) {
            hopper.setTransferCooldown(0);
            insertAndExtract(world, pos, state, hopper, () -> extractFullStack(world, pos, hopper));
        }
    }

    private static boolean insertFullStack(World world, BlockPos pos, GoldenHopperBlockEntity hopper) {
        Direction outDir = hopper.getCachedState().get(HopperBlock.FACING);
        BlockPos targetPos = pos.offset(outDir);

        Storage<@NotNull ItemVariant> targetStorage = ItemStorage.SIDED.find(world, targetPos, outDir.getOpposite());
        boolean moved = false;

        for (int i = 0; i < hopper.size(); i++) {
            ItemStack stack = hopper.getStack(i);
            if (stack.isEmpty()) continue;

            ItemVariant variant = ItemVariant.of(stack);
            long amount = stack.getCount();

            if (targetStorage != null) {
                try (Transaction tx = Transaction.openOuter()) {
                    long inserted = targetStorage.insert(variant, amount, tx);
                    if (inserted > 0) {
                        stack.decrement((int) inserted);
                        moved = true;
                        tx.commit();
                    }
                }
            } else {
                Inventory inv = getVanillaInventory(world, targetPos, outDir.getOpposite());
                if (inv != null) {
                    ItemStack toMove = hopper.removeStack(i, (int) amount);
                    ItemStack rem = transfer(hopper, inv, toMove, outDir.getOpposite());
                    if (!rem.isEmpty()) hopper.setStack(i, rem);
                    moved |= rem.isEmpty();
                }
            }
        }
        return moved;
    }

    private static boolean extractFullStack(World world, BlockPos pos, GoldenHopperBlockEntity hopper) {
        BlockPos abovePos = pos.up();

        boolean moved = false;
        Storage<@NotNull ItemVariant> sourceStorage = ItemStorage.SIDED.find(world, abovePos, Direction.DOWN);

        if (sourceStorage != null) {
            try (Transaction tx = Transaction.openOuter()) {
                for (StorageView<@NotNull ItemVariant> view : sourceStorage) {
                    if (view.isResourceBlank()) continue;

                    long available = view.getAmount();
                    ItemVariant variant = view.getResource();

                    long inserted = insertIntoHopperFAPI(hopper, variant, available);
                    if (inserted > 0) {
                        view.extract(variant, inserted, tx);
                        moved = true;
                    }
                }
                tx.commit();
            }
        }

        Inventory inv = getVanillaInventory(world, abovePos, Direction.DOWN);
        if (inv != null) {
            for (int slot : HopperBlockEntityAccessor.getAvailableSlots(inv, Direction.DOWN)) {
                ItemStack stack = inv.getStack(slot);
                if (stack.isEmpty()) continue;

                ItemStack pulled = inv.removeStack(slot, stack.getCount());
                ItemStack rem = transfer(inv, hopper, pulled, null);
                if (!rem.isEmpty()) inv.setStack(slot, rem);
                else {
                    inv.markDirty();
                    return true;
                }
            }
        } else {
            for (ItemEntity itemEntity : getInputItemEntities(world, hopper)) {
                ItemStack stack = itemEntity.getStack();
                long inserted = insertIntoHopperFAPI(hopper, ItemVariant.of(stack), stack.getCount());
                stack.decrement((int) inserted);
                if (stack.isEmpty()) itemEntity.discard();
                return inserted > 0;
            }
        }

        return moved;
    }

    private static long insertIntoHopperFAPI(GoldenHopperBlockEntity hopper, ItemVariant variant, long amount) {
        long remaining = amount;
        for (int i = 0; i < hopper.size(); i++) {
            ItemStack slot = hopper.getStack(i);
            if (slot.isEmpty() || ItemVariant.of(slot).equals(variant)) {
                long maxInsert = slot.isEmpty() ? variant.toStack().getMaxCount() : slot.getMaxCount() - slot.getCount();
                long toInsert = Math.min(remaining, maxInsert);
                if (toInsert <= 0) continue;

                if (slot.isEmpty()) hopper.setStack(i, variant.toStack((int) toInsert));
                else slot.increment((int) toInsert);

                remaining -= toInsert;
                if (remaining <= 0) return amount;
            }
        }
        return amount - remaining;
    }

    public static void onEntityCollided(World world, BlockPos pos, BlockState state, Entity entity, GoldenHopperBlockEntity hopper) {
        if (entity instanceof ItemEntity itemEntity && !itemEntity.getStack().isEmpty()) {
            Box hopperBox = hopper.getInputAreaShape().offset(hopper.getHopperX() - 0.5, hopper.getHopperY() - 0.5, hopper.getHopperZ() - 0.5);
            if (entity.getBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ()).intersects(hopperBox)) {
                insertAndExtract(world, pos, state, hopper, () -> {
                    ItemStack stack = itemEntity.getStack();
                    long inserted = insertIntoHopperFAPI(hopper, ItemVariant.of(stack), stack.getCount());
                    stack.decrement((int) inserted);
                    if (stack.isEmpty()) itemEntity.discard();
                    return inserted > 0;
                });
            }
        }
    }

    private static Inventory getVanillaInventory(World world, BlockPos pos, Direction side) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof InventoryProvider provider) {
            return provider.getInventory(state, world, pos);
        }
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof Inventory inv) {
            return inv;
        }
        return null;
    }

    private static void insertAndExtract(World world, BlockPos pos, BlockState state,
                                         GoldenHopperBlockEntity hopper, BooleanSupplier extractSupplier) {
        if (world.isClient()) return;

        if (hopper.noCooldown() && state.get(HopperBlock.ENABLED)) {
            boolean moved = insertFullStack(world, pos, hopper);
            if (!hopper.isFull()) moved |= extractSupplier.getAsBoolean();
            if (moved) {
                hopper.setTransferCooldown(8);
                markDirty(world, pos, state);
            }
        }

    }

    private void setTransferCooldown(int cooldown) { this.transferCooldown = cooldown; }
    private boolean noCooldown() { return this.transferCooldown <= 0; }

    private boolean isFull() {
        for (ItemStack itemStack : this.getHeldStacks()) {
            if (itemStack.isEmpty() || itemStack.getCount() != itemStack.getMaxCount()) {
                return false;
            }
        }

        return true;
    }
}

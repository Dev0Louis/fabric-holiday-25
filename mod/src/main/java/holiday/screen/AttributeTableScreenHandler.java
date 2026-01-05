package holiday.screen;

import holiday.CommonEntrypoint;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AttributeTableScreenHandler extends ScreenHandler {

    private RegistryEntry<EntityAttribute> selectedAttribute = EntityAttributes.ARMOR;
    private Mode mode = Mode.MERGE;
    private final ScreenHandlerContext context;
    private boolean updating = false;
    private final Inventory inventory = new SimpleInventory(3);

    public AttributeTableScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(HolidayServerScreenHandlers.ATTRIBUTE_TABLE, syncId);

        this.addSlot(new SingleItemSlot(inventory, 0, 15, 15));
        this.addSlot(new SingleItemSlot(inventory, 1, 15, 52));

        this.context = context;

        this.addSlot(new Slot(inventory, 2, 145, 39) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                super.onTakeItem(player, stack);
                finalizeCraft();
            }
        });

        // Player inventory
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

        for (int i = 0; i < 9; ++i)
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));

        ((SimpleInventory) inventory).addListener(this::onContentChanged);
    }

    public AttributeTableScreenHandler(int syncId, PlayerInventory inv) {
        this(syncId, inv, ScreenHandlerContext.EMPTY);
    }

    @Override
    public void onContentChanged(Inventory inv) {
        if (inv != inventory || updating) return;
        updating = true;

        ItemStack top = inventory.getStack(0);
        ItemStack bottom = inventory.getStack(1);

        if (top.isEmpty() || bottom.isEmpty() && this.mode == Mode.MERGE) {
            inventory.setStack(2, ItemStack.EMPTY);
            updating = false;
            return;
        }

        ItemStack output = computeOutput(top, bottom);
        inventory.setStack(2, output);

        updating = false;
    }

    private ItemStack computeOutput(ItemStack top, ItemStack bottom) {
        ItemStack output = top.copy();

        switch (mode) {
            case MERGE -> {
                if (bottom.isEmpty()) {
                    return ItemStack.EMPTY;
                }

                Map<ModifierKey, Double> merged = new HashMap<>();
                mergeModifiers(top, merged);
                mergeModifiers(bottom, merged);
                applyMergedModifiers(output, merged);
            }

            case REMOVE -> {
                boolean changed = removeSelectedAttribute(output, selectedAttribute);

                if (!changed) {
                    return ItemStack.EMPTY;
                }
            }
        }

        return output;
    }


    private void mergeModifiers(ItemStack stack, Map<ModifierKey, Double> merged) {
        AttributeModifiersComponent comp = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (comp == null) return;

        comp.modifiers().forEach(entry -> {
            EntityAttributeModifier m = entry.modifier();
            ModifierKey key = new ModifierKey(entry.attribute(), m.operation());
            merged.merge(key, m.value(), Double::sum);
        });
    }

    private void applyMergedModifiers(ItemStack stack, Map<ModifierKey, Double> merged) {
        if (merged.isEmpty()) {
            stack.remove(DataComponentTypes.ATTRIBUTE_MODIFIERS);
            return;
        }

        AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();
        merged.forEach((key, value) -> {
            double finalValue = key.attribute() == EntityAttributes.ATTACK_SPEED ? Math.max(value, 0.01) : value;

            builder.add(
                key.attribute(),
                new EntityAttributeModifier(
                    CommonEntrypoint.identifier("combined_" + UUID.randomUUID().toString().substring(0,6)),
                    finalValue,
                    key.operation()
                ),
                AttributeModifierSlot.ANY
            );
        });

        stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
    }

    private boolean removeSelectedAttribute(ItemStack stack, RegistryEntry<EntityAttribute> attribute) {
        if (attribute == null) return false;

        AttributeModifiersComponent comp = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (comp == null) return false;

        boolean removedAny = false;
        AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();

        for (var entry : comp.modifiers()) {
            if (entry.attribute().value().equals(attribute.value())) {
                removedAny = true;
                continue;
            }
            builder.add(entry.attribute(), entry.modifier(), entry.slot());
        }

        if (!removedAny) return false;

        AttributeModifiersComponent result = builder.build();
        if (result.modifiers().isEmpty()) {
            stack.remove(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        } else {
            stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, result);
        }

        return true;
    }


    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack result;
        Slot slot = this.slots.get(slotIndex);

        if (!slot.hasStack()) return ItemStack.EMPTY;

        ItemStack stack = slot.getStack();
        result = stack.copy();

        if (slotIndex == 2) {
            if (!this.insertItem(stack, 3, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickTransfer(stack, result);
            finalizeCraft();
        }
        else if (slotIndex < 2) {
            if (!this.insertItem(stack, 3, this.slots.size(), false)) {
                return ItemStack.EMPTY;
            }
        }
        else {
            if (!this.insertItem(stack, 0, 2, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        return result;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);

        boolean removed = player.isRemoved() &&
            player.getRemovalReason() != Entity.RemovalReason.CHANGED_DIMENSION;
        boolean disconnected = player instanceof ServerPlayerEntity sp && sp.isDisconnected();

        for (int i = 0; i < 2; i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;

            if (removed || disconnected) {
                player.dropItem(stack, false);
            } else if (player instanceof ServerPlayerEntity) {
                player.getInventory().offerOrDrop(stack);
            }

            inventory.setStack(i, ItemStack.EMPTY);
        }

        inventory.setStack(2, ItemStack.EMPTY);
    }

    private void finalizeCraft() {
        inventory.setStack(0, ItemStack.EMPTY);
        inventory.setStack(1, ItemStack.EMPTY);

        if (this.context.get((world, pos) -> world == null || world.isClient(), true)) return;

        this.context.run((world, pos) -> world.playSound(
            null,
            pos,
            SoundEvents.BLOCK_SMITHING_TABLE_USE,
            SoundCategory.BLOCKS,
            1.0f,
            1.0f
        ));
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        this.onContentChanged(this.inventory);
    }

    public RegistryEntry<EntityAttribute> getSelectedAttribute() {
        return selectedAttribute;
    }

    public void setSelectedAttribute(RegistryEntry<EntityAttribute> attribute) {
        this.selectedAttribute = attribute;
        this.onContentChanged(this.inventory);
    }


    private record ModifierKey(
        RegistryEntry<EntityAttribute> attribute,
        EntityAttributeModifier.Operation operation
    ) {}

    public enum Mode {
        MERGE,
        REMOVE
    }

    private class SingleItemSlot extends Slot {
        public SingleItemSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public int getMaxItemCount() {
            return 1;
        }
    }
}

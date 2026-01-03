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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AttributeTableScreenHandler extends ScreenHandler {

    private final ScreenHandlerContext context;
    private boolean updating = false;

    private final Inventory inventory = new SimpleInventory(3);

    public AttributeTableScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(HolidayServerScreenHandlers.ATTRIBUTE_TABLE, syncId);

        this.addSlot(new Slot(inventory, 0, 15, 15));
        this.addSlot(new Slot(inventory, 1, 15, 52));

        this.context = context;

        this.addSlot(new Slot(inventory, 2, 145, 39) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                finalizeCraft();
                super.onTakeItem(player, stack);
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

        ((SimpleInventory) inventory).addListener(this::onContentChanged);
    }

    public AttributeTableScreenHandler(int syncId, PlayerInventory inv) {
        this(syncId, inv, ScreenHandlerContext.EMPTY);
    }

    @Override
    public void onContentChanged(Inventory inv) {
        if (inv != inventory || updating) return;

        updating = true;

        ItemStack left = inventory.getStack(0);
        ItemStack right = inventory.getStack(1);

        if (left.isEmpty() || right.isEmpty()) {
            if (!inventory.getStack(2).isEmpty()) {
                inventory.setStack(2, ItemStack.EMPTY);
            }
            updating = false;
            return;
        }

        ItemStack output = left.copy();

        Map<ModifierKey, Double> merged = new HashMap<>();
        mergeModifiers(left, merged);
        mergeModifiers(right, merged);

        if (!merged.isEmpty()) {
            AttributeModifiersComponent.Builder builder =
                AttributeModifiersComponent.builder();

            merged.forEach((key, value) -> {
                double finalValue = value;

                if (key.attribute() == EntityAttributes.ATTACK_SPEED) {
                    finalValue = Math.max(finalValue, 0.01);
                }

                UUID id = UUID.randomUUID();

                builder.add(
                    key.attribute(),
                    new EntityAttributeModifier(
                        CommonEntrypoint.identifier("combined_" + id.toString().substring(0, 6)),
                        finalValue,
                        key.operation()
                    ),
                    AttributeModifierSlot.ANY
                );
            });

            output.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
        }

        if (!ItemStack.areEqual(output, inventory.getStack(2))) {
            inventory.setStack(2, output);
        }

        updating = false;
    }

    private void mergeModifiers(ItemStack stack, Map<ModifierKey, Double> merged) {
        AttributeModifiersComponent comp = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (comp == null) return;

        comp.modifiers().forEach(entry -> {
            EntityAttributeModifier m = entry.modifier();

            ModifierKey key = new ModifierKey(
                entry.attribute(),
                m.operation()
            );

            merged.merge(key, m.value(), Double::sum);
        });
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
            finalizeCraft();
            slot.onQuickTransfer(stack, result);
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



    private record ModifierKey(
        RegistryEntry<EntityAttribute> attribute,
        EntityAttributeModifier.Operation operation
    ) {}
}

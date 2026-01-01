package holiday.screen;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
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
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class AttributeTableScreenHandler extends ScreenHandler {

    private final Inventory inventory = new SimpleInventory(3);

    public AttributeTableScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(HolidayServerScreenHandlers.ATTRIBUTE_TABLE, syncId);


        this.addSlot(new Slot(inventory, 0, 15, 15));

        this.addSlot(new Slot(inventory, 1, 15, 52));

        this.addSlot(new Slot(inventory, 2, 145, 39) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                inventory.setStack(0, ItemStack.EMPTY);
                inventory.setStack(1, ItemStack.EMPTY);
                super.onTakeItem(player, stack);
                context.run((world, pos) -> world.playSound(
                    null, pos, SoundEvents.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 1.0f, 1.0f
                ));
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
        this(syncId, inv, null);
    }

    @Override
    public void onContentChanged(Inventory inv) {
        if (inv != inventory) return;

        ItemStack left = inventory.getStack(0);
        ItemStack right = inventory.getStack(1);

        if (left.isEmpty() || right.isEmpty()) {
            if (!inventory.getStack(2).isEmpty()) inventory.setStack(2, ItemStack.EMPTY);
            return;
        }

        ItemStack output = left.copy();
        output.setCount(1);

        Multimap<RegistryEntry<EntityAttribute>, @NotNull AttributeMod> modifiers = HashMultimap.create();

        copyModifiers(left, modifiers);
        copyModifiers(right, modifiers);

        if (!modifiers.isEmpty()) {
            var builder = AttributeModifiersComponent.builder();
            modifiers.forEach((attr, mod) ->
                builder.add(attr, mod.m, mod.slot)
            );
            output.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
        }

        ItemStack current = inventory.getStack(2);
        if (!ItemStack.areEqual(output, current)) {
            inventory.setStack(2, output);
        }
    }

    private void copyModifiers(ItemStack stack, Multimap<RegistryEntry<EntityAttribute>, AttributeMod> modifiers) {
        AttributeModifiersComponent comp = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (comp == null) return;

        comp.modifiers().forEach(entry -> {
            EntityAttributeModifier m = entry.modifier();
            UUID stableId = UUID.nameUUIDFromBytes(
                (m.id().toString() + "_" + entry.slot().name()).getBytes(StandardCharsets.UTF_8)
            );

            modifiers.put(
                entry.attribute(),
                new AttributeMod(
                    new EntityAttributeModifier(
                        Identifier.of(m.id().getNamespace(), m.id().getPath() + "_combined_" + stableId.toString().substring(0, 4)),
                        m.value(),
                        m.operation()
                    ),
                    entry.slot()
                )
            );
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

            inventory.setStack(0, ItemStack.EMPTY);
            inventory.setStack(1, ItemStack.EMPTY);

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
        if (!this.inventory.isEmpty()) {
            boolean bl = player.isRemoved() && player.getRemovalReason() != Entity.RemovalReason.CHANGED_DIMENSION;
            boolean bl2 = player instanceof ServerPlayerEntity serverPlayerEntity && serverPlayerEntity.isDisconnected();
            this.inventory.forEach(stack -> {
                if (bl || bl2) {
                    player.dropItem(stack, false);
                } else if (player instanceof ServerPlayerEntity) {
                    player.getInventory().offerOrDrop(stack);
                }
            });

        }
    }

        private record AttributeMod(EntityAttributeModifier m, AttributeModifierSlot slot) {}
}

package holiday.mixin;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HopperBlockEntity.class)
public interface HopperBlockEntityAccessor {
    @Invoker("getAvailableSlots")
    static int[] getAvailableSlots(Inventory inventory, Direction side) {
        throw new AssertionError("Mixin failed to apply");
    }
}

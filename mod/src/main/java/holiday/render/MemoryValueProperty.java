package holiday.render;

import com.mojang.serialization.MapCodec;

import holiday.component.HolidayServerDataComponentTypes;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;

public final class MemoryValueProperty implements NumericProperty {
    public static final MemoryValueProperty INSTANCE = new MemoryValueProperty();

    public static final MapCodec<MemoryValueProperty> CODEC = MapCodec.unit(INSTANCE);

    private MemoryValueProperty() {
    }

    @Override
    public float getValue(ItemStack stack, ClientWorld world, HeldItemContext context, int seed) {
        return stack.getOrDefault(HolidayServerDataComponentTypes.MEMORY_VALUE, 0);
    }

    @Override
    public MapCodec<? extends MemoryValueProperty> getCodec() {
        return CODEC;
    }
}

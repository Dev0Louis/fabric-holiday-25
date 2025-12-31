package holiday.render;

import com.mojang.serialization.MapCodec;

import holiday.CommonEntrypoint;
import net.minecraft.client.render.item.property.numeric.NumericProperties;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.util.Identifier;

public final class HolidayServerNumericProperties {
    private HolidayServerNumericProperties() {
    }

    public static void register() {
        register("memory_value", MemoryValueProperty.CODEC);
    }

    public static void register(String path, MapCodec<? extends NumericProperty> codec) {
        Identifier id = CommonEntrypoint.identifier(path);
        NumericProperties.ID_MAPPER.put(id, codec);
    }
}

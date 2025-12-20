package holiday.entity;

import holiday.CommonEntrypoint;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public final class HolidayServerPaintingVariants {
    public static final RegistryKey<PaintingVariant> OUTDATED = of("outdated");

    private HolidayServerPaintingVariants() {
    }

    public static RegistryKey<PaintingVariant> of(String path) {
        return RegistryKey.of(RegistryKeys.PAINTING_VARIANT, CommonEntrypoint.identifier(path));
    }
}

package holiday.block;

import holiday.CommonEntrypoint;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public final class HolidayServerBannerPatterns {
    public static final RegistryKey<BannerPattern> FABRIC = of("fabric");
    public static final RegistryKey<BannerPattern> TATER = of("tater");

    private HolidayServerBannerPatterns() {
    }

    public static RegistryKey<BannerPattern> of(String path) {
        return RegistryKey.of(RegistryKeys.BANNER_PATTERN, CommonEntrypoint.identifier(path));
    }
}

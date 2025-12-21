package holiday.tag;

import holiday.CommonEntrypoint;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public final class HolidayServerBannerPatternTags {
    public static final TagKey<BannerPattern> FABRIC_PATTERN_ITEM = of("pattern_item/fabric");
    public static final TagKey<BannerPattern> TATER_PATTERN_ITEM = of("pattern_item/tater");

    private HolidayServerBannerPatternTags() {
    }

    public static TagKey<BannerPattern> of(String path) {
        return TagKey.of(RegistryKeys.BANNER_PATTERN, CommonEntrypoint.identifier(path));
    }
}

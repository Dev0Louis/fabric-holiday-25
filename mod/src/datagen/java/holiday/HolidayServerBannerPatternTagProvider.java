package holiday;

import java.util.concurrent.CompletableFuture;

import holiday.block.HolidayServerBannerPatterns;
import holiday.tag.HolidayServerBannerPatternTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

public class HolidayServerBannerPatternTagProvider extends FabricTagProvider<BannerPattern> {
    public HolidayServerBannerPatternTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
        super(output, RegistryKeys.BANNER_PATTERN, registries);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries) {
        this.builder(HolidayServerBannerPatternTags.FABRIC_PATTERN_ITEM)
                .add(HolidayServerBannerPatterns.FABRIC);

        this.builder(HolidayServerBannerPatternTags.TATER_PATTERN_ITEM)
                .add(HolidayServerBannerPatterns.TATER);
    }
}

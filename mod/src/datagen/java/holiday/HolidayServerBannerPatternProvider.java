package holiday;

import java.util.concurrent.CompletableFuture;

import holiday.block.HolidayServerBannerPatterns;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BannerPatterns;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

public class HolidayServerBannerPatternProvider extends FabricDynamicRegistryProvider {
    public HolidayServerBannerPatternProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
        super(output, registries);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, FabricDynamicRegistryProvider.Entries entries) {
        entries.addAll(registries.getOrThrow(RegistryKeys.BANNER_PATTERN));
    }

    @Override
    public String getName() {
        return "Banner Patterns";
    }

    public static void register(Registerable<BannerPattern> registry) {
        register(registry, HolidayServerBannerPatterns.FABRIC);
        register(registry, HolidayServerBannerPatterns.TATER);
    }

    /**
     * @see {@link BannerPatterns#register} for the standard method; the translation keys use a different format here
     */
    private static void register(Registerable<BannerPattern> registry, RegistryKey<BannerPattern> key) {
        registry.register(key, new BannerPattern(key.getValue(), "block.holiday-server-mod.banner." + key.getValue().getPath()));
    }
}

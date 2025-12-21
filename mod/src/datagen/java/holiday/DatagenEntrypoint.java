package holiday;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class DatagenEntrypoint implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
        FabricDataGenerator.Pack pack = dataGenerator.createPack();

        pack.addProvider(HolidayServerBannerPatternProvider::new);
        pack.addProvider(HolidayServerBannerPatternTagProvider::new);
        pack.addProvider(HolidayServerBlockLootTableProvider::new);
        pack.addProvider(HolidayServerBlockTagProvider::new);
        pack.addProvider(HolidayServerPaintingVariantProvider::new);
        pack.addProvider(HolidayServerPaintingVariantTagProvider::new);
        pack.addProvider(HolidayServerRecipeProvider::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.BANNER_PATTERN, HolidayServerBannerPatternProvider::register);
        registryBuilder.addRegistry(RegistryKeys.PAINTING_VARIANT, HolidayServerPaintingVariantProvider::register);
    }
}

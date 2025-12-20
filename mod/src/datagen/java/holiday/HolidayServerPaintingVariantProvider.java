package holiday;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import holiday.entity.HolidayServerPaintingVariants;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class HolidayServerPaintingVariantProvider extends FabricDynamicRegistryProvider {
    public HolidayServerPaintingVariantProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
        super(output, registries);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, FabricDynamicRegistryProvider.Entries entries) {
        entries.addAll(registries.getOrThrow(RegistryKeys.PAINTING_VARIANT));
    }

    @Override
    public String getName() {
        return "Painting Variants";
    }

    public static void register(Registerable<PaintingVariant> registry) {
        registry.register(HolidayServerPaintingVariants.OUTDATED, new PaintingVariant(
            2,
            1,
            HolidayServerPaintingVariants.OUTDATED.getValue(),
            Optional.of(Text.translatable(HolidayServerPaintingVariants.OUTDATED.getValue().toTranslationKey("painting", "title")).formatted(Formatting.YELLOW)),
            Optional.of(Text.translatable(HolidayServerPaintingVariants.OUTDATED.getValue().toTranslationKey("painting", "author")).formatted(Formatting.GRAY))
        ));
    }
}

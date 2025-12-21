package holiday;

import java.util.concurrent.CompletableFuture;

import holiday.entity.HolidayServerPaintingVariants;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.PaintingVariantTags;

public class HolidayServerPaintingVariantTagProvider extends FabricTagProvider<PaintingVariant> {
    public HolidayServerPaintingVariantTagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> registries) {
        super(output, RegistryKeys.PAINTING_VARIANT, registries);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries) {
        this.builder(PaintingVariantTags.PLACEABLE)
                .add(HolidayServerPaintingVariants.OUTDATED);
    }
}

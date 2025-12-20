package holiday;

import java.util.concurrent.CompletableFuture;

import holiday.block.HolidayServerBlocks;
import holiday.tag.HolidayServerBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

public class HolidayServerBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public HolidayServerBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
        super(output, registries);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries) {
        this.valueLookupBuilder(HolidayServerBlockTags.CRYING_IMPACTORS)
                .add(Blocks.CRYING_OBSIDIAN);

        this.valueLookupBuilder(BlockTags.AXE_MINEABLE)
                .add(HolidayServerBlocks.TINY_POTATO);

        this.valueLookupBuilder(BlockTags.SHOVEL_MINEABLE)
                .add(HolidayServerBlocks.REDSTONE_SAND);
    }
}

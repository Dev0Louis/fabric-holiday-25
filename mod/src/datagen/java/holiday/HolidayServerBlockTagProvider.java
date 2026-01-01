package holiday;

import holiday.block.HolidayServerBlocks;
import holiday.tag.HolidayServerBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class HolidayServerBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public HolidayServerBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
        super(output, registries);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries) {
        this.valueLookupBuilder(HolidayServerBlockTags.CRYING_IMPACTORS)
                .add(Blocks.CRYING_OBSIDIAN);

        this.valueLookupBuilder(HolidayServerBlockTags.STONE_MEALABLE)
            .add(Blocks.POINTED_DRIPSTONE);

        this.valueLookupBuilder(BlockTags.AXE_MINEABLE)
                .add(HolidayServerBlocks.TINY_POTATO)
                .add(HolidayServerBlocks.ATTRIBUTE_TABLE);

        this.valueLookupBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(HolidayServerBlocks.STORAGE_TERMINAL);

        this.valueLookupBuilder(BlockTags.SHOVEL_MINEABLE)
                .add(HolidayServerBlocks.REDSTONE_SAND);
        this.valueLookupBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(HolidayServerBlocks.GOLDEN_HOPPER)
                .add(HolidayServerBlocks.TELE_INHIBITOR)
                .add(HolidayServerBlocks.CHUNK_LOADER);
    }
}

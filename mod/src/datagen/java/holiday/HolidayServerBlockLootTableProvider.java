package holiday;

import java.util.concurrent.CompletableFuture;

import holiday.block.HolidayServerBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

public class HolidayServerBlockLootTableProvider extends FabricBlockLootTableProvider {
    public HolidayServerBlockLootTableProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
        super(output, registries);
    }

    @Override
    public void generate() {
        this.addDrop(HolidayServerBlocks.REDSTONE_SAND);
        this.addDrop(HolidayServerBlocks.TINY_POTATO);

        this.lootTables.forEach((key, lootTable) -> {
            lootTable.randomSequenceId(key.getValue());
        });
    }
}

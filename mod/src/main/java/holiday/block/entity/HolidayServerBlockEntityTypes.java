package holiday.block.entity;

import holiday.CommonEntrypoint;
import holiday.block.HolidayServerBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public final class HolidayServerBlockEntityTypes {
    public static final BlockEntityType<InducerBlockEntity> INDUCER = register("inducer", InducerBlockEntity::new, HolidayServerBlocks.INDUCER);
    public static final BlockEntityType<GoldenHopperBlockEntity> GOLDEN_HOPPER = register("golden_hopper", GoldenHopperBlockEntity::new, HolidayServerBlocks.GOLDEN_HOPPER);

    private HolidayServerBlockEntityTypes() {
    }

    public static void register() {
        return;
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String path, FabricBlockEntityTypeBuilder.Factory<T> factory, Block... blocks) {
        RegistryKey<BlockEntityType<?>> key = RegistryKey.of(RegistryKeys.BLOCK_ENTITY_TYPE, CommonEntrypoint.identifier(path));
        BlockEntityType<T> type = FabricBlockEntityTypeBuilder.create(factory, blocks).build();

        return Registry.register(Registries.BLOCK_ENTITY_TYPE, key, type);
    }
}

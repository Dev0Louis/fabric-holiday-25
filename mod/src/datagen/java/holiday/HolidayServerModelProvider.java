package holiday;

import holiday.block.HolidayServerBlocks;
import holiday.component.HolidayServerDataComponentTypes;
import holiday.item.HolidayServerItems;
import holiday.render.MemoryValueProperty;
import holiday.state.HolidayServerProperties;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.client.data.*;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.RangeDispatchItemModel;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.item.Item;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.Direction;

public class HolidayServerModelProvider extends FabricModelProvider {
    public HolidayServerModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        generator.registerDispenserLikeOrientable(HolidayServerBlocks.INDUCER);
        generator.registerSimpleCubeAll(HolidayServerBlocks.REDSTONE_SAND);
        generator.registerSingleton(HolidayServerBlocks.STORAGE_TERMINAL, TexturedModel.CUBE_TOP);
        generator.registerNorthDefaultHorizontalRotatable(HolidayServerBlocks.TINY_POTATO);
        this.registerHopper(generator, HolidayServerBlocks.GOLDEN_HOPPER);
        this.registerPreModeled(generator, HolidayServerBlocks.TELE_INHIBITOR);
        this.registerChunkLoader(generator, HolidayServerBlocks.CHUNK_LOADER);
        this.registerColumnWithFront(generator, HolidayServerBlocks.ATTRIBUTE_TABLE);
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        generator.register(HolidayServerItems.ABSOLUTELY_SAFE_ARMOR, Models.GENERATED);
        this.registerMemory(generator, HolidayServerItems.UNSAFE_MEMORY, 4);
        generator.register(HolidayServerItems.FABRIC_PATTERN_ITEM, Models.GENERATED);
        this.registerMite(generator, HolidayServerItems.HOPPER_MITE);
        generator.register(HolidayServerItems.TATER_PATTERN_ITEM, Models.GENERATED);
        generator.register(HolidayServerItems.ECHO_DUST, Models.GENERATED);
        generator.register(HolidayServerItems.STONE_MEAL, Models.GENERATED);
        generator.register(HolidayServerItems.FINE_GRAVEL, Models.GENERATED);
        generator.register(HolidayServerItems.GROUND_GRAVEL, Models.GENERATED);
        generator.register(HolidayServerItems.WITHER_CROWN, Models.GENERATED);
    }

    private void registerMemory(ItemModelGenerator generator, Item item, int count) {
        RangeDispatchItemModel.Entry[] entries = new RangeDispatchItemModel.Entry[count];

        for (int i = 0; i < count; i++) {
            ItemModel.Unbaked model = ItemModels.basic(generator.registerSubModel(item, "_" + i, Models.GENERATED));
            entries[i] = ItemModels.rangeDispatchEntry(model, (float) i / count * Integer.MAX_VALUE);
        }

        generator.output.accept(item, ItemModels.rangeDispatch(
            MemoryValueProperty.INSTANCE,
            entries[0].model(),
            entries
        ));
    }

    private void registerMite(ItemModelGenerator generator, Item item) {
        generator.output.accept(item, ItemModels.condition(
                ItemModels.hasComponentProperty(HolidayServerDataComponentTypes.MITE_FOOD),
                ItemModels.basic(generator.registerSubModel(item, "_food", Models.GENERATED)),
                ItemModels.basic(generator.upload(item, Models.GENERATED))
        ));
    }

    private void registerHopper(BlockStateModelGenerator generator, Block block) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockModelId(block));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(block, "_side"));
        generator.registerItemModel(block.asItem());
        generator.blockStateCollector
            .accept(
                VariantsBlockModelDefinitionCreator.of(block)
                    .with(
                        BlockStateVariantMap.models(Properties.HOPPER_FACING)
                            .register(Direction.DOWN, weightedVariant)
                            .register(Direction.NORTH, weightedVariant2)
                            .register(Direction.EAST, weightedVariant2.apply(BlockStateModelGenerator.ROTATE_Y_90))
                            .register(Direction.SOUTH, weightedVariant2.apply(BlockStateModelGenerator.ROTATE_Y_180))
                            .register(Direction.WEST, weightedVariant2.apply(BlockStateModelGenerator.ROTATE_Y_270))
                    )
            );
    }

    private void registerPreModeled(BlockStateModelGenerator generator, Block toRegister) {
        generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(toRegister, new WeightedVariant(Pool.of(new ModelVariant(ModelIds.getBlockModelId(toRegister))))));
        generator.registerItemModel(toRegister.asItem(), ModelIds.getBlockModelId(toRegister));
    }

    private void registerChunkLoader(BlockStateModelGenerator generator, Block block) {
        Identifier off = Models.CUBE_COLUMN.upload(
            block,
            new TextureMap()
                .put(TextureKey.END, ModelIds.getBlockSubModelId(block, "_top"))
                .put(TextureKey.SIDE, ModelIds.getBlockModelId(block)),
            generator.modelCollector
        );

        generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block)
            .with(BlockStateVariantMap.models(HolidayServerProperties.ACTIVATED)
                .register(false, BlockStateModelGenerator.createWeightedVariant(off))
                .register(true, BlockStateModelGenerator.createWeightedVariant(ModelIds.getBlockSubModelId(block, "_on"))))
        );
        if (block.asItem() != null) generator.registerItemModel(block.asItem(), off);
    }

    private void registerColumnWithFront(BlockStateModelGenerator generator, Block block) {
        TextureMap textureMap = new TextureMap()
            .put(TextureKey.PARTICLE, TextureMap.getSubId(block, "_front"))
            .put(TextureKey.NORTH, TextureMap.getSubId(block, "_front"))
            .put(TextureKey.UP, TextureMap.getSubId(block, "_end"))
            .put(TextureKey.DOWN, TextureMap.getSubId(block, "_end"))
            .put(TextureKey.SIDE, TextureMap.getSubId(block, "_side"));
        generator.blockStateCollector
            .accept(BlockStateModelGenerator.createSingletonBlockState(block, BlockStateModelGenerator.createWeightedVariant(Models.CUBE.upload(block, textureMap, generator.modelCollector))));
    }
}

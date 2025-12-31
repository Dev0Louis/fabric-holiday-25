package holiday;

import holiday.block.HolidayServerBlocks;
import holiday.component.HolidayServerDataComponentTypes;
import holiday.item.HolidayServerItems;
import holiday.render.MemoryValueProperty;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.ItemModels;
import net.minecraft.client.data.Models;
import net.minecraft.client.data.TexturedModel;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.RangeDispatchItemModel;
import net.minecraft.item.Item;

public class HolidayServerModelProvider extends FabricModelProvider {
    public HolidayServerModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        generator.registerSimpleCubeAll(HolidayServerBlocks.REDSTONE_SAND);
        generator.registerSingleton(HolidayServerBlocks.STORAGE_TERMINAL, TexturedModel.CUBE_TOP);
        generator.registerNorthDefaultHorizontalRotatable(HolidayServerBlocks.TINY_POTATO);
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        generator.register(HolidayServerItems.ABSOLUTELY_SAFE_ARMOR, Models.GENERATED);
        this.registerMemory(generator, HolidayServerItems.UNSAFE_MEMORY, 4);
        generator.register(HolidayServerItems.FABRIC_PATTERN_ITEM, Models.GENERATED);
        this.registerMite(generator, HolidayServerItems.HOPPER_MITE);
        generator.register(HolidayServerItems.TATER_PATTERN_ITEM, Models.GENERATED);
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
}

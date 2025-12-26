package holiday;

import holiday.block.HolidayServerBlocks;
import holiday.component.HolidayServerDataComponentTypes;
import holiday.item.HolidayServerItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.ItemModels;
import net.minecraft.client.data.Models;
import net.minecraft.item.Item;

public class HolidayServerModelProvider extends FabricModelProvider {
    public HolidayServerModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        generator.registerTintableCross(HolidayServerBlocks.DIFFICULTY_SPIKE, BlockStateModelGenerator.CrossType.NOT_TINTED);
        generator.registerSimpleCubeAll(HolidayServerBlocks.REDSTONE_SAND);
        generator.registerNorthDefaultHorizontalRotatable(HolidayServerBlocks.TINY_POTATO);
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        generator.register(HolidayServerItems.ABSOLUTELY_SAFE_ARMOR, Models.GENERATED);
        generator.register(HolidayServerItems.EASY_DIFFICULTY_TICKET, Models.GENERATED);
        generator.register(HolidayServerItems.FABRIC_PATTERN_ITEM, Models.GENERATED);
        generator.register(HolidayServerItems.HARD_DIFFICULTY_TICKET, Models.GENERATED);
        this.registerMite(generator, HolidayServerItems.HOPPER_MITE);
        generator.register(HolidayServerItems.NORMAL_DIFFICULTY_TICKET, Models.GENERATED);
        generator.register(HolidayServerItems.TATER_PATTERN_ITEM, Models.GENERATED);
    }

    private void registerMite(ItemModelGenerator generator, Item item) {
        generator.output.accept(item, ItemModels.condition(
                ItemModels.hasComponentProperty(HolidayServerDataComponentTypes.MITE_FOOD),
                ItemModels.basic(generator.registerSubModel(item, "_food", Models.GENERATED)),
                ItemModels.basic(generator.upload(item, Models.GENERATED))
        ));
    }
}

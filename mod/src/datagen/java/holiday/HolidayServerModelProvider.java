package holiday;

import holiday.block.HolidayServerBlocks;
import holiday.component.HolidayServerDataComponentTypes;
import holiday.item.HolidayServerItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.ItemModels;
import net.minecraft.client.data.ModelIds;
import net.minecraft.client.data.Models;
import net.minecraft.client.render.item.tint.PotionTintSource;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class HolidayServerModelProvider extends FabricModelProvider {
    public HolidayServerModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        generator.registerSimpleCubeAll(HolidayServerBlocks.REDSTONE_SAND);
        generator.registerNorthDefaultHorizontalRotatable(HolidayServerBlocks.TINY_POTATO);
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        generator.register(HolidayServerItems.ABSOLUTELY_SAFE_ARMOR, Models.GENERATED);
        generator.register(HolidayServerItems.FABRIC_PATTERN_ITEM, Models.GENERATED);
        this.registerMite(generator, HolidayServerItems.HOPPER_MITE);
        this.registerSmoothie(generator, HolidayServerItems.SMOOTHIE);
        generator.register(HolidayServerItems.TATER_PATTERN_ITEM, Models.GENERATED);
    }

    private void registerMite(ItemModelGenerator generator, Item item) {
        generator.output.accept(item, ItemModels.condition(
                ItemModels.hasComponentProperty(HolidayServerDataComponentTypes.MITE_FOOD),
                ItemModels.basic(generator.registerSubModel(item, "_food", Models.GENERATED)),
                ItemModels.basic(generator.upload(item, Models.GENERATED))
        ));
    }

    private void registerSmoothie(ItemModelGenerator generator, Item item) {
        Identifier id = generator.uploadTwoLayers(item, ModelIds.getItemSubModelId(item, "_overlay"), ModelIds.getItemModelId(item));
        generator.output.accept(item, ItemModels.tinted(id, new PotionTintSource()));
    }
}

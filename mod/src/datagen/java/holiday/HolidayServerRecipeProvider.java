package holiday;

import java.util.concurrent.CompletableFuture;

import holiday.block.HolidayServerBlocks;
import holiday.item.HolidayServerItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

public class HolidayServerRecipeProvider extends FabricRecipeProvider {
    public HolidayServerRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
        super(output, registries);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registries, RecipeExporter exporter) {
        return new RecipeGenerator(registries, exporter) {
            @Override
            public void generate() {
                this.createShaped(RecipeCategory.COMBAT, HolidayServerItems.ABSOLUTELY_SAFE_ARMOR)
                        .input('I', Items.IRON_INGOT)
                        .input('N', Items.NETHER_STAR)
                        .input('P', Items.PIG_SPAWN_EGG)
                        .pattern("I I")
                        .pattern("INI")
                        .pattern("IPI")
                        .criterion("has_nether_star", this.conditionsFromItem(Items.NETHER_STAR))
                        .offerTo(this.exporter);

                this.createShapeless(RecipeCategory.MISC, HolidayServerItems.FABRIC_PATTERN_ITEM)
                        .input(Items.PAPER)
                        .input(Items.LOOM)
                        .criterion("has_loom", this.conditionsFromItem(Items.LOOM))
                        .offerTo(this.exporter);

                this.createShapeless(RecipeCategory.REDSTONE, HolidayServerItems.REDSTONE_SAND, 2)
                        .input(Items.REDSTONE)
                        .input(Items.SAND)
                        .criterion("has_redstone", this.conditionsFromItem(Items.REDSTONE))
                        .offerTo(this.exporter);

                this.createShapeless(RecipeCategory.MISC, HolidayServerItems.TATER_PATTERN_ITEM)
                        .input(Items.PAPER)
                        .input(Items.POTATO)
                        .criterion("has_potato", this.conditionsFromItem(Items.POTATO))
                        .offerTo(this.exporter);

                this.offer2x2CompactingRecipe(RecipeCategory.DECORATIONS, HolidayServerItems.TINY_POTATO, Items.POTATO);
            }
        };
    }

    @Override
    public String getName() {
        return "Recipes";
    }
}

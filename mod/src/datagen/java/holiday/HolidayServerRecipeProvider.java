package holiday;

import java.util.concurrent.CompletableFuture;

import holiday.baritone.BaritoneInit;
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

                this.createShaped(RecipeCategory.MISC, HolidayServerItems.ENDER_PARALYZER)
                    .input('#', Items.TWISTING_VINES)
                    .input('E', Items.ENDER_EYE)
                    .input('O', Items.OBSIDIAN)
                    .pattern("#E#")
                    .pattern("OOO")
                    .criterion("has_ender_eye", this.conditionsFromItem(Items.ENDER_EYE))
                    .offerTo(exporter);

                this.createShaped(RecipeCategory.REDSTONE, HolidayServerItems.GOLDEN_HOPPER)
                    .input('G', Items.GOLD_INGOT)
                    .input('C', Items.CHEST)
                    .pattern("G G")
                    .pattern("GCG")
                    .pattern(" G ")
                    .criterion("has_gold_ingot", this.conditionsFromItem(Items.GOLD_INGOT))
                    .offerTo(exporter);

                this.createShaped(RecipeCategory.MISC, Items.ECHO_SHARD)
                    .input('#', HolidayServerItems.ECHO_DUST)
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .criterion("has_dust", this.conditionsFromItem(HolidayServerItems.ECHO_DUST))
                    .offerTo(exporter);
                this.createShapeless(RecipeCategory.MISC, HolidayServerItems.ECHO_DUST, 9)
                    .input(Items.ECHO_SHARD)
                    .criterion("has_echo_shard", this.conditionsFromItem(Items.ECHO_SHARD))
                    .offerTo(exporter);

                this.createShaped(RecipeCategory.MISC, HolidayServerItems.GROUND_GRAVEL, 3)
                    .input('F', Items.FLINT)
                    .input('G', Items.GRAVEL)
                    .input('B', Items.BOWL)
                    .pattern("GFG")
                    .pattern("GGG")
                    .pattern("BBB")
                    .criterion("has_flint", this.conditionsFromItem(Items.FLINT))
                    .offerTo(exporter);

                this.createShaped(RecipeCategory.MISC, HolidayServerItems.FINE_GRAVEL, 3)
                    .pattern("WGW")
                    .pattern("BBB")
                    .input('G', HolidayServerItems.GROUND_GRAVEL)
                    .input('W', Items.WATER_BUCKET)
                    .input('B', Items.BOWL)
                    .criterion("has_ground_gravel", this.conditionsFromItem(HolidayServerItems.GROUND_GRAVEL))
                    .offerTo(exporter);

                this.createShapeless(RecipeCategory.MISC, HolidayServerItems.STONE_MEAL, 9) // Result: 9 Stone Meal
                    .input(HolidayServerItems.FINE_GRAVEL)
                    .input(Items.WATER_BUCKET)
                    .input(Items.BONE_MEAL)
                    .criterion("has_fine_gravel", this.conditionsFromItem(HolidayServerItems.FINE_GRAVEL))
                    .offerTo(exporter);

                this.createShaped(RecipeCategory.MISC, BaritoneInit.BARITONE)
                    .input('g', Items.GOLD_INGOT)
                    .input('G', Items.NOTE_BLOCK)
                    .input('n', Items.STONE_BUTTON)
                    .pattern("ggG")
                    .pattern("gnn")
                    .pattern("ggg")
                    .criterion("has_gold_ingot", this.conditionsFromItem(Items.GOLD_INGOT))
                    .offerTo(exporter);
            }
        };
    }

    @Override
    public String getName() {
        return "Recipes";
    }
}

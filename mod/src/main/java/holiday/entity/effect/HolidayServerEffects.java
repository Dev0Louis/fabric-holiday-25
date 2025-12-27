package holiday.entity.effect;

import holiday.CommonEntrypoint;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

public class HolidayServerEffects {

    public static RegistryEntry<StatusEffect> SLIPPERY = register("slippery",new SlipperyEffect(StatusEffectCategory.NEUTRAL,0xFF1086c1));
    public static RegistryEntry<Potion> SLIPPERY_POTION = register("slippery",new Potion("slippery",new StatusEffectInstance(SLIPPERY, 5*60*20)));
    public static RegistryEntry<Potion> LONG_SLIPPERY_POTION = register("long_slippery",new Potion("long_slippery",new StatusEffectInstance(SLIPPERY, 30*60*20)));

    public static void register() {
        FabricBrewingRecipeRegistryBuilder.BUILD.register((builder -> {
            builder.registerRecipes(Items.ICE, SLIPPERY_POTION);
            builder.registerPotionRecipe(SLIPPERY_POTION, Items.REDSTONE, LONG_SLIPPERY_POTION);
        }));
    }

    private static RegistryEntry<StatusEffect> register(String id, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(CommonEntrypoint.MOD_ID,id), statusEffect);
    }
    private static RegistryEntry<Potion> register(String name, Potion potion) {
        return Registry.registerReference(Registries.POTION, Identifier.of(CommonEntrypoint.MOD_ID,name), potion);
    }
}

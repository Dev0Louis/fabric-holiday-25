package holiday.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import holiday.entity.effect.HolidayServerEffects;
import net.minecraft.block.Block;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class SlipperyMixin {
    @WrapOperation(method = "travelMidAir",at= @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getSlipperiness()F"))
    float makeSlippery(Block instance, Operation<Float> original){
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof PlayerEntity clientPlayer && clientPlayer.getEntityWorld().isClient() && clientPlayer.hasStatusEffect(HolidayServerEffects.SLIPPERY)) {
            return 1.05f;
        }
        return original.call(instance);
    }
}

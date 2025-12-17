package holiday.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import holiday.item.HolidayServerItems;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(
            method = "isImmobile",
            at = @At("HEAD"),
            cancellable = true
    )
    private void makePlayerAbsolutelySafeFromAttemptingMovement(CallbackInfoReturnable<Boolean> ci) {
        if (HolidayServerItems.isAbsolutelySafe((LivingEntity) (Object) this)) {
            ci.setReturnValue(true);
        }
    }
}

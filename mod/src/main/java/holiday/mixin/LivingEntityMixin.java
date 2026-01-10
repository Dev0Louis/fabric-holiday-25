package holiday.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import holiday.idkwheretoputthis.WitherEntityExtension;
import holiday.item.HolidayServerItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @WrapOperation(
        method = "onKilledBy",
        constant = @Constant(classValue = WitherEntity.class)
    )
    private boolean wrapWitherInstanceof(Object object, Operation<Boolean> original) {
        if (original.call(object)) {
            WitherEntity wither = (WitherEntity) object;
            return !((WitherEntityExtension) wither).fabric_holiday_25$isInOverWorld();
        }
        return false;
    }
}

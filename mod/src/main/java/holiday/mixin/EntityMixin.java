package holiday.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import holiday.item.HolidayServerItems;
import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(
            method = "isAlwaysInvulnerableTo",
            at = @At("HEAD"),
            cancellable = true
    )
    private void makePlayerAbsolutelySafeFromDamage(CallbackInfoReturnable<Boolean> ci) {
        if (HolidayServerItems.isAbsolutelySafe((Entity) (Object) this)) {
            ci.setReturnValue(true);
        }
    }

    @Inject(
            method = "move",
            at = @At("HEAD"),
            cancellable = true
    )
    private void makePlayerAbsolutelySafeFromMovement(CallbackInfo ci) {
        if (HolidayServerItems.isAbsolutelySafe((Entity) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "getFinalGravity",
            at = @At("HEAD"),
            cancellable = true
    )
    private void makePlayerAbsolutelySafeFromGravity(CallbackInfoReturnable<Double> ci) {
        if (HolidayServerItems.isAbsolutelySafe((Entity) (Object) this)) {
            ci.setReturnValue(0.0);
        }
    }
}

package holiday.mixin;

import holiday.event.EndermanParalyzeEvent;
import net.minecraft.entity.mob.EndermanEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndermanEntity.class)
public class EndermanMixin {
	@Inject(method = "teleportTo(DDD)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/EndermanEntity;getEntityPos()Lnet/minecraft/util/math/Vec3d;", shift = At.Shift.BEFORE), cancellable = true)
    private void stopTeleporting(double d, double e, double f, CallbackInfoReturnable<Boolean> cir){
        if(EndermanParalyzeEvent.EVENT.invoker().canTeleport((EndermanEntity) (Object) this))
            cir.setReturnValue(false);
    }
}

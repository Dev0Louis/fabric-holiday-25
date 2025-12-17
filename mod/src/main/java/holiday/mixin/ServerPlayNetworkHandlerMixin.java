package holiday.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import holiday.item.HolidayServerItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Inject(
            method = "getMaxAllowedFloatingTicks",
            at = @At("HEAD"),
            cancellable = true
    )
    private void makePlayerAbsolutelySafeFromKickedForFlying(CallbackInfoReturnable<Integer> ci) {
        if (HolidayServerItems.isAbsolutelySafe((LivingEntity) (Object) this)) {
            ci.setReturnValue(Integer.MAX_VALUE);
        }
    }
}

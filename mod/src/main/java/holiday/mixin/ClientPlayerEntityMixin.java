package holiday.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import holiday.item.HolidayServerItems;
import net.minecraft.client.network.ClientPlayerEntity;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(
            method = "canSprint",
            at = @At("HEAD"),
            cancellable = true
    )
    private void makePlayerAbsolutelySafeFromSprinting(CallbackInfoReturnable<Boolean> ci) {
        if (HolidayServerItems.isAbsolutelySafe((ClientPlayerEntity) (Object) this)) {
            ci.setReturnValue(false);
        }
    }
}

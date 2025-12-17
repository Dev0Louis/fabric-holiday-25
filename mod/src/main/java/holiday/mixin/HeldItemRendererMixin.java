package holiday.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import holiday.item.HolidayServerItems;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Inject(
            method = "renderFirstPersonItem",
            at = @At("HEAD"),
            cancellable = true
    )
    private void makePlayerAbsolutelySafeFromFirstPersonItem(CallbackInfo ci, @Local AbstractClientPlayerEntity player) {
        if (HolidayServerItems.isAbsolutelySafe(player)) {
            ci.cancel();
        }
    }
}

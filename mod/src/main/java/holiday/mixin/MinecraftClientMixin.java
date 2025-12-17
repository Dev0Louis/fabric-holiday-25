package holiday.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import holiday.item.HolidayServerItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    @Final
    private ClientPlayerEntity player;

    @Shadow
    @Final
    private ClientPlayerInteractionManager interactionManager;

    @Inject(
            method = "doItemUse",
            at = @At("HEAD"),
            cancellable = true
    )
    private void makePlayerAbsolutelySafeFromInteraction(CallbackInfo ci) {
        if (this.player != null && HolidayServerItems.isAbsolutelySafe(this.player)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "handleBlockBreaking",
            at = @At("HEAD"),
            cancellable = true
    )
    private void makePlayerAbsolutelySafeFromBreaking(CallbackInfo ci) {
        if (this.player != null && HolidayServerItems.isAbsolutelySafe(this.player)) {
            this.interactionManager.cancelBlockBreaking();
            ci.cancel();
        }
    }

    @Inject(
            method = "doAttack",
            at = @At("HEAD"),
            cancellable = true
    )
    private void makePlayerAbsolutelySafeFromAttacking(CallbackInfoReturnable<Boolean> ci) {
        if (this.player != null && HolidayServerItems.isAbsolutelySafe(this.player)) {
            ci.setReturnValue(false);
        }
    }
}

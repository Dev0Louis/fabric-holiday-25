package holiday.mixin;

import holiday.idkwheretoputthis.PlayerEntityRenderStateExtension;
import holiday.item.HolidayServerItems;
import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.PlayerLikeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin<AvatarlikeEntity extends PlayerLikeEntity & ClientPlayerLikeEntity> {

    @Inject(
        method = "updateRenderState(Lnet/minecraft/entity/PlayerLikeEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V",
        at = @At("TAIL")
    )
    private void injectUpdateRenderState(AvatarlikeEntity playerLikeEntity, PlayerEntityRenderState playerEntityRenderState, float f, CallbackInfo ci) {
        PlayerEntityRenderStateExtension stateExtension = (PlayerEntityRenderStateExtension) playerEntityRenderState;

        boolean isWearingWitherCrown = playerLikeEntity.getEquippedStack(EquipmentSlot.HEAD).isOf(HolidayServerItems.WITHER_CROWN);

        stateExtension.fabric_holiday_25$setWearingWitherCrown(isWearingWitherCrown);
    }
}

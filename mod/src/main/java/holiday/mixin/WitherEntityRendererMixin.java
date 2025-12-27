package holiday.mixin;

import holiday.CommonEntrypoint;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.WitherEntityRenderer;
import net.minecraft.client.render.entity.state.WitherEntityRenderState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WitherEntityRenderer.class)
public class WitherEntityRendererMixin {

    @Unique
    private static final Identifier FRIENDLY_TEXTURE = CommonEntrypoint.identifier("textures/entity/wither/friendly_wither.png");

    @Unique
    private static final Identifier FRIENDLY_INVULNERABLE_TEXTURE = CommonEntrypoint.identifier("textures/entity/wither/friendly_wither_invulnerable.png");

    @Inject(
        method = "getTexture(Lnet/minecraft/client/render/entity/state/WitherEntityRenderState;)Lnet/minecraft/util/Identifier;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void injectGetTexture(WitherEntityRenderState witherEntityRenderState, CallbackInfoReturnable<Identifier> cir) {
        assert MinecraftClient.getInstance().player != null;
        if (MinecraftClient.getInstance().player.getEntityWorld().getDimensionEntry().matchesKey(DimensionTypes.OVERWORLD)) {
            int i = MathHelper.floor(witherEntityRenderState.invulnerableTimer);
            Identifier texture = i > 0 && (i > 80 || i / 5 % 2 != 1) ? FRIENDLY_INVULNERABLE_TEXTURE : FRIENDLY_TEXTURE;

            cir.setReturnValue(texture);
            cir.cancel();
        }
    }
}

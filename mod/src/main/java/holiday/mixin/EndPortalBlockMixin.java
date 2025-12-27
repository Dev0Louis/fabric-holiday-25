package holiday.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import holiday.CommonEntrypoint;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin {
    @WrapOperation(
            method = "onEntityCollision",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;canUsePortals(Z)Z")
    )
    public boolean disableEndPortals(Entity instance, boolean allowVehicles, Operation<Boolean> original) {
        if (instance.getEntityWorld() instanceof ServerWorld serverWorld && !serverWorld.getGameRules().getValue(CommonEntrypoint.EPORTAL_GAMERULE)) return false;
        else return original.call(instance, allowVehicles);
    }
}

package holiday.mixin;

import dev.louis.chainstylewither.entity.goal.WitherChargeAttackGoal;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WitherChargeAttackGoal.class)
public class WitherChargeAttackGoalMixin {

    @Shadow
    @Final
    private WitherEntity mob;

    @Inject(
        method = "canUse",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void disableGoalInOverworld(boolean randed, CallbackInfoReturnable<Boolean> cir) {
        if (fabric_holiday_25$isWitherInOverWorld()) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    public boolean fabric_holiday_25$isWitherInOverWorld() {
        return this.mob.getEntityWorld().getDimensionEntry().matchesKey(DimensionTypes.OVERWORLD);
    }
}

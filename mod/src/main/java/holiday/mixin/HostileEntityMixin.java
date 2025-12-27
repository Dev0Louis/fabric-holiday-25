package holiday.mixin;

import holiday.idkwheretoputthis.WitherEntityExtension;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HostileEntity.class)
public abstract class HostileEntityMixin extends PathAwareEntity {
    private HostileEntityMixin(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(
        method = "shouldDropExperience()Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private void injectShouldDropExperience(CallbackInfoReturnable<Boolean> cir) {
        if (((Object) this) instanceof WitherEntity witherEntity && ((WitherEntityExtension)witherEntity).fabric_holiday_25$isInOverWorld()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(
        method = "shouldDropLoot(Lnet/minecraft/server/world/ServerWorld;)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private void injectShouldDropLoot(ServerWorld world, CallbackInfoReturnable<Boolean> cir) {
        if (((Object) this) instanceof WitherEntity witherEntity && ((WitherEntityExtension)witherEntity).fabric_holiday_25$isInOverWorld()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}

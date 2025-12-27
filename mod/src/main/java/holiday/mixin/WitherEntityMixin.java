package holiday.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import holiday.entity.HeartEntity;
import holiday.idkwheretoputthis.WitherEntityExtension;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.dimension.DimensionTypes;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherEntity.class)
public abstract class WitherEntityMixin extends HostileEntity implements WitherEntityExtension {

    protected WitherEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    protected abstract double getHeadZ(int headIndex);

    @Shadow
    protected abstract double getHeadX(int headIndex);

    @Shadow
    protected abstract double getHeadY(int headIndex);

    @Shadow
    private int blockBreakingCooldown;

    @Inject(
        method = "shootSkullAt(IDDDZ)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void shootSkullAtMixin(int headIndex, double targetX, double targetY, double targetZ, boolean charged, CallbackInfo ci) {
        if (!this.isSilent()) {
            this.getEntityWorld().syncWorldEvent(null, WorldEvents.WITHER_SHOOTS, this.getBlockPos(), 0);
        }

        double d = this.getHeadX(headIndex);
        double e = this.getHeadY(headIndex);
        double f = this.getHeadZ(headIndex);
        double g = targetX - d;
        double h = targetY - e;
        double i = targetZ - f;

        Vec3d vec3d = new Vec3d(g, h, i);

        if (fabric_holiday_25$isInOverWorld()) {
            HeartEntity heartEntity = new HeartEntity(this.getEntityWorld(), this, vec3d.normalize());
            //heartEntity.setOwner(this);
            heartEntity.setPosition(d, e, f);
            this.getEntityWorld().spawnEntity(heartEntity);

        } else {
            WitherSkullEntity witherSkullEntity = new WitherSkullEntity(this.getEntityWorld(), this, vec3d.normalize());
            witherSkullEntity.setOwner(this);
            if (charged) {
                witherSkullEntity.setCharged(true);
            }

            witherSkullEntity.setPosition(d, e, f);
            this.getEntityWorld().spawnEntity(witherSkullEntity);
        }

        ci.cancel();
    }

    @WrapOperation(
        method = "tickMovement",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;addParticleClient(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V")
    )
    private void wrapAddParticleClient(World instance, ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Operation<Void> original) {
        if (fabric_holiday_25$isInOverWorld()) {
            original.call(instance, ParticleTypes.HEART, x, y, z, velocityX, velocityY, velocityZ);
        } else {
            original.call(instance, parameters, x, y, z, velocityX, velocityY, velocityZ);
        }
    }
    
    @WrapOperation(
        method = "mobTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;createExplosion(Lnet/minecraft/entity/Entity;DDDFZLnet/minecraft/world/World$ExplosionSourceType;)V"
        )
    )
    private void wrapCreateExplosion(ServerWorld instance, Entity entity, double x, double y, double z, float power, boolean b, World.ExplosionSourceType explosionSourceType, Operation<Void> original) {
        if (!fabric_holiday_25$isInOverWorld()) {
            original.call(instance, entity, x, y, z, power, b, explosionSourceType);
        }
    }

    @Redirect(
        method = "mobTick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/boss/WitherEntity;blockBreakingCooldown:I",
            opcode = Opcodes.GETFIELD,
            ordinal = 0
        )
    )
    private int overrideCooldown(WitherEntity instance) {
        if (fabric_holiday_25$isInOverWorld()) {
            return 0;
        }
        return this.blockBreakingCooldown;
    }

    @Override
    public boolean fabric_holiday_25$isInOverWorld() {
        return this.getEntityWorld().getDimensionEntry().matchesKey(DimensionTypes.OVERWORLD);
    }

}

package holiday.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

public class HeartEntity extends ExplosiveProjectileEntity {

    public HeartEntity(EntityType<? extends HeartEntity> type, World world) {
        super(type, world);
    }

    public HeartEntity(World world, LivingEntity owner, Vec3d velocity) {
        super(HolidayServerEntities.HEART_ENTITY, owner, velocity, world);
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected boolean isBurning() {
        return false;
    }

    @Override
    protected @Nullable ParticleEffect getParticleType() {
        return ParticleTypes.HEART;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getEntityWorld().isClient())
            this.discard();
    }
}

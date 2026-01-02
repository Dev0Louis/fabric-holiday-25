package holiday.mixin.squared;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import holiday.idkwheretoputthis.WitherEntityExtension;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = WitherEntity.class, priority = 1500)
public abstract class WitherEntityMixinSquared extends HostileEntity {
    private WitherEntityMixinSquared(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow private boolean dropLootSkip;

    public boolean fabric_holiday_25$isInOverWorldInMixinSquared() {
        return this.getEntityWorld().getDimensionEntry().matchesKey(DimensionTypes.OVERWORLD);
    }

    /*
        ChainStyleWither compatibility
     */

    @Dynamic
    @SuppressWarnings("all")
    @TargetHandler(
        mixin = "dev.louis.chainstylewither.mixin.WitherBossMixin",
        name = "customServerAiStep"
    )
    @WrapMethod(
        method = "@MixinSquared:Handler",
        remap = false
    )
    private void wrapCustomServerAiStep(ServerWorld world, CallbackInfo ci, Operation<Void> original) {
        if (!((WitherEntityExtension)(Object)this).fabric_holiday_25$isInOverWorld()) {
            original.call(world, ci);
        }
    }

    /**
     * @author Tenneb22
     * @reason Remove explosion on death from ChainStyleWither mod
     */
    @SuppressWarnings("all")
    @Overwrite
    protected void updatePostDeath() {
        super.updatePostDeath();
    }


    @TargetHandler(
        mixin = "dev.louis.chainstylewither.mixin.WitherBossMixin",
        name = "drop"
    )
    @Inject(
        method = "@MixinSquared:Handler",
        at = @At("HEAD"),
        remap = true,
        require = 0
    )
    public void makeDropStateInOverworld(ServerWorld world, DamageSource damageSource, CallbackInfo info) {
        if (!fabric_holiday_25$isInOverWorldInMixinSquared()) {
            dropLootSkip = false;
        }
    }

    @TargetHandler(
        mixin = "dev.louis.chainstylewither.mixin.WitherBossMixin",
        name = "method_16080"
    )
    @Inject(
        method = "@MixinSquared:Handler",
        at = @At("HEAD"),
        remap = true,
        require = 0
    )
    public void makeDropStateInOverworld2(ServerWorld world, DamageSource damageSource, CallbackInfo info) {
        if (!fabric_holiday_25$isInOverWorldInMixinSquared()) {
            dropLootSkip = false;
        }
    }
}

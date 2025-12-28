package holiday.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import holiday.block.HolidayServerBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.SonicBoomTask;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin(SonicBoomTask.class)
public class SonicBoomMixin extends MultiTickTask<WardenEntity> {
    public SonicBoomMixin(Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState) {
        super(requiredMemoryState);
    }
    
    @WrapMethod(method = "method_43265")
    private static void preventSonicBoom(WardenEntity wardenEntity, ServerWorld serverWorld, LivingEntity target, Operation<Void> original) {
        Box box = wardenEntity.getBoundingBox().expand(8);
        for (BlockPos pos : BlockPos.iterate(box)) {
            BlockState state = serverWorld.getBlockState(pos);
            if (state.isOf(HolidayServerBlocks.SCULK_SILENCER)) {
                return;
            }
        }
        original.call(wardenEntity, serverWorld, target);
    }
}

package holiday.mixin;

import holiday.idkwheretoputthis.PlayerEntityRenderStateExtension;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntityRenderState.class)
public abstract class PlayerEntityRenderStateMixin implements PlayerEntityRenderStateExtension {

    @Unique
    private boolean fabric_holiday_25$isWearingWitherCrown = false;

    @Override
    public boolean fabric_holiday_25$isWearingWitherCrown() {
        return this.fabric_holiday_25$isWearingWitherCrown;
    }

    @Override
    public void fabric_holiday_25$setWearingWitherCrown(boolean isWearingWitherCrown) {
        this.fabric_holiday_25$isWearingWitherCrown = isWearingWitherCrown;
    }
}

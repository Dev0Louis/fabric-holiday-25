package holiday.mixin;

import holiday.idkwheretoputthis.WitherEntityRenderStateExtension;
import net.minecraft.client.render.entity.state.WitherEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WitherEntityRenderState.class)
public abstract class WitherEntityRenderStateMixin implements WitherEntityRenderStateExtension {

    @Unique
    private boolean fabric_holiday_25$isInOverworld;

    @Override
    public void fabric_holiday_25$setInOverworld(boolean isInOverworld) {
        this.fabric_holiday_25$isInOverworld = isInOverworld;
    }

    @Override
    public boolean fabric_holiday_25$isInOverworld() {
        return this.fabric_holiday_25$isInOverworld;
    }
}

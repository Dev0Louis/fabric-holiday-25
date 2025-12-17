package holiday.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;

@Mixin(GameMenuScreen.class)
public interface GameMenuScreenAccessor {
    @Accessor
    ButtonWidget getExitButton();
}

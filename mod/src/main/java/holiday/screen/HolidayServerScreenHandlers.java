package holiday.screen;

import holiday.CommonEntrypoint;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public final class HolidayServerScreenHandlers {
    public static final ScreenHandlerType<StorageTerminalScreenHandler> STORAGE_TERMINAL = register("storage_terminal", StorageTerminalScreenHandler::new);

    private HolidayServerScreenHandlers() {
    }

    public static void register() {
        return;
    }

    public static <T extends ScreenHandler> ScreenHandlerType<T> register(String path, ScreenHandlerType.Factory<T> factory) {
        RegistryKey<ScreenHandlerType<?>> key = RegistryKey.of(RegistryKeys.SCREEN_HANDLER, CommonEntrypoint.identifier(path));
        ScreenHandlerType<T> type = new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES);

        return Registry.register(Registries.SCREEN_HANDLER, key, type);
    }
}

package holiday.screen;

import net.minecraft.client.gui.screen.ingame.HandledScreens;

public final class HolidayServerScreens {
    private HolidayServerScreens() {
    }

    public static void register() {
        HandledScreens.register(HolidayServerScreenHandlers.STORAGE_TERMINAL, StorageTerminalScreen::new);
    }
}

package holiday.baritone;

import holiday.CommonEntrypoint;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;


public class BaritoneInit {
    public static BARITONE BARITONE;
    public static SoundEvent звукБаритону;

	public static void onInitialize() {
		звукБаритону = Registry.register(
				Registries.SOUND_EVENT,
				Identifier.of(CommonEntrypoint.MOD_ID,"baritone_sound"),
				SoundEvent.of(Identifier.of(CommonEntrypoint.MOD_ID,"baritone_sound")));
		BaritoneInit.BARITONE = fabric_register_thing_stolen_from_tutorial.зареєструвати("baritone", BARITONE::new);
	}

    public static void onInitializeClient(){
        BaritoneHud.старт();
    }
}

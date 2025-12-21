package holiday.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.mob.EndermanEntity;

public interface EndermanParalyzeEvent {
    Event<EndermanParalyzeEvent> EVENT = EventFactory.createArrayBacked(EndermanParalyzeEvent.class,
            (listeners) -> (enderman) -> {
                for (EndermanParalyzeEvent listener : listeners) {
                    if (!listener.canTeleport(enderman)) {
                        return false;
                    }
                }
                return true;
            });

    boolean canTeleport(EndermanEntity enderMan);
}

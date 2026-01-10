package holiday.event;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;

@FunctionalInterface
public interface GetModelsCallback {
    Event<GetModelsCallback> EVENT = EventFactory.createArrayBacked(GetModelsCallback.class,
        (listeners) -> (builder) -> {
            for (GetModelsCallback listener : listeners) {
                listener.onGetModels(builder);
            }
        });

    void onGetModels(ImmutableMap.Builder<EntityModelLayer, TexturedModelData> builder);
}

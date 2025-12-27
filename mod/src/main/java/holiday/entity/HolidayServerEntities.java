package holiday.entity;

import holiday.CommonEntrypoint;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class HolidayServerEntities {

    public static final EntityType<HeartEntity> HEART_ENTITY = register(
        "heart",
        EntityType.Builder.<HeartEntity>create(HeartEntity::new, SpawnGroup.MISC)
            .dimensions(0.5f, 0.5f)
            .maxTrackingRange(4)
            .trackingTickInterval(10)
        );

    private HolidayServerEntities() {
    }

    public static void register() {
    }

    private static <T extends Entity> EntityType<T> register(String path, EntityType.Builder<T> type) {
        RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, CommonEntrypoint.identifier(path));

        return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
    }
}

package holiday.component;

import holiday.CommonEntrypoint;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Unit;

import java.util.function.Consumer;

import com.mojang.serialization.Codec;

public final class HolidayServerDataComponentTypes {
    public static final ComponentType<Unit> ABSOLUTELY_SAFE = register("absolutely_safe", builder -> builder
        .codec(Unit.CODEC)
        .packetCodec(Unit.PACKET_CODEC));

    public static final ComponentType<Long> LAST_DRINK_TIME = register("last_drink_time", builder -> builder
        .codec(Codec.LONG)
        .packetCodec(PacketCodecs.LONG));

    public static final ComponentType<RegistryEntry<Item>> MITE_FOOD = register("mite_food", builder -> builder
        .codec(Item.ENTRY_CODEC)
        .packetCodec(Item.ENTRY_PACKET_CODEC));

    public static final ComponentType<Integer> TOTAL_DRINK_TIME = register("total_drink_time", builder -> builder
        .codec(Codec.INT)
        .packetCodec(PacketCodecs.INTEGER));

    private HolidayServerDataComponentTypes() {
    }

    public static void register() {
        return;
    }

    public static <T> ComponentType<T> register(String path, Consumer<ComponentType.Builder<T>> factory) {
        RegistryKey<ComponentType<?>> key = RegistryKey.of(RegistryKeys.DATA_COMPONENT_TYPE, CommonEntrypoint.identifier(path));

        ComponentType.Builder<T> builder = ComponentType.builder();
        factory.accept(builder);

        return Registry.register(Registries.DATA_COMPONENT_TYPE, key, builder.build());
    }
}
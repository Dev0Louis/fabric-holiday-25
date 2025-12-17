package holiday.item;

import holiday.CommonEntrypoint;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.RegistryKey;

public final class HolidayServerEquipmentAssetKeys {
    private HolidayServerEquipmentAssetKeys() {}

    public static final RegistryKey<EquipmentAsset> ABSOLUTELY_SAFE_ARMOR = of("absolutely_safe_armor");

    public static RegistryKey<EquipmentAsset> of(String path) {
        return RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, CommonEntrypoint.identifier(path));
    }
}

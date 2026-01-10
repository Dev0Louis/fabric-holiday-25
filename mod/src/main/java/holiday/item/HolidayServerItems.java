package holiday.item;

import holiday.CommonEntrypoint;
import holiday.block.HolidayServerBlocks;
import holiday.component.HolidayServerDataComponentTypes;
import holiday.tag.HolidayServerBannerPatternTags;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Unit;

import java.util.function.Function;

public final class HolidayServerItems {
    public static final Item INDUCER = register("inducer", settings -> new BlockItem(HolidayServerBlocks.INDUCER, settings
            .useBlockPrefixedTranslationKey()));

    public static final Item REDSTONE_SAND = register("redstone_sand", settings -> new BlockItem(HolidayServerBlocks.REDSTONE_SAND, settings
            .useBlockPrefixedTranslationKey()));

    public static final Item STORAGE_TERMINAL = register("storage_terminal", settings -> new BlockItem(HolidayServerBlocks.STORAGE_TERMINAL, settings
            .useBlockPrefixedTranslationKey()));

    public static final Item TINY_POTATO = register("tiny_potato", settings -> new BlockItem(HolidayServerBlocks.TINY_POTATO, settings
            .useBlockPrefixedTranslationKey()
            .equippableUnswappable(EquipmentSlot.HEAD)));

    public static final Item TELE_INHIBITOR = register("tele_inhibitor", settings -> new BlockItem(HolidayServerBlocks.TELE_INHIBITOR, settings
        .useBlockPrefixedTranslationKey()));

    public static final Item FABRIC_PATTERN_ITEM = register("fabric_banner_pattern", new Item.Settings().maxCount(1).component(DataComponentTypes.PROVIDES_BANNER_PATTERNS, HolidayServerBannerPatternTags.FABRIC_PATTERN_ITEM));
    public static final Item TATER_PATTERN_ITEM = register("tater_banner_pattern", new Item.Settings().maxCount(1).component(DataComponentTypes.PROVIDES_BANNER_PATTERNS, HolidayServerBannerPatternTags.TATER_PATTERN_ITEM));

    public static final Item UNSAFE_MEMORY = register("unsafe_memory", settings -> new UnsafeMemoryItem(settings
        .maxCount(1)));

    public static final Item HOPPER_MITE = register("hopper_mite", settings -> new HopperMiteItem(settings
        .maxCount(1)));

    public static final Item ABSOLUTELY_SAFE_ARMOR = register("absolutely_safe_armor", new Item.Settings()
        .maxCount(1)
        .component(HolidayServerDataComponentTypes.ABSOLUTELY_SAFE, Unit.INSTANCE)
        .component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(EquipmentSlot.CHEST)
            .damageOnHurt(false)
            .model(HolidayServerEquipmentAssetKeys.ABSOLUTELY_SAFE_ARMOR)
            .cameraOverlay(Identifier.ofVanilla("block/light_gray_stained_glass"))
            .build()
        )
    );

    public static final Item GOLDEN_HOPPER = register("golden_hopper", settings -> new BlockItem(HolidayServerBlocks.GOLDEN_HOPPER, settings
        .useBlockPrefixedTranslationKey()));

    public static final Item STONE_MEAL = register("stone_meal", BoneMealItem::new);
    public static final Item GROUND_GRAVEL = register("ground_gravel",  settings -> new Item(settings.recipeRemainder(Items.BOWL)));
    public static final Item FINE_GRAVEL = register("fine_gravel", settings -> new Item(settings.recipeRemainder(Items.BOWL)));

    public static final Item ECHO_DUST = register("echo_dust", Item::new);
  
    public static final Potion HASTE_POTION = Registry.register(
        Registries.POTION,
        RegistryKey.of(RegistryKeys.POTION, CommonEntrypoint.identifier("haste_potion")),
        new Potion("haste", new StatusEffectInstance(
            StatusEffects.HASTE,
            9600,
            2
        ))
    );

    public static final Item CHUNK_LOADER = register("chunk_loader", settings -> new BlockItem(HolidayServerBlocks.CHUNK_LOADER,settings
        .rarity(Rarity.RARE)
        .useBlockPrefixedTranslationKey())
    );

    public static final Item ATTRIBUTE_TABLE = register("attribute_table", settings -> new BlockItem(HolidayServerBlocks.ATTRIBUTE_TABLE, settings
        .useBlockPrefixedTranslationKey())
    );

    public static final Item WITHER_CROWN = register("wither_crown", new Item.Settings()
        .maxCount(1)
        .equippable(EquipmentSlot.HEAD)
    );

    public static Item register(String id, Item.Settings settings) {
        return register(keyOf(id), Item::new, settings);
    }

    public static Item register(RegistryKey<Item> key, Function<Item.Settings, Item> factory, Item.Settings settings) {
        Item item = factory.apply(settings.registryKey(key));
        if (item instanceof BlockItem blockItem) {
            blockItem.appendBlocks(Item.BLOCK_ITEMS, item);
        }

        return Registry.register(Registries.ITEM, key, item);
    }

    private static RegistryKey<Item> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ITEM, CommonEntrypoint.identifier(id));
    }

    private HolidayServerItems() {
    }

    public static void register() {

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries ->
            entries.addAfter(Items.TURTLE_HELMET, ABSOLUTELY_SAFE_ARMOR, WITHER_CROWN)
        );

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.addAfter(Items.MOJANG_BANNER_PATTERN, FABRIC_PATTERN_ITEM, TATER_PATTERN_ITEM);
            entries.addAfter(Items.FLINT, GROUND_GRAVEL, FINE_GRAVEL);
            entries.addBefore(Items.ECHO_SHARD, ECHO_DUST);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.addAfter(Items.PINK_SHULKER_BOX, STORAGE_TERMINAL);
            entries.addBefore(Items.SKELETON_SKULL, TINY_POTATO);
            entries.addAfter(Items.SMITHING_TABLE, ATTRIBUTE_TABLE);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> {
            entries.addAfter(Items.HOPPER, GOLDEN_HOPPER, HOPPER_MITE);
            entries.addAfter(Items.REDSTONE_BLOCK, REDSTONE_SAND, TELE_INHIBITOR);
            entries.addBefore(Items.DISPENSER, INDUCER);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries ->
            entries.addAfter(Items.BONE_MEAL, STONE_MEAL)
        );
    }

    public static Item register(String path, Function<Item.Settings, Item> factory) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, CommonEntrypoint.identifier(path));

        Item.Settings settings = new Item.Settings().registryKey(key);
        Item item = factory.apply(settings);

        return Registry.register(Registries.ITEM, key, item);
    }

    public static boolean isAbsolutelySafe(Entity entity) {
        if (entity instanceof LivingEntity livingEntity && !entity.isSpectator() && entity.isAlive()) {
            for (EquipmentSlot slot : EquipmentSlot.VALUES) {
                ItemStack stack = livingEntity.getEquippedStack(slot);

                if (stack.contains(HolidayServerDataComponentTypes.ABSOLUTELY_SAFE)) {
                    EquippableComponent equippable = stack.get(DataComponentTypes.EQUIPPABLE);

                    if (equippable != null && slot == equippable.slot()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}

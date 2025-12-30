package holiday.block;

import holiday.CommonEntrypoint;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ColorCode;

import java.util.function.Function;

public final class HolidayServerBlocks {
    public static final Block REDSTONE_SAND = register("redstone_sand", settings -> new RedstoneSandBlock(new ColorCode(0xFFFF0000), settings
            .mapColor(MapColor.BRIGHT_RED)
            .instrument(NoteBlockInstrument.SNARE)
            .strength(0.5f)
            .sounds(BlockSoundGroup.SAND)));

    public static final Block STORAGE_TERMINAL = register("storage_terminal", settings -> new StorageTerminalBlock(settings
            .mapColor(MapColor.IRON_GRAY)
            .instrument(NoteBlockInstrument.SNARE)
            .strength(10)
            .resistance(1200)
            .sounds(BlockSoundGroup.HEAVY_CORE)));

    public static final Block TINY_POTATO = register("tiny_potato", settings -> new TinyPotatoBlock(settings
            .mapColor(MapColor.RAW_IRON_PINK)
            .breakInstantly()
            .nonOpaque()
            .sounds(BlockSoundGroup.CROP)));

    private HolidayServerBlocks() {
    }

    public static void register() {
        Registry.register(Registries.BLOCK_TYPE, CommonEntrypoint.identifier("redstone_sand"), RedstoneSandBlock.CODEC);
        Registry.register(Registries.BLOCK_TYPE, CommonEntrypoint.identifier("storage_terminal"), StorageTerminalBlock.CODEC);
        Registry.register(Registries.BLOCK_TYPE, CommonEntrypoint.identifier("tiny_potato"), TinyPotatoBlock.CODEC);
    }

    public static Block register(String path, Function<Block.Settings, Block> factory) {
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, CommonEntrypoint.identifier(path));

        Block.Settings settings = Block.Settings.create().registryKey(key);
        Block block = factory.apply(settings);

        return Registry.register(Registries.BLOCK, key, block);
    }
}
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

    public static final Block TELE_INHIBITOR = register("tele_inhibitor", settings -> new EnderParalyzerBlock(settings
        .mapColor(MapColor.PALE_PURPLE)
        .strength(1.5F)
        .sounds(BlockSoundGroup.SCULK_SENSOR)
        .luminance(state -> 1)));

    public static final Block GOLDEN_HOPPER = register("golden_hopper", settings -> new GoldenHopperBlock(settings
        .requiresTool()
        .strength(3.0F, 4.8F)
        .sounds(BlockSoundGroup.METAL)
        .nonOpaque()
        .mapColor(MapColor.GOLD)));
    
    public static final Block SCULK_SILENCER = register("sculk_silencer", Block::new);

    public static final Block CHUNK_LOADER = register("chunk_loader", settings -> new ChunkLoaderBlock(settings
        .requiresTool()
        .strength(50.0F, 1200.0F)
        .mapColor(MapColor.PURPLE)
    ));

    public static final Block ATTRIBUTE_TABLE = register("attribute_table", settings -> new AttributeTableBlock(settings
        .mapColor(MapColor.OAK_TAN)
        .instrument(NoteBlockInstrument.BASS)
        .strength(2.5F)
        .sounds(BlockSoundGroup.WOOD)
        .burnable()
    ));

    private HolidayServerBlocks() {
    }

    public static void register() {
        Registry.register(Registries.BLOCK_TYPE, CommonEntrypoint.identifier("redstone_sand"), RedstoneSandBlock.CODEC);
        Registry.register(Registries.BLOCK_TYPE, CommonEntrypoint.identifier("storage_terminal"), StorageTerminalBlock.CODEC);
        Registry.register(Registries.BLOCK_TYPE, CommonEntrypoint.identifier("tiny_potato"), TinyPotatoBlock.CODEC);
        Registry.register(Registries.BLOCK_TYPE, CommonEntrypoint.identifier("ender_paralyzer"), EnderParalyzerBlock.CODEC);
        Registry.register(Registries.BLOCK_TYPE, CommonEntrypoint.identifier("chunk_loader"), ChunkLoaderBlock.CODEC);
    }

    public static Block register(String path, Function<Block.Settings, Block> factory) {
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, CommonEntrypoint.identifier(path));

        Block.Settings settings = Block.Settings.create().registryKey(key);
        Block block = factory.apply(settings);

        return Registry.register(Registries.BLOCK, key, block);
    }
}

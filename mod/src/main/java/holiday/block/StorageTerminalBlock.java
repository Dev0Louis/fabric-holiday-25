package holiday.block;

import com.mojang.serialization.MapCodec;

import holiday.screen.StorageTerminalScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StorageTerminalBlock extends Block {
    public static final MapCodec<StorageTerminalBlock> CODEC = createCodec(StorageTerminalBlock::new);

    private static final Text TITLE = Text.translatable("text.holiday-server-mod.storage_terminal.title");

    public StorageTerminalBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
        }

        return ActionResult.SUCCESS;
    }

    @Override
    protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((syncId, playerInventory, player) -> {
            return new StorageTerminalScreenHandler(syncId, playerInventory, pos.offset(StorageTerminalScreenHandler.STORAGE_DIRECTION), world);
        }, TITLE);
    }

    @Override
    public MapCodec<? extends StorageTerminalBlock> getCodec() {
        return CODEC;
    }
}

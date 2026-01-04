package holiday.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import holiday.block.HolidayServerBlocks;
import holiday.block.entity.HolidayServerBlockEntityTypes;
import holiday.item.HopperMiteItem;
import holiday.tag.HolidayServerItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {

    @Inject(method = "serverTick", at = @At("HEAD"))
    private static void applyHopperMiteEffects(World world, BlockPos pos, BlockState state, HopperBlockEntity blockEntity, CallbackInfo ci) {
        HopperMiteItem.applyEffectsTo(world, pos, blockEntity);
    }

    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    private static void preventInsertingHopperTrapped(Inventory inventory, ItemStack stack, int slot, Direction side, CallbackInfoReturnable<Boolean> ci) {
        if (stack.isIn(HolidayServerItemTags.HOPPER_TRAPPED)) {
            ci.setReturnValue(false);
        }
    }

    @Inject(method = "canExtract", at = @At("HEAD"), cancellable = true)
    private static void preventExtractingHopperTrapped(Inventory hopperInventory, Inventory fromInventory, ItemStack stack, int slot, Direction facing, CallbackInfoReturnable<Boolean> ci) {
        if (stack.isIn(HolidayServerItemTags.HOPPER_TRAPPED)) {
            ci.setReturnValue(false);
        }
    }

    @Inject(
        method = "transfer(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/item/ItemStack;ILnet/minecraft/util/math/Direction;)Lnet/minecraft/item/ItemStack;",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void preventTransferringMiteFood(Inventory from, Inventory to, ItemStack stack, int slot, Direction side, CallbackInfoReturnable<ItemStack> ci) {
        if (from != null && HopperMiteItem.isFood(from, stack)) {
            ci.setReturnValue(stack);
        }
    }

    @ModifyArg(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/entity/LootableContainerBlockEntity;<init>(Lnet/minecraft/block/entity/BlockEntityType;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"
        )
    )
    private static BlockEntityType<?> goldenHopperMoment(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        return state.isOf(HolidayServerBlocks.GOLDEN_HOPPER)
            ? HolidayServerBlockEntityTypes.GOLDEN_HOPPER
            : blockEntityType;
    }

    @Inject(method = "extract(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/entity/ItemEntity;)Z", at = @At("HEAD"))
    private static void splitOverstackedItems(
        Inventory inventory,
        ItemEntity _itemEntity,
        CallbackInfoReturnable<Boolean> cir,
        @Local(argsOnly = true) LocalRef<ItemEntity> itemEntityLocalRef,
        @Share("split") LocalBooleanRef split,
        @Share("original") LocalRef<ItemEntity> original
    ) {
        ItemEntity itemEntity = itemEntityLocalRef.get();
        original.set(itemEntity);

        ItemStack stack = itemEntity.getStack();
        int maxCount = stack.getMaxCount();
        int count = stack.getCount();

        if (count > maxCount) {
            ItemStack splitStack = stack.split(maxCount);
            ItemEntity fakeItemEntity = new ItemEntity(EntityType.ITEM, itemEntity.getEntityWorld());
            fakeItemEntity.setStack(splitStack);
            itemEntityLocalRef.set(fakeItemEntity);
            split.set(true);
        }
    }

    @Inject(method = "extract(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/entity/ItemEntity;)Z", at = @At("RETURN"))
    private static void mergeThemBackTogetherIfTakingFails(
        Inventory inventory,
        ItemEntity _itemEntity,
        CallbackInfoReturnable<Boolean> cir,
        @Share("split") LocalBooleanRef split,
        @Share("original") LocalRef<ItemEntity> original
    ) {
        if (split.get() && !_itemEntity.getStack().isEmpty()) {
            original.get().getStack().increment(_itemEntity.getStack().getCount());
        }
    }
}

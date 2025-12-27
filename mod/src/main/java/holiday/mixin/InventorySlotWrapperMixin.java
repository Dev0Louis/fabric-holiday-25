package holiday.mixin;

import holiday.block.blockentity.GoldenHopperBlockEntity;
import net.fabricmc.fabric.impl.transfer.item.InventoryStorageImpl;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "net.fabricmc.fabric.impl.transfer.item.InventorySlotWrapper")
public abstract class InventorySlotWrapperMixin {
    @Shadow
    @Final
    private InventoryStorageImpl storage;

    @Shadow
    protected abstract ItemStack getStack();

    @ModifyArg(method = "extract(Lnet/fabricmc/fabric/api/transfer/v1/item/ItemVariant;JLnet/fabricmc/fabric/api/transfer/v1/transaction/TransactionContext;)J", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/transfer/v1/item/base/SingleStackStorage;extract(Lnet/fabricmc/fabric/api/transfer/v1/item/ItemVariant;JLnet/fabricmc/fabric/api/transfer/v1/transaction/TransactionContext;)J"))
    private long goldenHopperBiggerExtract(long size) {
        if (((InventoryStorageImplAccessor)storage).getInventory() instanceof GoldenHopperBlockEntity) return getStack().getCount();
        return size;
    }
}

package holiday.mixin;

import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import holiday.render.LargeItemStackCounts;
import holiday.screen.StorageTerminalScreen;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {
    @WrapOperation(
            method = "drawSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"
            )
    )
    private void useExpandedSlotCount(DrawContext context, TextRenderer textRenderer, ItemStack stack, int x, int y, String stackCountText, Operation<Void> operation, @Local Slot slot) {
        if (stackCountText == null && (Object) this instanceof StorageTerminalScreen screen) {
            int count = screen.getScreenHandler().getCount(slot);

            if (count > 1) {
                stackCountText = LargeItemStackCounts.format(count);
                float scale = LargeItemStackCounts.getScale(stackCountText);

                Matrix3x2fStack matrices = context.getMatrices();

                matrices.pushMatrix();
                matrices.scale(scale);

                int offsetX = 17;
                int offsetY = 9 + textRenderer.fontHeight - 2;

                x = (int) ((x + offsetX) / scale - offsetX);
                y = (int) ((y + offsetY) / scale - offsetY);

                operation.call(context, textRenderer, stack, x, y, stackCountText);

                matrices.popMatrix();
                return;
            }
        }

        operation.call(context, textRenderer, stack, x, y, stackCountText);
    }
}

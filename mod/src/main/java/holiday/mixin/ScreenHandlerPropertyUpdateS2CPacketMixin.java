package holiday.mixin;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandlerPropertyUpdateS2CPacket.class)
public class ScreenHandlerPropertyUpdateS2CPacketMixin {
    @Shadow
    @Mutable
    private int value;

    @Redirect(
        method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V",
        at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/network/PacketByteBuf;readShort()S",
                ordinal = 1
        )
    )
    private short preventReadingValueAsShort(PacketByteBuf buf) {
        return 0;
    }

    @Inject(
            method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V",
            at = @At("TAIL")
    )
    private void readValueAsInt(PacketByteBuf buf, CallbackInfo ci) {
        this.value = buf.readVarInt();
    }

    @Redirect(
            method = "write",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/PacketByteBuf;writeShort(I)Lnet/minecraft/network/PacketByteBuf;",
                    ordinal = 1
            )
    )
    private PacketByteBuf writeValueAsInt(PacketByteBuf buf, int value) {
        return buf.writeVarInt(value);
    }
}

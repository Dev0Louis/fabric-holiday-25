package holiday.mixin;

import holiday.WhitelistLogger;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.command.WhitelistCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(WhitelistCommand.class)
public class WhitelistCommandMixin {
    @Inject(
            method = "executeAdd",
            at = @At("HEAD")
    )
    private static void a(ServerCommandSource source, Collection<PlayerConfigEntry> targets, CallbackInfoReturnable<Integer> cir) {
        var whitelister = source.getName();
        targets.forEach(whiteliste -> {
            var whitelisteName = whiteliste.name();
            WhitelistLogger.logWhitelisting(whitelister, whitelisteName);
        });
    }
}

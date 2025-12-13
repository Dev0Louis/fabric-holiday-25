package holiday;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class WhitelistLogger {
    public static void logWhitelisting(String whitelister, String whitelste) {
        Path whitelistPath = FabricLoader.getInstance().getGameDir().resolve("whitelist.log");

        if (!Files.exists(whitelistPath)) {
            try {
                Files.createFile(whitelistPath);
            } catch (IOException e) {
                CommonEntrypoint.LOGGER.error("Couldn't create file: whitelist.log");
            }
        }
        try {
            var writer = Files.newBufferedWriter(whitelistPath, StandardOpenOption.APPEND);
            writer.write(whitelister + " whitelisted " + whitelste + ".\n");

            writer.close();;
        } catch (IOException e) {
            CommonEntrypoint.LOGGER.error("Couldn't create buffered reader for whitelist.log");
        }
    }
}

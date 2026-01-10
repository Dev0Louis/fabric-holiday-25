package holiday;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public record Config(
    String discordWebhookUrl,
    // does nothing if shouldWitherOnlyAttackCrown is true
    boolean shouldTatherTargetPlayers,
    boolean shouldSilenceTather,
    boolean shouldHideTatherBossBar,
    boolean shouldWitherOnlyAttackCrown
) {
    public static Config loadConfig() throws IOException {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("holiday_server.properties");
        Properties properties = new Properties();

        if (Files.exists(path)) {
            properties.load(Files.newBufferedReader(path));
        }

        var config = new Config(
            get(properties, "discordWebhookUrl", ""),
            get(properties, "shouldTatherTargetPlayers", false),
            get(properties, "shouldSilenceTather", true),
            get(properties, "shouldHideTatherBossBar", true),
            get(properties, "shouldWitherOnlyAttackCrown", true)
        );

        try (OutputStream os = Files.newOutputStream(path)) {
            properties.store(os, "Holiday Server Config");
        }

        return config;
    }

    private static String get(Properties properties, String key, String defaultValue) {
        if (!properties.containsKey(key)) {
            properties.setProperty(key, defaultValue);
        }

        return properties.getProperty(key);
    }

    private static boolean get(Properties properties, String key, boolean defaultValue) {
        return Boolean.parseBoolean(get(properties, key, Boolean.toString(defaultValue)));
    }
}

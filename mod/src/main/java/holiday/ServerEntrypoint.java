package holiday;

import com.google.gson.Gson;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ServerEntrypoint implements DedicatedServerModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerEntrypoint.class);
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();
    public static Config CONFIG;

    @Override
    public void onInitializeServer() {
        try {
            CONFIG = Config.loadConfig();
        } catch (IOException e) {
            LOGGER.error("Failed to load config", e);
            throw new UncheckedIOException(e);
        }

        if (CONFIG.discordWebhookUrl().isBlank()) {
            return;
        }


        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            announceServerEvent("Server has started! Have fun playing", CONFIG.discordWebhookUrl());
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            announceServerEvent("Playing time is over, come back later!", CONFIG.discordWebhookUrl());
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> announcePlayerJoin(handler.player, handler.player.getName().getString() + " joined the server", CONFIG.discordWebhookUrl()));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> announcePlayerJoin(handler.player, handler.player.getName().getString() + " left the server", CONFIG.discordWebhookUrl()));
    }

    private static void announceServerEvent(String message, String webookUrl) {
        sendWebhook(webookUrl, message, "https://fabricmc.net/assets/logo.png");
    }

    private static void announcePlayerJoin(ServerPlayerEntity player, String message, String webookUrl) {
        sendWebhook(webookUrl, message, getAvatarUrl(player));
    }

    private static void sendWebhook(String webookUrl, String message, String url) {
        var webhook = new Webhook(
            "Fabric Holiday Server",
            message,
            url,
            Webhook.AllowedMentions.NONE
        );
        String json = GSON.toJson(webhook);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webookUrl))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(json))
                .build();

        HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .exceptionally(e -> {
                    LOGGER.error("Failed to send join webhook", e);
                    return null;
                });
    }

    private static String getAvatarUrl(ServerPlayerEntity player) {
        return "https://mc-heads.net/head/" + player.getUuidAsString();
    }

    // https://discord.com/developers/docs/resources/webhook#execute-webhook
    private record Webhook(
        String username,
        String content,
        String avatar_url,
        AllowedMentions allowed_mentions
    ) {
        private record AllowedMentions(List<Object> parse) {
            private static final AllowedMentions NONE = new AllowedMentions(List.of());
        }
    }
}

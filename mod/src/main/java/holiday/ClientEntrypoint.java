package holiday;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix3x2fStack;

import holiday.item.HolidayServerItems;
import holiday.mixin.GameMenuScreenAccessor;

public class ClientEntrypoint implements ClientModInitializer {
    private static final Identifier SNOW_TEXTURE = Identifier.ofVanilla("textures/environment/snow.png");

    private static final SnowfallLayer[] layers = new SnowfallLayer[3];
    private static final Random random = Random.create();

    private static long screenStartTime;

    private static final Tooltip ABSOLUTELY_SAFE_EXIT_TOOLTIP = Tooltip.of(Text.translatable("item.holiday-server-mod.absolutely_safe_armor.exit_tooltip"));

    @Override
    public void onInitializeClient() {
        ClientConfigurationNetworking.registerGlobalReceiver(CommonEntrypoint.RequestVersionPayload.ID, (payload, context) -> {
            context.responseSender().sendPacket(new CommonEntrypoint.VersionResponsePayload(CommonEntrypoint.CURRENT_VERSION));
        });

        /*ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen) {
                generateSnowfallLayers();
                screenStartTime = Util.getMeasuringTimeMs();

                ScreenEvents.afterRender(screen).register(ClientEntrypoint::afterTitleScreenRender);
            }
        });*/

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof GameMenuScreen) {
                updateExitButton(client, screen);

                ScreenEvents.beforeTick(screen).register((screenx) -> {
                    updateExitButton(client, screenx);
                });
            }
        });
    }

    private static void afterTitleScreenRender(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        for (SnowfallLayer layer : layers) {
            drawSnowfallLayer(screen, drawContext, layer);
        }
    }

    private static void drawSnowfallLayer(Screen screen, DrawContext drawContext, SnowfallLayer layer) {
        int width = screen.width;
        int height = screen.height;

        double time = (Util.getMeasuringTimeMs() - screenStartTime) / 100d;

        double x = (time * layer.velocityX()) % width;
        double y = (time + layer.deltaY() * height) % height;

        Matrix3x2fStack matrices = drawContext.getMatrices();

        matrices.pushMatrix();
        matrices.translate((float) x, (float) y);

        drawContext.drawTexture(RenderPipelines.GUI_TEXT, SNOW_TEXTURE, 0, 0, 0, 0, width, height, 64, 256);
        drawContext.drawTexture(RenderPipelines.GUI_TEXT, SNOW_TEXTURE, 0, -height, 0, 0, width, height, 64, 256);
        drawContext.drawTexture(RenderPipelines.GUI_TEXT, SNOW_TEXTURE, -width, 0, 0, 0, width, height, 64, 256);
        drawContext.drawTexture(RenderPipelines.GUI_TEXT, SNOW_TEXTURE, -width, -height, 0, 0, width, height, 64, 256);

        matrices.popMatrix();
    }

    private static void generateSnowfallLayers() {
        for (int i = 0; i < layers.length; i++) {
            layers[i] = new SnowfallLayer(random.nextDouble() * 2 - 1, random.nextDouble());
        }
    }

    private static void updateExitButton(MinecraftClient client, Screen screen) {
        ButtonWidget button = ((GameMenuScreenAccessor) screen).getExitButton();

        if (client.player != null) {
            boolean safe = HolidayServerItems.isAbsolutelySafe(client.player);

            button.active = !safe;
            button.setTooltip(safe ? ABSOLUTELY_SAFE_EXIT_TOOLTIP : null);
        }
    }

    record SnowfallLayer(double velocityX, double deltaY) {}
}
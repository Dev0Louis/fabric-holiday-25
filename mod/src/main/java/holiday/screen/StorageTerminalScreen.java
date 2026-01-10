package holiday.screen;

import holiday.CommonEntrypoint;
import holiday.CommonEntrypoint.StorageTerminalSearchPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.cursor.StandardCursors;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class StorageTerminalScreen extends HandledScreen<StorageTerminalScreenHandler> {
    private static final Identifier TEXTURE = CommonEntrypoint.identifier("textures/gui/container/storage_terminal.png");

    private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("container/creative_inventory/scroller");
    private static final Identifier SCROLLER_DISABLED_TEXTURE = Identifier.ofVanilla("container/creative_inventory/scroller_disabled");

    private static final Text SEARCH_LABEL = Text.translatable("itemGroup.search");
    private static final Text NO_STORAGE_CONNECTED_TEXT = Text.translatable("text.holiday-server-mod.storage_terminal.no_storage_connected");

    private TextFieldWidget searchBox;

    private float scrollPosition;
    private boolean scrolling;

    public StorageTerminalScreen(StorageTerminalScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        this.backgroundWidth = 194;
        this.backgroundHeight = 204;

        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        this.searchBox = new TextFieldWidget(this.textRenderer, this.x + 82, this.y + 6, 80, 9, SEARCH_LABEL);

        this.searchBox.setMaxLength(StorageTerminalScreenHandler.MAX_SEARCH_LENGTH);
        this.searchBox.setDrawsBackground(false);
        this.searchBox.setEditableColor(-1);
        this.searchBox.setInvertSelectionBackground(false);
        this.searchBox.setFocused(true);

        this.addSelectableChild(this.searchBox);
    }

    @Override
    public void resize(int width, int height) {
        String search = this.searchBox.getText();

        super.resize(width, height);
        this.searchBox.setText(search);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        super.render(context, mouseX, mouseY, tickDelta);

        boolean scrollable = this.getOverflowRows() > 0;
        this.scrollPosition = scrollable ? MathHelper.clamp(this.scrollPosition, 0, 1) : 0;

        if (scrollable && this.isClickInScrollbar(mouseX, mouseY)) {
            context.setCursor(this.scrolling ? StandardCursors.RESIZE_NS : StandardCursors.POINTING_HAND);
        }

        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, scrollable ? SCROLLER_TEXTURE : SCROLLER_DISABLED_TEXTURE, this.x + 175, this.y + 18 + (int) (this.scrollPosition * 95), 12, 15);
        this.searchBox.render(context, mouseX, mouseY, tickDelta);

        if (!this.getScreenHandler().isConnected()) {
            int x = (this.width - this.backgroundWidth) / 2;
            int y = (this.height - this.backgroundHeight) / 2;

            int textX = x + 90;
            int textY = y + 62 - this.textRenderer.fontHeight / 2;

            double dangerSpeed = this.client.options.getGlintSpeed().getValue();
            double danger = dangerSpeed == 0 ? 1 : (float) (Math.sin((this.client.player.age + tickDelta) / 3 * dangerSpeed) + 1) / 2;

            int backgroundColor = this.client.options.getTextBackgroundColor(0.8f);
            int color = ColorHelper.lerp((float) danger, Colors.WHITE, Colors.RED);

            OrderedText text = NO_STORAGE_CONNECTED_TEXT.asOrderedText();
            int width = this.textRenderer.getWidth(text);

            context.fill(textX - width / 2 - 3, textY - 3, textX + width / 2 + 2, textY + this.textRenderer.fontHeight + 2, backgroundColor);
            context.drawTextWithShadow(this.textRenderer, text, textX - width / 2, textY, color);
        }

        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float tickDelta, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
    }

    private void sendSearchPacket() {
        int skip = MathHelper.clamp((int) (this.scrollPosition * this.getOverflowRows() + 0.5) * StorageTerminalScreenHandler.INVENTORY_WIDTH, 0, this.getScreenHandler().getSize());
        ClientPlayNetworking.send(new StorageTerminalSearchPayload(this.searchBox.getText(), skip));
    }

    @Override
    public boolean charTyped(CharInput input) {
        String search = this.searchBox.getText();

        if (this.searchBox.charTyped(input)) {
            if (!Objects.equals(search, this.searchBox.getText())) {
                this.sendSearchPacket();
            }

            return true;
        }

        return super.charTyped(input);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        String search = this.searchBox.getText();

        if (this.searchBox.keyPressed(input)) {
            if (!Objects.equals(search, this.searchBox.getText())) {
                this.sendSearchPacket();
            }

            return true;
        } else if (this.searchBox.isFocused() && this.searchBox.isVisible() && !input.isEscape()) {
            return true;
        }

        return super.keyPressed(input);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT && this.isClickInScrollbar(click.x(), click.y())) {
            this.scrolling = true;
            return true;
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (click.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.scrolling = false;
        }

        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (this.scrolling) {
            this.scrollPosition = MathHelper.clamp((float) (click.y() - this.y - 25.5f) / 97, 0, 1);
            this.sendSearchPacket();

            return true;
        } else {
            return super.mouseDragged(click, offsetX, offsetY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            this.scrollPosition = MathHelper.clamp(this.scrollPosition - (float) (verticalAmount / this.getOverflowRows()), 0, 1);
            this.sendSearchPacket();
        }

        return true;
    }

    private boolean isClickInScrollbar(double mouseX, double mouseY) {
        int minX = this.x + 175;
        int minY = this.y + 18;

        int maxX = minX + 14;
        int maxY = minY + 112;

        return mouseX >= minX && mouseY >= minY && mouseX < maxX && mouseY < maxY;
    }

    private int getOverflowRows() {
        return MathHelper.ceilDiv(Math.max(this.getScreenHandler().getSize(), 1), StorageTerminalScreenHandler.INVENTORY_WIDTH) - StorageTerminalScreenHandler.INVENTORY_HEIGHT;
    }
}

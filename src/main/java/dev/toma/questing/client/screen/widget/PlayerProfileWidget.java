package dev.toma.questing.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.toma.questing.utils.Alignment;
import dev.toma.questing.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import java.util.UUID;

public final class PlayerProfileWidget extends ContainerWidget {

    private final UUID playerId;
    private final int pictureSize;
    private int frameSize;
    private int frameColor;
    private String tooltipText;
    private boolean canShowName;
    private boolean allowOnlineStatus;
    private boolean isOnline;

    public PlayerProfileWidget(int x, int y, int width, int height, UUID playerId) {
        super(x, y, width, height, StringTextComponent.EMPTY);
        this.playerId = playerId;
        this.canShowName = Math.abs(height - width) > 25;
        this.pictureSize = Math.min(width, height);
        PlayerEntity player = Minecraft.getInstance().level.getPlayerByUUID(playerId);
        if (this.canShowName) {
            this.tooltipText = player != null ? player.getName().getString() : "Unknown player";
        }
        this.isOnline = player != null;
    }

    public void forceTooltipText(@Nullable String text) {
        this.tooltipText = text;
    }

    public void setFrame(int frameSize, int frameColor) {
        this.frameSize = frameSize;
        this.frameColor = frameColor;
    }

    public void showOnlineStatus(boolean allowOnlineStatus) {
        this.allowOnlineStatus = allowOnlineStatus;
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.color3f(1.0F, 1.0F, 1.0F);
        if (frameSize > 0) {
            fill(stack, x - frameSize, y - frameSize, x + width + frameSize, y + height + frameSize, frameColor);
            fill(stack, x, y, x + width, y + height, 0xFF << 24);
        }
        Minecraft client = Minecraft.getInstance();
        TextureManager textureManager = client.getTextureManager();
        PlayerEntity player = client.player.level.getPlayerByUUID(this.playerId);
        ResourceLocation skinTexture = DefaultPlayerSkin.getDefaultSkin(this.playerId);
        if (player != null) {
            skinTexture = ((AbstractClientPlayerEntity) player).getSkinTextureLocation();
        }
        textureManager.bind(skinTexture);
        float min =  8.0F / 64.0F;
        float max = 16.0F / 64.0F;
        RenderUtils.blit(stack, x, y, x + pictureSize, y + pictureSize, min, min, max, max);
        if (this.canShowName) {
            RenderUtils.drawAlignedText(Alignment.VERTICAL, stack, this.tooltipText, x + pictureSize + 3, y, width - pictureSize + 3, height, 0xFFFFFF, FontRenderer::width, FontRenderer::drawShadow);
        } else {
            if (isHovered && tooltipText != null) {
                FontRenderer font = client.font;
                int width = font.width(tooltipText);
                int textLeft = x + (this.width - width) / 2;
                fill(stack, textLeft - 2, y + height + 1, textLeft + width + 2, y + height + font.lineHeight + 3, 0xFF000033);
                font.draw(stack, tooltipText, textLeft, y + height + 3, 0xFFFFFF);
            }
        }
        if (this.allowOnlineStatus && isOnline) {
            int x1 = this.x + pictureSize - 5;
            int x2 = x1 + 4;
            int y1 = this.y + pictureSize - 5;
            int y2 = y1 + 4;
            fill(stack, x1, y1, x2, y2, 0xFF << 24);
            fill(stack, x1 + 1, y1 + 1, x2 - 1, y2 - 1, 0xFF00FF33);
        }
        super.renderButton(stack, mouseX, mouseY, partialTicks);
    }
}

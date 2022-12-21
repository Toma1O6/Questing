package dev.toma.questing.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public final class PlayerProfileWidget extends Widget {

    private final UUID playerId;
    private int frameSize;
    private int frameColor;
    private OnClick onClick = (x, y, profile) -> {};
    private String tooltipText;
    private boolean canShowName;
    private int pictureSize;

    public PlayerProfileWidget(int x, int y, int width, int height, UUID playerId) {
        super(x, y, width, height, StringTextComponent.EMPTY);
        this.playerId = playerId;
        this.canShowName = height - width > 25;
        this.pictureSize = Math.min(width, height);
        if (this.canShowName) {
            PlayerEntity player = Minecraft.getInstance().level.getPlayerByUUID(playerId);
            this.tooltipText = player != null ? player.getName().getString() : "Unknown player";
        }
    }

    public void forceTooltipText(@Nullable String text) {
        this.tooltipText = text;
    }

    public void setClickResponder(OnClick onClick) {
        this.onClick = Objects.requireNonNull(onClick);
    }

    public void setClickResponder(OnClick onClick, int requiredButton) {
        this.setClickResponder(new OnClick() {
            @Override
            public void clicked(int mouseX, int mouseY, PlayerProfileWidget profileWidget) {
                onClick.clicked(mouseX, mouseY, profileWidget);
            }

            @Override
            public boolean isValidButton(int button) {
                return button == requiredButton;
            }
        });
    }

    public void setFrame(int frameSize, int frameColor) {
        this.frameSize = frameSize;
        this.frameColor = frameColor;
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        if (frameSize > 0) {
            fill(stack, x - frameSize, y - frameSize, x + width + frameSize, y + height + frameSize, frameColor);
            fill(stack, x, y, x + width, y + height, 0xFF << 24);
        }
        Minecraft client = Minecraft.getInstance();
        TextureManager textureManager = client.getTextureManager();
        textureManager.bind(client.player.getSkinTextureLocation());
        float min =  8.0F / 64.0F;
        float max = 16.0F / 64.0F;
        RenderUtils.blit(stack, x, y, x + width, y + height, min, min, max, max);
        if (isHovered && tooltipText != null) {
            FontRenderer font = client.font;
            int width = font.width(tooltipText);
            int textLeft = x + (this.width - width) / 2;
            fill(stack, textLeft - 2, y + height + 1, textLeft + width + 2, y + height + font.lineHeight + 3, 0xFF000033);
            font.draw(stack, tooltipText, textLeft, y + height + 3, 0xFFFFFF);
        }
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return onClick.isValidButton(button);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        onClick.clicked((int) mouseX, (int) mouseY, this);
    }

    @FunctionalInterface
    public interface OnClick {
        void clicked(int mouseX, int mouseY, PlayerProfileWidget profileWidget);

        default boolean isValidButton(int button) {
            return true;
        }
    }
}

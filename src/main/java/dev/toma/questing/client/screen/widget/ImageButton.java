package dev.toma.questing.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ImageButton extends Button {

    private ResourceLocation image;
    private int imageMargin = 2;
    private int frameSize = 0;
    private int frameColor = 0x00000000;
    private int hoverFrameColor = frameColor;
    private int hoverColor = 0x44FFFFFF;
    private float u1 = 0.0F;
    private float v1 = 0.0F;
    private float u2 = 1.0F;
    private float v2 = 1.0F;

    public ImageButton(int x, int y, int width, int height, ResourceLocation image, IPressable clickResponder) {
        this(x, y, width, height, image, StringTextComponent.EMPTY, clickResponder);
    }

    protected ImageButton(int x, int y, int width, int height, ResourceLocation image, ITextComponent text, IPressable clickResponder) {
        super(x, y, width, height, text, clickResponder);
        this.image = image;
    }

    public void setImage(ResourceLocation image) {
        this.image = image;
    }

    public void setImageMargin(int imageMargin) {
        this.imageMargin = imageMargin;
    }

    public void setImageUV(float u1, float v1, float u2, float v2) {
        this.u1 = u1;
        this.v1 = v1;
        this.u2 = u2;
        this.v2 = v2;
    }

    public void setFrameSize(int frameSize) {
        this.frameSize = frameSize;
    }

    public void setFrameColor(int frameColor) {
        this.frameColor = frameColor;
    }

    public void setHoverFrameColor(int hoverFrameColor) {
        this.hoverFrameColor = hoverFrameColor;
    }

    public void setHoverColor(int hoverColor) {
        this.hoverColor = hoverColor;
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        if (frameSize > 0) {
            int color = isHovered ? hoverFrameColor : frameColor;
            RenderUtils.outline(stack, x, y, x + width, y + height, color, 0, frameSize);
        }
        renderContent(stack);
        if (isHovered) {
            fill(stack, x, y, x + width, y + height, hoverColor);
        }
    }

    protected void renderContent(MatrixStack stack) {
        Minecraft.getInstance().getTextureManager().bind(image);
        RenderUtils.blit(stack, x + imageMargin, y + imageMargin, x + width - imageMargin, y + height - imageMargin, u1, v1, u2, v2);
    }
}

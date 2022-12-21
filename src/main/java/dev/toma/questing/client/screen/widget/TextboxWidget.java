package dev.toma.questing.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.utils.Alignment;
import dev.toma.questing.utils.RenderUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;

import java.util.Objects;

public class TextboxWidget extends Widget {

    private final FontRenderer font;
    private int color;
    private Alignment alignment;
    private RenderUtils.TextRenderer<ITextComponent> textRenderer;

    public TextboxWidget(int x, int y, int width, int height, ITextComponent text, FontRenderer font) {
        super(x, y, width, height, text);
        this.font = font;
        this.setTextColor(0xFFFFFF);
        this.setTextAlignment(Alignment.CENTER);
        this.setTextRenderer(FontRenderer::draw);
    }

    public void setTextColor(int textColor) {
        color = textColor;
    }

    public void setTextAlignment(Alignment alignment) {
        this.alignment = Objects.requireNonNull(alignment);
    }

    public void setTextRenderer(RenderUtils.TextRenderer<ITextComponent> textRenderer) {
        this.textRenderer = Objects.requireNonNull(textRenderer);
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        ITextComponent text = this.getMessage();
        if (text == null)
            return;
        RenderUtils.drawAlignedText(alignment, stack, font, text, x, y, width, height, color, FontRenderer::width, textRenderer);
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return false;
    }
}

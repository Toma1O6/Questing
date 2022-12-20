package dev.toma.questing.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;

public final class QuestsScreen extends Screen {

    private static final int HEADER_HEIGHT = 20;
    private static final int ELEMENT_MARGIN = 5;

    public QuestsScreen() {
        super(new TranslationTextComponent("questing.screen.quest_screen"));
    }

    @Override
    protected void init() {
        
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(stack);
        drawHeader(stack, mouseX, mouseY, partialTicks);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private void drawHeader(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        fill(stack, 0, 0, width, HEADER_HEIGHT, 0x66 << 24);
        font.drawShadow(stack, this.title, ELEMENT_MARGIN, (HEADER_HEIGHT - font.lineHeight) / 2.0F, 0xFFFFFF);
    }
}

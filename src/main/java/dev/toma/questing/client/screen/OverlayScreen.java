package dev.toma.questing.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;

public abstract class OverlayScreen extends Screen {

    private final Screen layeredScreen;
    protected int leftPos, topPos;
    protected int innerWidth, innerHeight;

    public OverlayScreen(ITextComponent title, Screen layeredScreen) {
        super(title);
        this.layeredScreen = layeredScreen;
    }

    @Override
    protected void init() {
        this.setDimensions(176, 166);
    }

    protected abstract void drawContent(MatrixStack stack, int mouseX, int mouseY, float partialTicks);

    protected void setDimensions(int innerWidth, int innerHeight) {
        this.innerWidth = innerWidth;
        this.innerHeight = innerHeight;
        this.leftPos = (this.width - this.innerWidth) / 2;
        this.topPos = (this.height - this.innerHeight) / 2;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.layeredScreen.render(stack, mouseX, mouseY, partialTicks);
        this.drawContent(stack, mouseX, mouseY, partialTicks);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    protected Screen getLayeredScreen() {
        return layeredScreen;
    }

    protected void spaceEqually(Widget w1, Widget w2, int margin) {
        int useableWidth = this.innerWidth - 3 * margin;
        int widgetWidth = useableWidth / 2;
        int xPosW1 = this.leftPos + margin;
        int xPosW2 = this.leftPos + innerWidth - margin - widgetWidth;
        w1.setWidth(widgetWidth);
        w2.setWidth(widgetWidth);
        w1.x = xPosW1;
        w2.x = xPosW2;
    }
}

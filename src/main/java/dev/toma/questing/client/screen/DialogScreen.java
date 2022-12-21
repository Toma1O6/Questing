package dev.toma.questing.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DialogScreen extends OverlayScreen {

    public static final ITextComponent TEXT_CONFIRM = new TranslationTextComponent("text.questing.confirm");
    public static final ITextComponent TEXT_CANCEL = new TranslationTextComponent("text.questing..cancel");

    private RespondEvent onConfirm;
    private RespondEvent onCancel;

    private DialogScreen(ITextComponent title, Screen parent) {
        super(title, parent);
    }

    @Override
    protected void drawContent(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {

    }

    public void closeDialog() {
        minecraft.setScreen(this.getLayeredScreen());
    }

    @FunctionalInterface
    public interface RespondEvent {

        void respond(DialogScreen screen);
    }
}

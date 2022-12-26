package dev.toma.questing.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.client.QuestingClient;
import dev.toma.questing.client.render.NotificationDockType;
import dev.toma.questing.client.render.NotificationFlowType;
import dev.toma.questing.client.render.NotificationRenderer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.StringTextComponent;

public class NotificationWidget extends Widget {

    private final NotificationRenderer renderer;

    public NotificationWidget(int x, int y, int width, int height, FontRenderer font, NotificationDockType dockType, NotificationFlowType flowType) {
        super(x, y, width, height, StringTextComponent.EMPTY);
        this.renderer = QuestingClient.CLIENT.notificationRendererProvider.getRendererWithConfiguration(dockType, flowType, x, y, width, height, width, height, font);
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderer.drawNotification(stack, partialTicks);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.renderer.onNotificationClicked();
    }
}

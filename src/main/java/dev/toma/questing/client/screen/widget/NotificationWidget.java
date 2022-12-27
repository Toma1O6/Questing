package dev.toma.questing.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.client.QuestingClient;
import dev.toma.questing.client.render.NotificationDockType;
import dev.toma.questing.client.render.NotificationFlowType;
import dev.toma.questing.client.render.NotificationRenderer;
import dev.toma.questing.client.render.NotificationRendererProvider;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.StringTextComponent;

public class NotificationWidget extends Widget {

    private final NotificationRenderer renderer;

    public NotificationWidget(int x, int y, int windowWidth, int windowHeight, int width, int height, FontRenderer font, NotificationDockType dockType, NotificationFlowType flowType) {
        super(x, y, width, height, StringTextComponent.EMPTY);
        this.renderer = QuestingClient.CLIENT.notificationRendererProvider.getRendererWithConfiguration(dockType, flowType, x, y, windowWidth, windowHeight, width, height, font);
    }

    public NotificationWidget(int x, int y, int windowWidth, int windowHeight, FontRenderer font, NotificationDockType dockType, NotificationFlowType flowType) {
        this(x, y, windowWidth, windowHeight, NotificationRendererProvider.NOTIFICATION_WIDTH, NotificationRendererProvider.NOTIFICATION_HEIGHT, font, dockType, flowType);
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        stack.pushPose();
        //stack.translate(0, 0, 999);
        this.renderer.drawNotification(stack, partialTicks, isHovered);
        stack.popPose();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.renderer.onNotificationClicked();
    }
}

package dev.toma.questing.client.screen;

import dev.toma.questing.client.render.NotificationDockType;
import dev.toma.questing.client.render.NotificationFlowType;
import dev.toma.questing.client.screen.widget.NotificationWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

public abstract class NotificationOverlayScreen extends OverlayScreen {

    public NotificationOverlayScreen(ITextComponent title, Screen layeredScreen) {
        super(title, layeredScreen);
    }

    protected void addNotificationWidget() {
        this.addButton(this.createNotificationWidget());
    }

    protected NotificationWidget createNotificationWidget() {
        return new NotificationWidget(0, 0, width, height, font, this.getNotificationDocking(), this.getNotificationFlow());
    }

    protected NotificationDockType getNotificationDocking() {
        return NotificationDockType.RIGHT_LOWER;
    }

    protected NotificationFlowType getNotificationFlow() {
        return NotificationFlowType.LEFT;
    }
}

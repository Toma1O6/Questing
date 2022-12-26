package dev.toma.questing.common.notification;

import dev.toma.questing.utils.RenderUtils;
import dev.toma.questing.utils.Utils;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.ArrayList;
import java.util.List;

public class Notification {

    public static final Marker MARKER = MarkerManager.getMarker("Notifications");
    private final NotificationIcon<?> icon;
    private final ITextComponent header;
    private final ITextComponent[] content;
    private final int renderTimer;
    private final int appearTimer;
    private NotificationStage stage = NotificationStage.READY;
    private int renderIndex;
    private int timer;
    private int lastTimer;
    private int currentAppearTimer;
    private int lastAppearTimer;

    private Notification(NotificationIcon<?> icon, ITextComponent header, ITextComponent[] content, int renderTimer, int appearTimer) {
        this.icon = icon;
        this.header = header;
        this.content = content;
        this.renderTimer = renderTimer;
        this.appearTimer = appearTimer;
    }

    public void tickNotification() {
        if (this.stage == NotificationStage.DISPLAY) {
            lastTimer = timer;
            if (++timer > renderTimer) {
                timer = 0;
                lastTimer = 0;
                if (renderIndex++ >= content.length) {
                    this.advanceStage();
                }
            }
        } else if (this.stage.isAnimationStage()) {
            this.lastAppearTimer = currentAppearTimer;
            if (currentAppearTimer++ > appearTimer) {
                this.advanceStage();
            }
        }
    }

    public float getAppearProgress(float partialTicks) {
        float interpolated = RenderUtils.linearInterpolate(lastAppearTimer / (float) appearTimer, currentAppearTimer / (float) appearTimer, partialTicks);
        return this.stage == NotificationStage.APPEAR ? interpolated : 1.0F - interpolated;
    }

    public float getTextStageProgress(float partialTicks) {
        float interpolated = RenderUtils.linearInterpolate(lastTimer / (float) renderTimer, timer / (float) renderTimer, partialTicks);
        return 1.0F - interpolated;
    }

    public boolean isForRemoval() {
        return this.stage == NotificationStage.COMPLETED;
    }

    public void setStage(NotificationStage stage) {
        this.stage = stage;
    }

    public NotificationIcon<?> getIcon() {
        return icon;
    }

    public ITextComponent getHeader() {
        return header;
    }

    public ITextComponent getContent() {
        return content[renderIndex];
    }

    private void advanceStage() {
        this.stage = Utils.next(this.stage);
        this.resetAttributes();
    }

    private void resetAttributes() {
        this.timer = 0;
        this.lastTimer = 0;
        this.currentAppearTimer = 0;
        this.lastAppearTimer = 0;
    }

    public static final class Builder {

        private NotificationIcon<?> icon = NotificationIcon.none();
        private ITextComponent header;
        private List<ITextComponent> content = new ArrayList<>();
        private int renderTimer = 40;
        private int appearTimer = 20;
    }
}

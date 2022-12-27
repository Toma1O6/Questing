package dev.toma.questing.common.notification;

import dev.toma.questing.utils.RenderUtils;
import dev.toma.questing.utils.Utils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private int dynamicContentSize;

    public Notification(NotificationIcon<?> icon, ITextComponent header, ITextComponent[] content, int renderTimer, int appearTimer) {
        this.icon = icon;
        this.header = header;
        this.content = content;
        this.renderTimer = renderTimer;
        this.appearTimer = appearTimer;
        this.dynamicContentSize = content.length;
    }

    public void tickNotification() {
        if (this.getStage() == NotificationStage.DISPLAY) {
            lastTimer = timer;
            if (++timer > renderTimer) {
                timer = 0;
                lastTimer = 0;
                if (renderIndex + 1 >= this.dynamicContentSize) {
                    this.advanceStage();
                } else {
                    ++renderIndex;
                }
            }
        } else if (this.getStage().isAnimationStage()) {
            this.lastAppearTimer = currentAppearTimer;
            if (currentAppearTimer++ > appearTimer) {
                this.advanceStage();
            }
        }
    }

    public float getAppearProgress(float partialTicks) {
        float interpolated = RenderUtils.linearInterpolate(lastAppearTimer / (float) appearTimer, currentAppearTimer / (float) appearTimer, partialTicks);
        return this.getStage() == NotificationStage.APPEAR ? interpolated : 1.0F - interpolated;
    }

    public float getTextStageProgress(float partialTicks) {
        return RenderUtils.linearInterpolate(lastTimer / (float) renderTimer, timer / (float) renderTimer, partialTicks);
    }

    public void copyRenderingAttributes(Notification other) {
        this.setStage(other.stage);
        this.renderIndex = MathHelper.clamp(other.renderIndex, 0, this.dynamicContentSize - 1);
        this.timer = other.timer;
        this.lastTimer = other.lastTimer;
        this.currentAppearTimer = other.currentAppearTimer;
        this.lastAppearTimer = other.lastAppearTimer;
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

    public int getRenderTimer() {
        return renderTimer;
    }

    public int getAppearTimer() {
        return appearTimer;
    }

    public int getRenderIndex() {
        return renderIndex;
    }

    public ITextComponent[] getAllContents() {
        return this.content;
    }

    public NotificationStage getStage() {
        return stage;
    }

    public void setDynamicContentSize(int dynamicContentSize) {
        this.dynamicContentSize = dynamicContentSize;
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

    @Override
    public String toString() {
        return "Notification{" +
                "icon=" + icon +
                ", header=" + header +
                ", content=" + Arrays.toString(content) +
                ", stage=" + stage +
                '}';
    }

    public static final class Builder {

        private NotificationIcon<?> icon = NotificationIcon.none();
        private ITextComponent header;
        private List<ITextComponent> content = new ArrayList<>();
        private int renderTimer = 40;
        private int appearTimer = 20;

        public Builder icon(NotificationIcon<?> icon) {
            this.icon = icon;
            return this;
        }

        public Builder header(ITextComponent header) {
            this.header = header;
            return this;
        }

        public Builder addContentSlide(ITextComponent content) {
            this.content.add(content);
            return this;
        }

        public Builder setSlideRenderTimer(int renderTimer) {
            this.renderTimer = renderTimer;
            return this;
        }

        public Builder setRenderAppearTimer(int appearTimer) {
            this.appearTimer = appearTimer;
            return this;
        }

        public Notification buildNotification() {
            Objects.requireNonNull(icon);
            Objects.requireNonNull(header);
            return new Notification(icon, header, content.toArray(new ITextComponent[0]), renderTimer, appearTimer);
        }
    }
}

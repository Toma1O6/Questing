package dev.toma.questing.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.client.QuestingClient;
import dev.toma.questing.common.notification.Notification;
import dev.toma.questing.common.notification.NotificationIcon;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class NotificationRenderer {

    private static final float TEXT_FADING_POINT = 0.20F;
    private static final float TEXT_FADING_LIMIT = 0.11F;
    private final FontRenderer font;
    private final NotificationDockType dockType;
    private final NotificationFlowType flowType;
    private final int windowX, windowY, windowWidth, windowHeight;
    private final int width;
    private final int height;
    private BakedNotification notification;
    private long lastRenderTime;

    public NotificationRenderer(FontRenderer font, NotificationDockType dockType, NotificationFlowType flowType, int windowX, int windowY, int windowWidth, int windowHeight, int width, int height) {
        this.font = font;
        this.dockType = dockType;
        this.flowType = flowType;
        this.windowX = windowX;
        this.windowY = windowY;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.width = width;
        this.height = height;
    }

    public BakedNotification prepareNotificationDimensions(Notification notification) {
        NotificationIcon<?> icon = notification.getIcon();
        int maxAllowedTextWidth = icon.isNone() ? this.width - 10 : this.width - 30;
        IReorderingProcessor[] texts = Arrays.stream(notification.getAllContents())
                .map(text -> this.font.split(text, maxAllowedTextWidth))
                .flatMap(Collection::stream)
                .toArray(IReorderingProcessor[]::new);
        List<Pair<IReorderingProcessor, IReorderingProcessor>> list = new ArrayList<>();
        for (int i = 0; i < texts.length; i += 2) {
            boolean hasNext = i + 1 < texts.length;
            IReorderingProcessor p1 = texts[i];
            IReorderingProcessor p2 = hasNext ? texts[i + 1] : null;
            list.add(Pair.of(p1, p2));
        }
        return new BakedNotification(notification, list);
    }

    public void drawNotification(MatrixStack stack, float partialTicks) {
        this.updateRenderTime();
        if (notification == null)
            return;
        float appearProgress = this.notification.getAppearProgress(partialTicks);
        int notificationX = (int) (this.dockType.getStartingPositionX(this.windowX, this.windowWidth) + this.flowType.getOffsetPositionX(this.width, appearProgress));
        int notificationY = (int) (this.dockType.getStartingPositionY(this.windowY, this.windowHeight) + this.flowType.getOffsetPositionY(this.height, appearProgress));
        AbstractGui.fill(stack, notificationX - 1, notificationY - 1, notificationX + width + 1, notificationY + height + 1, 0xFFA5A5A5);
        AbstractGui.fill(stack, notificationX, notificationY, notificationX + width, notificationY + height, 0xFFC6C6C6);
        NotificationIcon<?> icon = this.notification.getIcon();
        int contentStart = 5;
        if (!icon.isNone()) {
            icon.drawIcon(stack, notificationX + 2, notificationY + 7, 16, 16);
            contentStart = 25;
        }
        float textProgress = this.notification.getTextStageProgress(partialTicks);
        float textTransparency = 1.0F;
        if (textProgress < TEXT_FADING_POINT) {
            textTransparency = TEXT_FADING_LIMIT + (1.0F - TEXT_FADING_LIMIT) * (textProgress / TEXT_FADING_LIMIT);
        } else if (textProgress > 1.0F - TEXT_FADING_POINT) {
            textTransparency = TEXT_FADING_LIMIT + (1.0F - TEXT_FADING_LIMIT) * (1.0F - (textProgress - (1.0F - TEXT_FADING_POINT) / TEXT_FADING_LIMIT));
        }
        int aText = ((int) (255 * textTransparency)) << 24;
        Pair<IReorderingProcessor, IReorderingProcessor> pair = this.notification.getRenderedText();
        ITextComponent header = this.notification.getHeader();
        this.font.draw(stack, header, notificationX + contentStart, notificationY, this.notification.isFirstOrLastStage() ? 0xFFFFFF | aText : 0xFFFFFF);
        IReorderingProcessor p1 = pair.getLeft();
        IReorderingProcessor p2 = pair.getRight();
        this.font.draw(stack, p1, notificationX + contentStart, notificationY + 10, 0xFFFFFF | aText);
        if (p2 != null) {
            this.font.draw(stack, p2, notificationX + contentStart, notificationY + 10, 0xFFFFFF | aText);
        }
    }

    public void onNotificationClicked() {
        // TODO handle click actions
        QuestingClient.CLIENT.notificationManager.forceEnqueueNextNotification();
    }

    public void setActiveNotification(Notification notification) {
        this.notification = notification != null ? this.prepareNotificationDimensions(notification) : null;
    }

    public long getRenderTime() {
        return this.lastRenderTime;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    private void updateRenderTime() {
        this.lastRenderTime = System.currentTimeMillis();
    }

    public static final class BakedNotification extends Notification {

        private final List<Pair<IReorderingProcessor, IReorderingProcessor>> visualOrder;

        public BakedNotification(Notification parent, List<Pair<IReorderingProcessor, IReorderingProcessor>> visualOrder) {
            super(parent.getIcon(), parent.getHeader(), parent.getAllContents(), parent.getRenderTimer(), parent.getAppearTimer());
            this.visualOrder = visualOrder;
            this.copyRenderingAttributes(parent);
        }

        @Override
        protected int getContentSize() {
            return visualOrder.size();
        }

        public Pair<IReorderingProcessor, IReorderingProcessor> getRenderedText() {
            return this.visualOrder.get(this.getRenderIndex());
        }
    }
}

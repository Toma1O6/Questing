package dev.toma.questing.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.client.QuestingClient;
import dev.toma.questing.common.notification.Notification;
import dev.toma.questing.common.notification.NotificationIcon;
import dev.toma.questing.common.notification.NotificationStage;
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
    private static final float TEXT_FADING_LIMIT = 0.03F;
    private final FontRenderer font;
    private final NotificationDockType dockType;
    private final NotificationFlowType flowType;
    private final int windowX, windowY, windowWidth, windowHeight;
    private final int width;
    private final int height;
    private BakedNotification notification;
    private long lastRenderTime;
    private boolean expired;

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
        this.lastRenderTime = System.currentTimeMillis() + 10000L;
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

    public void drawNotification(MatrixStack stack, float partialTicks, boolean hovered) {
        this.updateRenderTime();
        if (notification == null || isExpired())
            return;
        float appearProgress = this.notification.getAppearProgress(partialTicks);
        int notificationX = (int) (this.dockType.getStartingPositionX(this.windowX, this.windowWidth, this.width) + this.flowType.getOffsetPositionX(this.width, appearProgress));
        int notificationY = (int) (this.dockType.getStartingPositionY(this.windowY, this.windowHeight, this.height) + this.flowType.getOffsetPositionY(this.height, appearProgress));
        AbstractGui.fill(stack, notificationX, notificationY, notificationX + width, notificationY + height, 0x66 << 24);
        NotificationIcon<?> icon = this.notification.getIcon();
        int contentStart = 5;
        if (!icon.isNone()) {
            int iconSize = 16;
            int iconTop = (this.height - iconSize) / 2;
            icon.drawIcon(stack, notificationX + 2, notificationY + iconTop, iconSize, iconSize);
            contentStart = 25;
        }
        float textProgress = this.notification.getTextStageProgress(partialTicks);
        float textTransparency = 1.0F;
        if (textProgress < TEXT_FADING_POINT) {
            textTransparency = TEXT_FADING_LIMIT + (1.0F - TEXT_FADING_LIMIT) * (textProgress / TEXT_FADING_POINT);
        } else if (textProgress > 1.0F - TEXT_FADING_POINT) {
            textTransparency = TEXT_FADING_LIMIT + (1.0F - TEXT_FADING_LIMIT) * ((1.0F - textProgress) / TEXT_FADING_POINT);
        }
        int aText = ((int) (255 * textTransparency)) << 24;
        Pair<IReorderingProcessor, IReorderingProcessor> pair = this.notification.getRenderedText();
        ITextComponent header = this.notification.getHeader();
        this.font.draw(stack, header, notificationX + contentStart, notificationY + 3, 0xFFFFFF);
        IReorderingProcessor p1 = pair.getLeft();
        IReorderingProcessor p2 = pair.getRight();
        this.font.draw(stack, p1, notificationX + contentStart, notificationY + 15, 0xFFFFFF | aText);
        if (p2 != null) {
            this.font.draw(stack, p2, notificationX + contentStart, notificationY + 25, 0xFFFFFF | aText);
        }
        if (hovered) {
            AbstractGui.fill(stack, notificationX, notificationY, notificationX + width, notificationY + height, 0x44FFFFFF);
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

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public boolean isExpired() {
        return expired;
    }

    private void updateRenderTime() {
        this.lastRenderTime = System.currentTimeMillis();
    }

    public static final class BakedNotification extends Notification {

        private final Notification parent;
        private final List<Pair<IReorderingProcessor, IReorderingProcessor>> visualOrder;

        public BakedNotification(Notification parent, List<Pair<IReorderingProcessor, IReorderingProcessor>> visualOrder) {
            super(parent.getIcon(), parent.getHeader(), parent.getAllContents(), parent.getRenderTimer(), parent.getAppearTimer());
            this.parent = parent;
            this.visualOrder = visualOrder;
            this.copyRenderingAttributes(parent);
            this.parent.setDynamicContentSize(this.visualOrder.size());
        }

        @Override
        public NotificationStage getStage() {
            return parent.getStage();
        }

        @Override
        public float getAppearProgress(float partialTicks) {
            return parent.getAppearProgress(partialTicks);
        }

        @Override
        public float getTextStageProgress(float partialTicks) {
            return parent.getTextStageProgress(partialTicks);
        }

        public Pair<IReorderingProcessor, IReorderingProcessor> getRenderedText() {
            return this.visualOrder.get(parent.getRenderIndex());
        }
    }
}

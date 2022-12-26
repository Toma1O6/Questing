package dev.toma.questing.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.client.render.NotificationDockType;
import dev.toma.questing.client.render.NotificationFlowType;
import dev.toma.questing.client.render.NotificationRenderer;
import dev.toma.questing.client.render.NotificationRendererProvider;
import dev.toma.questing.common.notification.NotificationManager;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class QuestingClient {

    public static QuestingClient CLIENT = new QuestingClient();
    public final NotificationManager notificationManager = new NotificationManager();
    public final NotificationRendererProvider notificationRendererProvider = new NotificationRendererProvider();
    private NotificationRenderer renderer;

    public void construct() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        modEventBus.addListener(this::setup);
        forgeEventBus.addListener(QuestingKeyMap::handle);
        forgeEventBus.addListener(this::clientTick);
        forgeEventBus.addListener(this::renderGameOverlay);
    }

    private void setup(FMLClientSetupEvent event) {
        QuestingKeyMap.registerKeymappings();
    }

    private void clientTick(TickEvent.ClientTickEvent event) {
        Minecraft client = Minecraft.getInstance();
        if (event.phase == TickEvent.Phase.END || client.player == null) {
            return;
        }
        notificationManager.tick();
        notificationRendererProvider.tick();
    }

    private void renderGameOverlay(RenderGameOverlayEvent event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        FontRenderer font = client.font;
        MainWindow window = event.getWindow();
        int windowWidth = window.getGuiScaledWidth();
        int windowHeight = window.getGuiScaledHeight();
        if (renderer == null || renderer.getWindowWidth() != windowWidth || renderer.getWindowHeight() != windowHeight) {
            this.renderer = notificationRendererProvider.getRendererWithConfiguration(NotificationDockType.RIGHT_UPPER, NotificationFlowType.LEFT, 0, 0,
                    windowWidth, windowHeight, NotificationRendererProvider.NOTIFICATION_WIDTH, NotificationRendererProvider.NOTIFICATION_HEIGHT, font);
        }
        MatrixStack stack = event.getMatrixStack();
        float partialTicks = event.getPartialTicks();
        this.renderer.drawNotification(stack, partialTicks);
    }

    private QuestingClient() {}
}

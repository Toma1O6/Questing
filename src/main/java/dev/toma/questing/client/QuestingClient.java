package dev.toma.questing.client;

import dev.toma.questing.client.render.NotificationRendererProvider;
import dev.toma.questing.common.notification.NotificationManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class QuestingClient {

    public static QuestingClient CLIENT = new QuestingClient();
    public final NotificationManager notificationManager = new NotificationManager();
    public final NotificationRendererProvider notificationRendererProvider = new NotificationRendererProvider();

    public void construct() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        modEventBus.addListener(this::setup);
        forgeEventBus.addListener(QuestingKeyMap::handle);
        forgeEventBus.addListener(this::clientTick);
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

    private QuestingClient() {}
}

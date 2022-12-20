package dev.toma.questing.client;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class QuestingClient {

    public static QuestingClient CLIENT = new QuestingClient();

    public void construct() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        modEventBus.addListener(this::setup);
        forgeEventBus.addListener(QuestingKeyMap::handle);
    }

    private void setup(FMLClientSetupEvent event) {
        QuestingKeyMap.registerKeymappings();
    }

    private QuestingClient() {}
}

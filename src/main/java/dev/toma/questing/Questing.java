package dev.toma.questing;

import dev.toma.questing.common.command.QuestingDebugCommand;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.party.PartyManager;
import dev.toma.questing.common.party.Player2PartyManager;
import dev.toma.questing.common.party.QuestParty;
import dev.toma.questing.file.DataFileManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Map;
import java.util.UUID;

@Mod(Questing.MODID)
public final class Questing {

    public static final String MODID = "questing";
    public static final Logger LOGGER = LogManager.getLogger("Questing");
    // Logging markers
    public static final Marker MARKER_MAIN = MarkerManager.getMarker("Main");
    public static final Marker MARKER_PARTIES = MarkerManager.getMarker("Parties");
    public static final Marker MARKER_IO = MarkerManager.getMarker("IO");
    public static final Marker MARKER_AREA = MarkerManager.getMarker("Area");
    // Files
    public static final DataFileManager<Map<UUID, QuestParty>, PartyManager> PARTY_MANAGER = DataFileManager.create("parties.dat", PartyManager.CODEC, PartyManager::new);
    public static final DataFileManager<Map<UUID, UUID>, Player2PartyManager> PLAYER2PARTY_MANAGER = DataFileManager.create("player2party.dat", Player2PartyManager.CODEC, Player2PartyManager::new);

    public Questing() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        modEventBus.addListener(this::setup);
        forgeEventBus.addListener(this::registerCommands);
    }

    private void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(QuestingRegistries::register);
    }

    private void registerCommands(RegisterCommandsEvent event) {
        QuestingDebugCommand.register(event.getDispatcher());
    }
}

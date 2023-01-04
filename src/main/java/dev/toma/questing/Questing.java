package dev.toma.questing;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import dev.toma.questing.client.QuestingClient;
import dev.toma.questing.common.command.QuestingDebugCommand;
import dev.toma.questing.common.data.PlayerData;
import dev.toma.questing.common.data.PlayerDataProvider;
import dev.toma.questing.common.data.PlayerDataSynchronizationFlags;
import dev.toma.questing.common.engine.QuestEngineManager;
import dev.toma.questing.common.engine.SimpleQuestEngine;
import dev.toma.questing.common.event.PlayerLoginEventHandler;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.party.PartyManager;
import dev.toma.questing.config.QuestingConfig;
import dev.toma.questing.file.DataFileManager;
import dev.toma.questing.network.Networking;
import dev.toma.questing.utils.CapabilityDataStorage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
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
    public static final Marker MARKER = MarkerManager.getMarker("Main");
    public static final Marker MARKER_AREA = MarkerManager.getMarker("Area");
    // Files
    public static final DataFileManager<Map<UUID, Party>, PartyManager> PARTY_MANAGER = DataFileManager.create("questing/parties.dat", PartyManager.CODEC, PartyManager::new);
    public static final DataFileManager<Map<ResourceLocation, CompoundNBT>, QuestEngineManager> QUEST_MANAGER = DataFileManager.create("questing/quests.dat", QuestEngineManager.CODEC, QuestEngineManager::new);

    // Config
    public static QuestingConfig config;

    public Questing() {
        config = Configuration.registerConfig(QuestingConfig.class, ConfigFormats.properties()).getConfigInstance();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        modEventBus.addListener(this::setup);
        forgeEventBus.addListener(this::registerCommands);
        forgeEventBus.addListener(PlayerLoginEventHandler::onPlayerLoggedIn);
        forgeEventBus.addListener(PlayerLoginEventHandler::onPlayerLoggedOut);
        forgeEventBus.addListener(this::clonePlayer);
        forgeEventBus.addListener(this::changeDimension);
        forgeEventBus.addGenericListener(Entity.class, this::attachPlayerCapabilities);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> QuestingClient.CLIENT::construct);

        QUEST_MANAGER.get().registerQuestEngine(SimpleQuestEngine.IDENTIFIER, SimpleQuestEngine.CODEC, SimpleQuestEngine::new);
    }

    private void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(QuestingRegistries::register);
        Networking.Registry.registerPackets();
        CapabilityManager.INSTANCE.register(PlayerData.class, new CapabilityDataStorage<>(), PlayerData.Impl::new);
    }

    private void registerCommands(RegisterCommandsEvent event) {
        QuestingDebugCommand.register(event.getDispatcher());
    }

    private void attachPlayerCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            event.addCapability(new ResourceLocation(MODID, "player_data"), new PlayerDataProvider(player));
        }
    }

    private void changeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        PlayerDataProvider.getOptional(event.getPlayer()).ifPresent(data -> data.sendDataToClient(PlayerDataSynchronizationFlags.ALL));
    }

    private void clonePlayer(PlayerEvent.Clone event) {
        PlayerEntity newPlayer = event.getPlayer();
        PlayerEntity oldPlayer = event.getPlayer();
        PlayerDataProvider.getOptional(oldPlayer).ifPresent(oldData -> {
            CompoundNBT nbt = oldData.serializeNBT();
            PlayerDataProvider.getOptional(newPlayer).ifPresent(newData -> {
                newData.deserializeNBT(nbt);
                newData.sendDataToClient(PlayerDataSynchronizationFlags.ALL);
            });
        });
    }
}

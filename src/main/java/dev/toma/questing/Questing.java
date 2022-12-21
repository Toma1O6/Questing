package dev.toma.questing;

import dev.toma.questing.client.QuestingClient;
import dev.toma.questing.common.command.QuestingDebugCommand;
import dev.toma.questing.common.data.PlayerData;
import dev.toma.questing.common.data.PlayerDataProvider;
import dev.toma.questing.common.event.PlayerLoginEventHandler;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.party.PartyManager;
import dev.toma.questing.file.DataFileManager;
import dev.toma.questing.network.Networking;
import dev.toma.questing.utils.CapabilityDataStorage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BinaryOperator;

@Mod(Questing.MODID)
public final class Questing {

    public static final String MODID = "questing";
    public static final Logger LOGGER = LogManager.getLogger("Questing");
    // Logging markers
    public static final Marker MARKER = MarkerManager.getMarker("Main");
    public static final Marker MARKER_AREA = MarkerManager.getMarker("Area");
    // Files
    public static final DataFileManager<Map<UUID, Party>, PartyManager> PARTY_MANAGER = DataFileManager.create("parties.dat", PartyManager.CODEC, PartyManager::new);
    // Properties
    private static final Map<String, PropertyHolder<?>> PROPERTIES = new HashMap<>();

    public Questing() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        modEventBus.addListener(this::setup);
        forgeEventBus.addListener(this::registerCommands);
        forgeEventBus.addListener(PlayerLoginEventHandler::onPlayerLoggedIn);
        forgeEventBus.addGenericListener(Entity.class, this::attachPlayerCapabilities);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> QuestingClient.CLIENT::construct);
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getProperty(String key) {
        return Optional.ofNullable(PROPERTIES.get(key)).map(holder -> (T) holder.value);
    }

    @SuppressWarnings("unchecked")
    public static <T> void setProperty(String key, Object value) {
        PropertyHolder<T> propertyHolder = (PropertyHolder<T>) PROPERTIES.get(key);
        Class<?> aClass = value.getClass();
        if (!aClass.isAssignableFrom(propertyHolder.type)) {
            return;
        }
        propertyHolder.setValue((T) value);
    }

    public static <T> void registerProperty(String key, T value, Class<T> type, BinaryOperator<T> adjuster) {
        PROPERTIES.put(key, new PropertyHolder<>(adjuster, type, value));
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

    public static final class Properties {

        public static final String PARTY_SIZE = "partySize";
    }

    private static final class PropertyHolder<T> {

        private final BinaryOperator<T> adjuster;
        private final Class<T> type;
        private T value;

        private PropertyHolder(BinaryOperator<T> adjuster, Class<T> type, T initialValue) {
            this.adjuster = adjuster;
            this.type = type;
            this.setValue(initialValue);
        }

        public void setValue(T value) {
            this.value = adjuster.apply(this.value, value);
        }
    }

    static {
        registerProperty(Properties.PARTY_SIZE, 5, Integer.class, Math::max);
    }
}

package dev.toma.questing;

import dev.toma.questing.config.ConfigHandler;
import dev.toma.questing.config.QuestConfig;
import net.minecraftforge.fml.common.Mod;

@Mod(Questing.MODID)
public final class Questing {

    public static final String MODID = "questing";
    private static QuestConfig config;

    public Questing() {
        config = ConfigHandler.loadConfig(QuestConfig.class);
    }

    public static QuestConfig config() {
        return config;
    }
}

package dev.toma.questing.config;

import dev.toma.questing.Questing;

@ConfigFile(Questing.MODID)
public final class QuestConfig {

    @ConfigFile.Value("party.maxSize")
    public final int partyMaxSize = 5;

    @ConfigFile.Value("party.name.regexp")
    public final String partyName = "[a-zA-Z0-9\\s]{1,30}";

    @ConfigFile.Value("party.name.error")
    public final String partyNameError = "Party name must be between 1-30 characters long and can contain only a-z and 0-9 characters";
}

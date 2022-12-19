package dev.toma.questing.common.provider;

public interface QuestProvider {

    Options options();

    interface Options {

        int maxPartyGroupSize();
    }
}

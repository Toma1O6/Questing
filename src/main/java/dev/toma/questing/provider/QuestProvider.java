package dev.toma.questing.provider;

public interface QuestProvider {

    Options options();

    interface Options {

        int maxPartyGroupSize();
    }
}

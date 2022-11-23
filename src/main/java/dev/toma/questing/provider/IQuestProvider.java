package dev.toma.questing.provider;

public interface IQuestProvider {

    Options options();

    interface Options {

        int maxPartyGroupSize();
    }
}

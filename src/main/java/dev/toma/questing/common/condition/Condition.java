package dev.toma.questing.common.condition;

public abstract class Condition {

    private final ConditionProvider<?> provider;

    public Condition(ConditionProvider<?> provider) {
        this.provider = provider;
    }

    public final ConditionProvider<?> getProvider() {
        return this.provider;
    }

    public abstract void registerTriggerResponders(ConditionRegisterHandler registerHandler);
}

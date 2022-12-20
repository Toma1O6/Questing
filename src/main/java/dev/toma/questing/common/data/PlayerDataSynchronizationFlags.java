package dev.toma.questing.common.data;

public final class PlayerDataSynchronizationFlags {

    public static final int WILDCARD = -1;
    public static final int PARTY = 1;

    public static boolean is(int flag, int values) {
        return values == WILDCARD || (values & flag) == flag;
    }
}

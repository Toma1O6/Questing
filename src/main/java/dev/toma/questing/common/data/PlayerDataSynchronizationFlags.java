package dev.toma.questing.common.data;

public final class PlayerDataSynchronizationFlags {

    public static final int ALL = -1;
    public static final int PARTY = 1;

    public static boolean is(int flag, int values) {
        return values == ALL || (values & flag) == flag;
    }
}

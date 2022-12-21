package dev.toma.questing.common.party;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.IntSupplier;

public enum PartyPermission implements IntSupplier {

    OWNER,
    MANAGE_INVITES,
    MANAGE_MEMBERS,
    INVITE_PLAYERS,
    MANAGE_PARTY,
    USER;

    public static final Set<PartyPermission> ADJUSTABLE_PERMISSIONS = EnumSet.of(
            MANAGE_INVITES,
            MANAGE_MEMBERS,
            INVITE_PLAYERS,
            MANAGE_PARTY
    );
    private final int value;

    PartyPermission() {
        this.value = 1 << this.ordinal();
    }

    @Override
    public int getAsInt() {
        return value;
    }

    public static boolean isAllowed(PartyPermission permissionType, int userPermissions) {
        return is(OWNER, userPermissions) || is(permissionType, userPermissions);
    }

    public static boolean is(PartyPermission permission, int userPermissions) {
        return (userPermissions & permission.value) == permission.value;
    }
}

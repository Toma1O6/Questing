package dev.toma.questing.common.party;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.IntSupplier;

public enum PartyPermission implements IntSupplier {

    OWNER(4),
    MANAGE_MEMBERS(3),
    MANAGE_INVITES(2),
    INVITE_PLAYERS(1),
    MANAGE_PARTY(2),
    USER(0);

    public static final Set<PartyPermission> ADJUSTABLE_PERMISSIONS = EnumSet.of(
            MANAGE_INVITES,
            MANAGE_MEMBERS,
            INVITE_PLAYERS,
            MANAGE_PARTY
    );
    public static final PartyPermission[] ADMIN_ROLES = {
            OWNER,
            MANAGE_INVITES,
            MANAGE_MEMBERS,
            INVITE_PLAYERS,
            MANAGE_PARTY
    };
    private final int value;
    private final int permissionLevel;

    PartyPermission(int permissionLevel) {
        this.permissionLevel = permissionLevel;
        this.value = 1 << this.ordinal();
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    @Override
    public int getAsInt() {
        return value;
    }

    public static boolean is(PartyPermission permission, int userPermissions) {
        return (userPermissions & permission.value) == permission.value;
    }
}

package dev.toma.questing.common.trigger;

import com.mojang.serialization.DataResult;

import java.util.EnumSet;
import java.util.Set;

public enum ResponseType {

    OK,
    PASS,
    SKIP,
    FAIL;

    public static final Set<ResponseType> VALID_FAILURE_STATUSES = EnumSet.range(PASS, FAIL);

    public static DataResult<ResponseType> fromString(String string) {
        try {
            ResponseType response = valueOf(string.toUpperCase());
            if (response == OK) {
                return DataResult.error("Cannot use 'OK' status for failure");
            }
            return DataResult.success(response);
        } catch (IllegalArgumentException e) {
            return DataResult.error("Unknown trigger status: " + string);
        }
    }

    public ResponseType transform(ResponseType other) {
        if (other == SKIP)
            return this;
        return other.ordinal() > this.ordinal() ? other : this;
    }
}

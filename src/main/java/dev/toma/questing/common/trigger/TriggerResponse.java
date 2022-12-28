package dev.toma.questing.common.trigger;

import com.mojang.serialization.DataResult;

import java.util.EnumSet;
import java.util.Set;

public enum TriggerResponse {

    OK,
    PASS,
    SKIP,
    FAIL;

    public static final Set<TriggerResponse> VALID_FAILURE_STATUSES = EnumSet.range(PASS, FAIL);

    public static DataResult<TriggerResponse> fromString(String string) {
        try {
            TriggerResponse response = valueOf(string.toUpperCase());
            if (response == OK) {
                return DataResult.error("Cannot use 'OK' status for failure");
            }
            return DataResult.success(response);
        } catch (IllegalArgumentException e) {
            return DataResult.error("Unknown trigger status: " + string);
        }
    }
}

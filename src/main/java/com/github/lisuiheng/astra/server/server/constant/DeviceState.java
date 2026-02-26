package com.github.lisuiheng.astra.server.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeviceState {
    ACTIVE("ACTIVE"),
    PENDING("PENDING"),
    OFFLINE("OFFLINE"),
    ONLINE("ONLINE");

    private final String value;

    public static DeviceState fromValue(String value) {
        for (DeviceState state : DeviceState.values()) {
            if (state.getValue().equals(value)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
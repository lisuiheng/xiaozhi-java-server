package com.github.lisuiheng.astra.server.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SpeakerType {
    USER("USER"),
    AGENT("AGENT"),
    SYSTEM("SYSTEM");

    private final String value;

    public static SpeakerType fromValue(String value) {
        for (SpeakerType state : SpeakerType.values()) {
            if (state.getValue().equals(value)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
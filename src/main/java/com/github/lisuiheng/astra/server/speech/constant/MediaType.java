package com.github.lisuiheng.astra.server.speech.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MediaType {
    AUDIO("AUDIO"),
    TTS("TTS");

    private final String value;

    public static MediaType fromValue(String value) {
        for (MediaType type : MediaType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
package com.github.lisuiheng.astra.server.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Tts {
    BYTEDANCE("BYTEDANCE");

    private final String value;

    public static Tts fromValue(String value) {
        for (Tts state : Tts.values()) {
            if (state.getValue().equals(value)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
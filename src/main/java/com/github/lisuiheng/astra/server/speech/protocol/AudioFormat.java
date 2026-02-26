package com.github.lisuiheng.astra.server.speech.protocol;

import lombok.Getter;

/**
 * 音频格式支持
 */
@Getter
public enum AudioFormat {
    MP3("mp3", "audio/mpeg"),
    OGG_OPUS("ogg_opus", "audio/ogg"),
    PCM("pcm", "audio/pcm"),
    WAV("wav", "audio/wav");

    private final String format;
    private final String mimeType;

    AudioFormat(String format, String mimeType) {
        this.format = format;
        this.mimeType = mimeType;
    }

    public static AudioFormat fromString(String format) {
        for (AudioFormat af : AudioFormat.values()) {
            if (af.format.equalsIgnoreCase(format)) {
                return af;
            }
        }
        return MP3; // 默认返回MP3
    }
}
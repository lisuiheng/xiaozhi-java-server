package com.github.lisuiheng.astra.server.speech.protocol;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * TTS配置参数
 */
@Data
@Builder
public class TtsConfig {
    // 基础配置
    private String speaker;
    private String model;

    // 音频参数
    @Builder.Default private String audioFormat = "mp3";
    @Builder.Default private int sampleRate = 24000;
    @Builder.Default private Integer bitRate = null;
    @Builder.Default private int speechRate = 0;
    @Builder.Default private int loudnessRate = 0;

    // 高级参数
    private String emotion;
    @Builder.Default private int emotionScale = 4;
    @Builder.Default private boolean enableTimestamp = false;
    @Builder.Default private int silenceDuration = 0;
    @Builder.Default private boolean enableLanguageDetector = false;
    @Builder.Default private boolean disableMarkdownFilter = false;
    @Builder.Default private boolean disableEmojiFilter = false;
    @Builder.Default private String explicitLanguage = null;

    // 自定义参数
    private Map<String, Object> customAdditions;

    /**
     * 转换为API请求参数
     */
    public Map<String, Object> toRequestParams(String text) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("text", text);
        params.put("speaker", speaker);

        if (model != null) {
            params.put("model", model);
        }

        // 音频参数
        java.util.Map<String, Object> audioParams = new java.util.HashMap<>();
        audioParams.put("format", audioFormat);
        audioParams.put("sample_rate", sampleRate);

        if (bitRate != null) {
            audioParams.put("bit_rate", bitRate);
        }

        if (emotion != null) {
            audioParams.put("emotion", emotion);
            audioParams.put("emotion_scale", emotionScale);
        }

        audioParams.put("speech_rate", speechRate);
        audioParams.put("loudness_rate", loudnessRate);
        audioParams.put("enable_timestamp", enableTimestamp);

        params.put("audio_params", audioParams);

        // 附加参数
        java.util.Map<String, Object> additions = new java.util.HashMap<>();
        additions.put("silence_duration", silenceDuration);
        additions.put("enable_language_detector", enableLanguageDetector);
        additions.put("disable_markdown_filter", disableMarkdownFilter);
        additions.put("disable_emoji_filter", disableEmojiFilter);

        if (explicitLanguage != null) {
            additions.put("explicit_language", explicitLanguage);
        }

        if (customAdditions != null) {
            additions.putAll(customAdditions);
        }

        params.put("additions", additions);

        return params;
    }
}
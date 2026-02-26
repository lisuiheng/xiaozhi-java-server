package com.github.lisuiheng.astra.server.speech;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * TTS配置类（基于火山引擎协议）
 */
@Data
@Builder(toBuilder = true)
public class OkHttpTtsConfig {
    // 音色配置
    @Builder.Default private String speaker = "zh_female_wanwanxiaohe_moon_bigtts"; // 默认中文女声

    // 音频参数
    @Builder.Default private String audioFormat = "pcm";  // 音频格式
    @Builder.Default private int sampleRate = 24000;      // 采样率
    @Builder.Default private boolean enableTimestamp = true; // 是否启用时间戳
    
    // 其他参数
    @Builder.Default private String additions = "{\"disable_markdown_filter\":false}"; // 附加参数

    /**
     * 创建默认配置（中文语音）
     */
    public static OkHttpTtsConfig defaultChinese() {
        return OkHttpTtsConfig.builder()
                .speaker("zh_female_wanwanxiaohe_moon_bigtts")
                .audioFormat("pcm")
                .sampleRate(24000)
                .build();
    }

    /**
     * 创建默认配置（英文语音）
     */
    public static OkHttpTtsConfig defaultEnglish() {
        return OkHttpTtsConfig.builder()
                .speaker("BV700_EN")
                .audioFormat("pcm")
                .sampleRate(24000)
                .build();
    }

    /**
     * 获取会话配置（用于startSession）
     */
    public Map<String, Object> getSessionConfig() {
        Map<String, Object> config = new HashMap<>();

        // 基础配置
        config.put("speaker", speaker);

        // 音频参数
        Map<String, Object> audioParams = new HashMap<>();
        audioParams.put("format", audioFormat);
        audioParams.put("sample_rate", sampleRate);
        audioParams.put("enable_timestamp", enableTimestamp);
        config.put("audio_params", audioParams);

        // 附加参数
        config.put("additions", additions);

        return config;
    }

    /**
     * 获取TTS任务配置（用于sendText）
     */
    public Map<String, Object> getTaskConfig(String text) {
        Map<String, Object> config = getSessionConfig();
        config.put("text", text);
        return config;
    }

    /**
     * 创建音频参数映射
     */
    public Map<String, Object> getAudioParams() {
        Map<String, Object> audioParams = new HashMap<>();
        audioParams.put("format", audioFormat);
        audioParams.put("sample_rate", sampleRate);
        return audioParams;
    }
}
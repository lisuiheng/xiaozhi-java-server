package com.github.lisuiheng.astra.server.speech.example;

import com.github.lisuiheng.astra.server.speech.OkHttpTtsClient;
import com.github.lisuiheng.astra.server.speech.OkHttpTtsConfig;
import com.github.lisuiheng.astra.server.speech.config.TtsProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.util.concurrent.CompletableFuture;

/**
 * TtsProperties 使用示例
 * 展示如何正确使用 TtsProperties 配置 TTS 客户端
 */
@Slf4j
public class TtsPropertiesUsageExample {

    public static void main(String[] args) {
        try {
            // 创建并配置 TtsProperties
            TtsProperties ttsProperties = new TtsProperties();
            ttsProperties.setAppId("your-app-id");
            ttsProperties.setAccessToken("your-access-token");
            ttsProperties.setDefaultResourceId("volc.service_type.10029");
            ttsProperties.setDefaultSpeaker("zh_female_wanwanxiaohe_moon_bigtts");
            ttsProperties.setDefaultFormat("mp3");
            ttsProperties.setDefaultSampleRate(24000);

            // 使用 TtsProperties 创建客户端
            OkHttpTtsClient client = new OkHttpTtsClient(ttsProperties);

            // 按需连接模式，无需初始化
            log.info("TTS client created with TtsProperties");

            // 创建配置（使用 TtsProperties 中的默认值）
            OkHttpTtsConfig config = OkHttpTtsConfig.builder()
                .speaker(ttsProperties.getDefaultSpeaker())
                .audioFormat(ttsProperties.getDefaultFormat())
                .sampleRate(ttsProperties.getDefaultSampleRate())
                .build();

            // 合成文本
            String text = "这是使用 TtsProperties 配置的 TTS 客户端示例。TTS 服务可以将文本转换为自然流畅的语音。";
            log.info("Synthesizing text: {}", text);

            CompletableFuture<byte[]> synthesisFuture = client.synthesize(text, config);
            byte[] audioData = synthesisFuture.join();

            // 保存到文件
            try (FileOutputStream fos = new FileOutputStream("tts_properties_example.mp3")) {
                fos.write(audioData);
                log.info("Audio saved to tts_properties_example.mp3, size: {} bytes", audioData.length);
            }

            // 关闭客户端
            client.shutdown().join();
            log.info("TTS client shutdown completed");

        } catch (Exception e) {
            log.error("Error in TTS synthesis", e);
        }
    }
}
package com.github.lisuiheng.astra.server.speech.controller;

import com.github.lisuiheng.astra.server.speech.OkHttpTtsConfig;
import com.github.lisuiheng.astra.server.speech.protocol.AudioFormat;
import com.github.lisuiheng.astra.server.speech.service.TtsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/tts")
public class TtsController {

    @Autowired
    private TtsService ttsService;

    /**
     * 文本转语音接口
     *
     * @param request 包含文本和其他参数的请求体
     * @return 音频数据
     */
    @PostMapping("/synthesize")
    public ResponseEntity<byte[]> synthesize(@RequestBody Map<String, Object> request) {
        try {
            String text = (String) request.get("text");
            if (text == null || text.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // 创建TTS配置
            OkHttpTtsConfig config = createConfigFromRequest(request);

            // 合成语音
            byte[] audioData = ttsService.synthesizeText(text, config);

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(getMediaType(config.getAudioFormat()));
            headers.setContentLength(audioData.length);

            return new ResponseEntity<>(audioData, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to synthesize speech", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 健康检查接口
     *
     * @return 服务状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "initialized", ttsService.isInitialized()
        ));
    }

    /**
     * 根据请求参数创建TTS配置
     *
     * @param request 请求参数
     * @return TTS配置
     */
    @SuppressWarnings("unchecked")
    private OkHttpTtsConfig createConfigFromRequest(Map<String, Object> request) {
        OkHttpTtsConfig config = ttsService.createDefaultConfig();
        OkHttpTtsConfig.OkHttpTtsConfigBuilder builder = config.toBuilder();

        // 音频格式
        String format = (String) request.get("format");
        if (format != null) {
            builder.audioFormat(format);
        }

        // 音色
        String speaker = (String) request.get("speaker");
        if (speaker != null) {
            builder.speaker(speaker);
        }


        // 采样率
        Integer sampleRate = (Integer) request.get("sampleRate");
        if (sampleRate != null) {
            builder.sampleRate(sampleRate);
        }

        // 自定义参数
        Map<String, Object> customParams = (Map<String, Object>) request.get("customParams");
        if (customParams != null) {
            // 注意：OkHttpTtsConfig不直接支持customAdditions，我们需要通过其他方式处理
            log.warn("Custom parameters are not supported in OkHttpTtsConfig");
        }

        return builder.build();
    }

    /**
     * 根据音频格式获取媒体类型
     *
     * @param format 音频格式
     * @return 媒体类型
     */
    private MediaType getMediaType(String format) {
        AudioFormat audioFormat = AudioFormat.fromString(format);
        switch (audioFormat) {
            case MP3:
                return MediaType.valueOf("audio/mpeg");
            case OGG_OPUS:
                return MediaType.valueOf("audio/ogg");
            case PCM:
                return MediaType.valueOf("audio/pcm");
            case WAV:
                return MediaType.valueOf("audio/wav");
            default:
                return MediaType.valueOf("audio/mpeg");
        }
    }
}
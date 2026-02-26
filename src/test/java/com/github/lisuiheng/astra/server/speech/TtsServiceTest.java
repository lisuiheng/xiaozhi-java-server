package com.github.lisuiheng.astra.server.speech;

import com.github.lisuiheng.astra.server.speech.service.TtsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TtsServiceTest {

    @Autowired
    private TtsService ttsService;

    @Test
    public void testTtsServiceInitialization() {
        assertTrue(ttsService.isInitialized(), "TTS service should be initialized");
    }

    @Test
    public void testCreateDefaultConfig() {
        var config = ttsService.createDefaultConfig();
        assertNotNull(config, "Default config should not be null");
        assertEquals("S_Baidu_chixiao_1", config.getSpeaker(), "Default speaker should match");
        assertEquals("seed-tts-1.1", config.getModel(), "Default model should match");
        assertEquals("mp3", config.getAudioFormat(), "Default format should match");
        assertEquals(24000, config.getSampleRate(), "Default sample rate should match");
    }

    @Test
    public void testCreateEmotionConfig() {
        var config = ttsService.createEmotionConfig("happy");
        assertNotNull(config, "Emotion config should not be null");
        assertEquals("happy", config.getEmotion(), "Emotion should match");
        assertEquals(4, config.getEmotionScale(), "Emotion scale should match");
    }

    @Test
    public void testSynthesizeText() {
        // 仅在服务已初始化时运行此测试
        if (!ttsService.isInitialized()) {
            System.out.println("TTS service not initialized, skipping synthesis test");
            return;
        }

        try {
            String text = "你好，这是一个TTS测试。";
            byte[] audioData = ttsService.synthesizeText(text);

            assertNotNull(audioData, "Audio data should not be null");
            assertTrue(audioData.length > 0, "Audio data should not be empty");

            // 将音频数据保存到文件以供验证（可选）
            Path outputPath = Path.of("test_output.mp3");
            Files.write(outputPath, audioData);
            System.out.println("Audio saved to: " + outputPath.toAbsolutePath());

            // 验证文件确实被创建且不为空
            assertTrue(Files.exists(outputPath), "Output file should exist");
            assertTrue(Files.size(outputPath) > 0, "Output file should not be empty");

            // 清理测试文件
            Files.deleteIfExists(outputPath);
        } catch (Exception e) {
            fail("Text synthesis failed: " + e.getMessage());
        }
    }
}
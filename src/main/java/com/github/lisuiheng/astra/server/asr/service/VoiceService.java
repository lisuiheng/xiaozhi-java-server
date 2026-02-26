package com.github.lisuiheng.astra.server.asr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoiceService {
    
    private final RestTemplate restTemplate;
    
    @Value("${app.audio.upload-dir:/tmp/audio}")
    private String uploadDir;
    
    public String speechToText(MultipartFile audioFile) {
        try {
            // 保存临时文件
            File tempFile = saveTempFile(audioFile);
            
            // 调用语音识别服务 (示例使用模拟服务)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(tempFile));
            body.add("model", "whisper-1");
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            // 这里应该调用实际的ASR服务
            String asrServiceUrl = "https://api.openai.com/v1/audio/transcriptions";
            // ResponseEntity<ASRResponse> response = restTemplate.postForEntity(asrServiceUrl, requestEntity, ASRResponse.class);
            
            // 清理临时文件
            tempFile.delete();
            
            // 返回识别的文本 (模拟)
            return "这是从语音识别得到的文本内容";
            
        } catch (IOException e) {
            log.error("语音识别失败", e);
            throw new RuntimeException("语音识别处理失败", e);
        }
    }
    
    public Resource textToSpeech(String text, String voice) {
        try {
            // 调用TTS服务 (示例)
            String ttsServiceUrl = "https://api.openai.com/v1/audio/speech";
            
            // TTS请求体
            Map<String, Object> requestBody = Map.of(
                "model", "tts-1",
                "input", text,
                "voice", voice != null ? voice : "alloy"
            );
            
            // 调用TTS服务并返回音频流
            // ResponseEntity<byte[]> response = restTemplate.postForEntity(ttsServiceUrl, requestBody, byte[].class);
            
            // 保存为临时文件并返回
            File audioFile = File.createTempFile("tts_", ".mp3");
            // Files.write(audioFile.toPath(), response.getBody());
            
            return new FileSystemResource(audioFile);
            
        } catch (Exception e) {
            log.error("语音合成失败", e);
            throw new RuntimeException("语音合成失败", e);
        }
    }
    
    private File saveTempFile(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File tempFile = new File(uploadDir, filename);
        
        // 确保目录存在
        tempFile.getParentFile().mkdirs();
        
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }
        
        return tempFile;
    }
}
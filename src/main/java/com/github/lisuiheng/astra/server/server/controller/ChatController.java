package com.github.lisuiheng.astra.server.server.controller;

import com.github.lisuiheng.astra.server.server.model.dto.ChatRequest;
import com.github.lisuiheng.astra.server.server.model.dto.ChatResponse;
import com.github.lisuiheng.astra.server.ai.service.AIChatService;
import com.github.lisuiheng.astra.server.asr.service.VoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    
    private final AIChatService aiChatService;
    private final VoiceService voiceService;
    
    @PostMapping("/message")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        log.info("收到聊天请求，会话: {}, 用户: {}", request.getSessionId(), request.getUserId());
        
        ChatResponse response = aiChatService.processChat(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/voice/transcribe")
    public ResponseEntity<String> transcribeVoice(@RequestParam("file") MultipartFile audioFile) {
        log.info("收到语音转录请求，文件大小: {} bytes", audioFile.getSize());
        
        String transcribedText = voiceService.speechToText(audioFile);
        return ResponseEntity.ok(transcribedText);
    }
    
    @PostMapping("/voice/synthesize")
    public ResponseEntity<ChatResponse> voiceChat(
            @RequestParam(value = "file", required = false) MultipartFile audioFile,
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "sessionId") String sessionId) {
        
        String inputText;
        if (audioFile != null) {
            // 语音输入
            inputText = voiceService.speechToText(audioFile);
        } else if (text != null) {
            // 文本输入
            inputText = text;
        } else {
            return ResponseEntity.badRequest().build();
        }
        
        ChatRequest request = new ChatRequest();
        request.setMessage(inputText);
        request.setSessionId(sessionId);
        request.setUserId("voice_user"); // 实际应从认证获取
        request.getAudioConfig().setNeedTTS(true);
        
        ChatResponse response = aiChatService.processChat(request);
        return ResponseEntity.ok(response);
    }
}
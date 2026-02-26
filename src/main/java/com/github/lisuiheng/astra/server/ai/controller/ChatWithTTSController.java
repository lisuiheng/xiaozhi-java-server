package com.github.lisuiheng.astra.server.ai.controller;

//import com.github.lisuiheng.astra.server.ai.model.dto.StreamWithTTSResponse;
//import com.github.lisuiheng.astra.server.ai.service.StreamingChatWithTTSService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.codec.ServerSentEvent;
//import org.springframework.web.bind.annotation.*;
//
//import reactor.core.publisher.Flux;
//
///**
// * 流式聊天TTS控制器
// */
//@RestController
//@RequestMapping("/api/v1/chat")
//@RequiredArgsConstructor
//@Slf4j
//public class ChatWithTTSController {
//
//    private final StreamingChatWithTTSService streamingChatWithTTSService;
//
//    /**
//     * 流式聊天带TTS（SSE格式）
//     */
//    @GetMapping(value = "/stream-tts", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<ServerSentEvent<StreamWithTTSResponse>> chatWithTTS(
//            @RequestParam String agentId,
//            @RequestParam String userId,
//            @RequestParam String message) {
//
//        log.info("Streaming chat with TTS - agentId: {}, userId: {}, message: {}",
//                agentId, userId, message);
//
//        return streamingChatWithTTSService.chatWithTTS(agentId, userId, message, true)
//                .map(response -> ServerSentEvent.builder(response).build())
//                .doOnError(error -> log.error("Stream error", error))
//                .doOnComplete(() -> log.info("Stream completed"))
//                .doOnCancel(() -> log.info("Stream cancelled"));
//    }
//
//    /**
//     * 流式聊天带TTS（二进制流，用于音频）
//     */
//    @GetMapping(value = "/stream-tts/audio", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
//    public Flux<byte[]> chatWithTTSAudio(
//            @RequestParam String agentId,
//            @RequestParam String userId,
//            @RequestParam String message) {
//
//        return streamingChatWithTTSService.chatWithTTS(agentId, userId, message, true)
//                .filter(response -> response.getType() == StreamWithTTSResponse.ResponseType.AUDIO)
//                .map(StreamWithTTSResponse::getAudio);
//    }
//
//    /**
//     * 中断流式会话
//     */
//    @PostMapping("/stream-tts/interrupt")
//    public ResponseEntity<Void> interruptStream(
//            @RequestParam String agentId,
//            @RequestParam String userId) {
//
//        streamingChatWithTTSService.interruptStream(agentId, userId);
//        return ResponseEntity.ok().build();
//    }
//
//    /**
//     * WebSocket端点（可选）
//     */
//    @GetMapping("/stream-tts/ws")
//    public String getWebSocketEndpoint() {
//        // 返回WebSocket连接信息
//        return "ws://localhost:8080/ws/chat-tts";
//    }
//}
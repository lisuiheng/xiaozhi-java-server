package com.github.lisuiheng.astra.server.ai.websocket;

//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.github.lisuiheng.astra.server.ai.model.dto.StreamWithTTSResponse;
//import com.github.lisuiheng.astra.server.ai.service.StreamingChatWithTTSService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.socket.WebSocketHandler;
//import org.springframework.web.reactive.socket.WebSocketMessage;
//import org.springframework.web.reactive.socket.WebSocketSession;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class ChatTTSWebSocketHandler implements WebSocketHandler {
//
//    private final StreamingChatWithTTSService streamingChatWithTTSService;
//    private final ObjectMapper objectMapper;
//
//    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
//
//    @Override
//    public Mono<Void> handle(WebSocketSession session) {
//        String sessionId = session.getId();
//        sessions.put(sessionId, session);
//
//        log.info("WebSocket connected: {}", sessionId);
//
//        // 处理接收到的消息
//        Flux<Void> input = session.receive()
//                .map(WebSocketMessage::getPayloadAsText)
//                .flatMap(message -> handleMessage(sessionId, message))
//                .doOnError(error -> log.error("WebSocket error", error))
//                .doOnComplete(() -> {
//                    log.info("WebSocket completed: {}", sessionId);
//                    sessions.remove(sessionId);
//                });
//
//        // 发送欢迎消息
//        WebSocketMessage welcomeMsg = session.textMessage(
//                "{\"type\":\"CONNECTED\",\"sessionId\":\"" + sessionId + "\"}"
//        );
//
//        return session.send(Mono.just(welcomeMsg))
//                .and(input)
//                .doFinally(signal -> {
//                    log.info("WebSocket disconnected: {}", sessionId);
//                    sessions.remove(sessionId);
//                });
//    }
//
//    private Mono<Void> handleMessage(String sessionId, String message) {
//        try {
//            Map<String, Object> msg = objectMapper.readValue(message, Map.class);
//            String type = (String) msg.get("type");
//
//            switch (type) {
//                case "CHAT":
//                    return handleChatMessage(sessionId, msg);
//                case "INTERRUPT":
//                    return handleInterruptMessage(sessionId, msg);
//                default:
//                    log.warn("Unknown message type: {}", type);
//                    return Mono.empty();
//            }
//
//        } catch (Exception e) {
//            log.error("Failed to parse message", e);
//            return Mono.empty();
//        }
//    }
//
//    private Mono<Void> handleChatMessage(String sessionId, Map<String, Object> msg) {
//        String agentId = (String) msg.get("agentId");
//        String userId = (String) msg.get("userId");
//        String message = (String) msg.get("message");
//
//        if (agentId == null || userId == null || message == null) {
//            return sendError(sessionId, "Missing required fields");
//        }
//
//        WebSocketSession wsSession = sessions.get(sessionId);
//        if (wsSession == null) {
//            return Mono.empty();
//        }
//
//        // 获取聊天流
//        Flux<StreamWithTTSResponse> chatStream =
//                streamingChatWithTTSService.chatWithTTS(agentId, userId, message, true);
//
//        // 发送响应到WebSocket
//        return wsSession.send(
//                chatStream.map(response -> {
//                    try {
//                        String json = objectMapper.writeValueAsString(response);
//                        return wsSession.textMessage(json);
//                    } catch (Exception e) {
//                        log.error("Failed to serialize response", e);
//                        return wsSession.textMessage("{\"type\":\"ERROR\",\"error\":\"Serialization error\"}");
//                    }
//                })
//        );
//    }
//
//    private Mono<Void> handleInterruptMessage(String sessionId, Map<String, Object> msg) {
//        String agentId = (String) msg.get("agentId");
//        String userId = (String) msg.get("userId");
//
//        if (agentId == null || userId == null) {
//            return sendError(sessionId, "Missing agentId or userId");
//        }
//
//        streamingChatWithTTSService.interruptStream(agentId, userId);
//        return sendMessage(sessionId, "{\"type\":\"INTERRUPTED\"}");
//    }
//
//    private Mono<Void> sendMessage(String sessionId, String message) {
//        WebSocketSession wsSession = sessions.get(sessionId);
//        if (wsSession != null) {
//            return wsSession.send(Mono.just(wsSession.textMessage(message)));
//        }
//        return Mono.empty();
//    }
//
//    private Mono<Void> sendError(String sessionId, String error) {
//        return sendMessage(sessionId,
//                String.format("{\"type\":\"ERROR\",\"error\":\"%s\"}", error));
//    }
//}
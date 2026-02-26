package com.github.lisuiheng.astra.server.speech.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lisuiheng.astra.server.speech.OkHttpTtsConfig;
import com.github.lisuiheng.astra.server.speech.service.TtsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TtsWebSocketHandler extends AbstractWebSocketHandler {

    @Autowired
    private TtsService ttsService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        log.info("TTS WebSocket connection established: {}", session.getId());

        // 发送连接成功的消息
        Map<String, Object> response = Map.of(
            "type", "CONNECTED",
            "sessionId", session.getId()
        );
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            Map<String, Object> request = objectMapper.readValue(message.getPayload(), Map.class);
            String type = (String) request.get("type");

            switch (type) {
                case "SYNTHESIZE":
                    handleSynthesizeRequest(session, request);
                    break;
                case "STREAM_START":
                    handleStreamStartRequest(session, request);
                    break;
                case "STREAM_DATA":
                    handleStreamDataRequest(session, request);
                    break;
                case "STREAM_END":
                    handleStreamEndRequest(session, request);
                    break;
                default:
                    sendErrorResponse(session, "Unknown message type: " + type);
            }
        } catch (Exception e) {
            log.error("Error handling WebSocket message", e);
            sendErrorResponse(session, "Error processing request: " + e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        log.info("TTS WebSocket connection closed: {} with status: {}", session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("TTS WebSocket transport error: {}", session.getId(), exception);
        sessions.remove(session.getId());
    }

    private void handleSynthesizeRequest(WebSocketSession session, Map<String, Object> request) throws Exception {
        String text = (String) request.get("text");
        if (text == null || text.isEmpty()) {
            sendErrorResponse(session, "Text is required");
            return;
        }

        // 创建TTS配置
        OkHttpTtsConfig config = createConfigFromRequest(request);

        // 合成语音
        byte[] audioData = ttsService.synthesizeText(text, config);

        // 发送音频数据
        session.sendMessage(new BinaryMessage(audioData));

        // 发送完成消息
        Map<String, Object> response = Map.of(
            "type", "SYNTHESIS_COMPLETE",
            "text", text
        );
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    private void handleStreamStartRequest(WebSocketSession session, Map<String, Object> request) throws Exception {
        // 在流式场景中，可能需要创建一个会话来跟踪流式合成
        String streamId = (String) request.get("streamId");
        if (streamId == null) {
            sendErrorResponse(session, "Stream ID is required");
            return;
        }

        // 创建TTS配置
        OkHttpTtsConfig config = createConfigFromRequest(request);

        // 在实际实现中，我们会在这里创建一个流式会话并存储它
        // 为了简化，我们只是发送确认消息
        Map<String, Object> response = Map.of(
            "type", "STREAM_STARTED",
            "streamId", streamId
        );
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    private void handleStreamDataRequest(WebSocketSession session, Map<String, Object> request) throws Exception {
        String streamId = (String) request.get("streamId");
        String text = (String) request.get("text");

        if (streamId == null || text == null) {
            sendErrorResponse(session, "Stream ID and text are required");
            return;
        }

        // 创建TTS配置
        OkHttpTtsConfig config = createConfigFromRequest(request);

        // 合成语音
        byte[] audioData = ttsService.synthesizeText(text, config);

        // 发送音频数据
        session.sendMessage(new BinaryMessage(audioData));

        // 发送确认消息
        Map<String, Object> response = Map.of(
            "type", "STREAM_DATA_SENT",
            "streamId", streamId,
            "textLength", text.length()
        );
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    private void handleStreamEndRequest(WebSocketSession session, Map<String, Object> request) throws Exception {
        String streamId = (String) request.get("streamId");
        if (streamId == null) {
            sendErrorResponse(session, "Stream ID is required");
            return;
        }

        // 发送流结束消息
        Map<String, Object> response = Map.of(
            "type", "STREAM_ENDED",
            "streamId", streamId
        );
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

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

    private void sendErrorResponse(WebSocketSession session, String errorMessage) throws Exception {
        Map<String, Object> response = Map.of(
            "type", "ERROR",
            "message", errorMessage
        );
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }
}
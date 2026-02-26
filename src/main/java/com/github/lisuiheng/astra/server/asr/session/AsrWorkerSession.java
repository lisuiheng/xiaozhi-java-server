package com.github.lisuiheng.astra.server.asr.session;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.BinaryMessage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class AsrWorkerSession {
    @Getter
    private final WebSocketSession webSocketSession;
    private final ObjectMapper objectMapper;
    @Getter
    private final String sessionId;
    private final long connectTime;

    public AsrWorkerSession(String sessionId, ObjectMapper objectMapper, WebSocketSession webSocketSession) {
        this.objectMapper = objectMapper;
        this.webSocketSession = webSocketSession;
        this.connectTime = System.currentTimeMillis();
        this.sessionId = sessionId;

        log.info("ASR Worker会话已建立 - SessionID: {}, RemoteAddress: {}, Protocol: {}",
                sessionId,
                webSocketSession.getRemoteAddress(),
                webSocketSession.getAcceptedProtocol());
    }

    /**
     * 发送音频数据
     */
    public void sendAudio(byte[] pcmBytes) throws IOException {
        if (!webSocketSession.isOpen()) {
            log.warn("尝试发送音频数据但会话已关闭 - SessionID: {}", this.sessionId);
            throw new IllegalStateException("WebSocket session is closed");
        }

        ByteBuffer lengthBuffer = ByteBuffer.allocate(4)
                .order(ByteOrder.BIG_ENDIAN)
                .putInt(pcmBytes.length);
        byte[] opusDataLengthBuffer = lengthBuffer.array();

        byte[] sessionIdBytes = sessionId.getBytes(StandardCharsets.UTF_8);
        ByteBuffer sessionIdLengthBuffer = ByteBuffer.allocate(4)
                .order(ByteOrder.BIG_ENDIAN)
                .putInt(sessionIdBytes.length);
        byte[] sessionIdBuffer = new byte[4 + sessionIdBytes.length];
        System.arraycopy(sessionIdLengthBuffer.array(), 0, sessionIdBuffer, 0, 4);
        System.arraycopy(sessionIdBytes, 0, sessionIdBuffer, 4, sessionIdBytes.length);

        ByteBuffer finalBuffer = ByteBuffer.allocate(sessionIdBuffer.length + opusDataLengthBuffer.length + pcmBytes.length);
        finalBuffer.put(sessionIdBuffer);
        finalBuffer.put(opusDataLengthBuffer);
        finalBuffer.put(pcmBytes);
        byte[] finalData = finalBuffer.array();

        try {
            webSocketSession.sendMessage(new BinaryMessage(finalData));
            log.debug("音频数据发送成功 WebSocketSessionID {} - SessionID: {}, DataSize: {} bytes", webSocketSession.getId(),
                    this.sessionId, pcmBytes.length);
        } catch (IOException e) {
            log.error("音频数据发送失败 - SessionID: {}", this.sessionId, e);
            throw e;
        }
    }

    /**
     * 发送JSON数据
     */
    public void sendJson(Object json) throws IOException {
        if (!webSocketSession.isOpen()) {
            log.warn("尝试发送JSON但会话已关闭 - SessionID: {}", sessionId);
            throw new IllegalStateException("WebSocket session is closed");
        }

        try {
            String jsonString = objectMapper.writeValueAsString(json);
            log.debug("发送JSON数据 - SessionID: {}, Content: {}",
                    sessionId, jsonString);

            webSocketSession.sendMessage(new TextMessage(jsonString));
        } catch (IOException e) {
            log.error("JSON数据发送失败 - SessionID: {}", sessionId, e);
            throw e;
        }
    }

    /**
     * 结束会话
     */
    public void finish() {
        try {
            log.info("结束ASR Worker会话 - SessionID: {}", sessionId);
            sendJson(java.util.Map.of("type", "finish"));

            long duration = System.currentTimeMillis() - connectTime;
            log.info("会话已关闭 - SessionID: {}, Duration: {}ms",
                    sessionId, duration);

            webSocketSession.close();
        } catch (IOException e) {
            log.error("关闭会话时出错 - SessionID: {}", sessionId, e);
        }
    }

    /**
     * 处理连接断开
     */
    public void onDisconnect() {
        long duration = System.currentTimeMillis() - connectTime;
        log.info("ASR Worker会话异常断开 - SessionID: {}, Duration: {}ms",
                sessionId, duration);
    }
}
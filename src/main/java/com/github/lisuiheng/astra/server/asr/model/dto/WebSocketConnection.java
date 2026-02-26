package com.github.lisuiheng.astra.server.asr.model.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lisuiheng.astra.server.server.model.entity.DeviceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class WebSocketConnection extends GenericConnection {
    private final AtomicLong sendCount = new AtomicLong(0);
    private final AtomicLong totalBytes = new AtomicLong(0);
    private volatile long lastLogTime = System.currentTimeMillis();
    private static final long LOG_INTERVAL_MS = 1000; // 1
    private final WebSocketSession webSocketSession;

    public WebSocketConnection(
            ObjectMapper objectMapper,
            DeviceInfo deviceInfo,
            String sessionId,
            WebSocketSession webSocketSession) {
        super(objectMapper, deviceInfo, sessionId);
        this.webSocketSession = webSocketSession;
    }

    @Override
    public void sendMessage(Object message) {
        try {
            if (webSocketSession.isOpen()) {
                String payload = (message instanceof String)
                        ? (String) message
                        : objectMapper.writeValueAsString(message);
                webSocketSession.sendMessage(new TextMessage(payload));
            }
        } catch (Exception e) {
            log.error("发送WebSocket消息失败 | 会话ID: {}", sessionId, e);
        }
    }


    @Override
    public void sendBinary(byte[] data) throws IOException {
        try {
            if (webSocketSession.isOpen()) {
                // 统计信息
                sendCount.incrementAndGet();
                totalBytes.addAndGet(data.length);

                // 打印频率（每秒）
                long currentTime = System.currentTimeMillis();
                long elapsed = currentTime - lastLogTime;

                if (elapsed >= LOG_INTERVAL_MS) {
                    synchronized (this) {
                        // 再次检查，避免多线程重复打印
                        long now = System.currentTimeMillis();
                        if (now - lastLogTime >= LOG_INTERVAL_MS) {
                            long count = sendCount.get();
                            long bytes = totalBytes.get();
                            double frequency = (count * 1000.0) / elapsed;
                            double throughput = (bytes * 1000.0) / elapsed / 1024; // KB/s

                            log.info("发送统计 | 会话: {} | 频率: {}次/秒 | 吞吐: {} KB/s | 总次数: {} | 总字节: {}",
                                    sessionId, frequency, throughput, count, bytes);

                            // 重置计数器
                            sendCount.set(0);
                            totalBytes.set(0);
                            lastLogTime = now;
                        }
                    }
                }

                webSocketSession.sendMessage(new BinaryMessage(data));
            } else {
                throw new IOException("WebSocket连接已关闭");
            }
        } catch (Exception e) {
            log.error("发送WebSocket二进制消息(byte[])失败 | 会话ID: {} | 数据长度: {}", sessionId, data.length, e);
            throw new IOException("发送二进制消息失败", e);
        }
    }

    @Override
    public void close() {
        try {
            if (webSocketSession.isOpen()) {
                webSocketSession.close();
            }
        } catch (IOException e) {
            log.error("关闭WebSocket连接失败 | 会话ID: {}", sessionId, e);
        }
    }

    @Override
    public boolean isOpen() {
        return webSocketSession != null && webSocketSession.isOpen();
    }

    @Override
    public ProtocolType getProtocolType() {
        return ProtocolType.WEBSOCKET;
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }
}
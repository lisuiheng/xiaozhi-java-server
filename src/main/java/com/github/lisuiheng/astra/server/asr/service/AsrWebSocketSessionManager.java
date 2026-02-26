package com.github.lisuiheng.astra.server.asr.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lisuiheng.astra.server.asr.session.AsrWorkerSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class AsrWebSocketSessionManager {
    @Autowired
    private ObjectMapper objectMapper;

    // 存储基础WebSocket会话
    private final ConcurrentHashMap<String, WebSocketSession> baseSessions = new ConcurrentHashMap<>();
    // 存储工作会话
    private final ConcurrentHashMap<String, AsrWorkerSession> workerSessions = new ConcurrentHashMap<>();


    public void addSession(String sessionId, WebSocketSession session) {
        baseSessions.put(sessionId, session);
        log.info("添加基础ASR会话 | ID: {} | 当前基础会话数: {} | 协议: {}",
                sessionId,
                baseSessions.size(),
                session.getAcceptedProtocol());
    }

    public void removeSession(String sessionId) {
        // 先移除工作会话
        AsrWorkerSession workerSession = workerSessions.remove(sessionId);
        if (workerSession != null) {
            log.info("移除关联的工作会话 | 工作会话ID: {}", sessionId);
        }

        // 再移除基础会话
        WebSocketSession removed = baseSessions.remove(sessionId);
        if (removed != null) {
            log.info("移除基础ASR会话 | ID: {} | 剩余基础会话数: {} | 关闭状态: {}",
                    sessionId,
                    baseSessions.size(),
                    removed.isOpen() ? "主动关闭" : "已断开");
        }
    }

    public WebSocketSession getBaseSession(String sessionId) {
        WebSocketSession session = baseSessions.get(sessionId);
        log.debug("查询基础ASR会话 | ID: {} | 存在: {} | 状态: {}",
                sessionId,
                session != null,
                session != null ? (session.isOpen() ? "已连接" : "已断开") : "不存在");
        return session;
    }

    public AsrWorkerSession getWorkerSession(String sessionId) {
        return workerSessions.get(sessionId);
    }

    public AsrWorkerSession getRandomWorkerSession() {
        if (workerSessions.isEmpty()) {
            log.warn("尝试获取随机工作会话失败 | 原因: 无可用工作会话");
            return null;
        }

        List<AsrWorkerSession> workerSessionList = List.copyOf(workerSessions.values());
        int randomIndex = ThreadLocalRandom.current().nextInt(workerSessionList.size());
        AsrWorkerSession selected = workerSessionList.get(randomIndex);

        log.debug("随机选择ASR工作会话 | 候选数量: {} | 选中ID: {}",
                workerSessionList.size(),
                selected.getSessionId());

        return selected;
    }

    public void sendObjectMessage(String sessionId, Object message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            sendMessage(sessionId, json);
        } catch (IOException e) {
            log.error("发送消息失败", e);
        }
    }

    /**
     * 发送WebSocket消息（核心发送逻辑）
     */
    public void sendMessage(String sessionId, String jsonMessage) {
        try {
            AsrWorkerSession workerSession = getValidWorkerSession(sessionId);
            WebSocketSession asrSession = workerSession.getWebSocketSession();
            synchronized (asrSession) { // 线程安全控制
                if (asrSession.isOpen()) {
                    asrSession.sendMessage(new TextMessage(jsonMessage));
                    log.info("发送消息成功 | 会话ID: {} | 消息: {}",
                            sessionId, jsonMessage);
                } else {
                    throw new IllegalStateException("WebSocket连接已关闭");
                }
            }
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("消息序列化失败: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new IllegalStateException("消息发送失败: " + e.getMessage(), e);
        }
    }


    /**
     * 校验并获取有效的工作会话
     */
    private AsrWorkerSession getValidWorkerSession(String sessionId) {
        AsrWorkerSession workerSession = workerSessions.get(sessionId);
        if (workerSession == null) {
            throw new IllegalStateException("工作会话不存在: " + sessionId);
        }
        if (workerSession.getWebSocketSession() == null) {
            throw new IllegalStateException("关联的基础会话为空");
        }
        return workerSession;
    }

    public WebSocketSession getRandomBaseSession() {
        if (baseSessions.isEmpty()) {
            log.warn("尝试获取随机基础会话失败 | 原因: 无可用会话");
            return null;
        }

        List<WebSocketSession> sessionList = List.copyOf(baseSessions.values());
        int randomIndex = ThreadLocalRandom.current().nextInt(sessionList.size());
        WebSocketSession selected = sessionList.get(randomIndex);

        log.debug("随机选择基础ASR会话 | 候选数量: {} | 选中ID: {} | 状态: {}",
                sessionList.size(),
                selected.getId(),
                selected.isOpen() ? "健康" : "异常");

        return selected;
    }

    /**
     * 注册新的ASR工作会话
     * @param sessionId 要注册的基础会话ID
     * @return 新创建的AsrWorkerSession实例，如果基础会话不存在或已关闭则返回null
     */
    public AsrWorkerSession registerWorkerSession(String sessionId) {
        // 检查是否已存在工作会话
        if (workerSessions.containsKey(sessionId)) {
            log.warn("注册ASR工作会话失败 | 原因: 工作会话已存在 | 会话ID: {}", sessionId);
            return workerSessions.get(sessionId);
        }

        WebSocketSession baseSession = getRandomBaseSession();
        if (baseSession == null) {
            log.error("注册ASR工作会话失败 | 原因: 基础会话不存在 | 会话ID: {}", sessionId);
            return null;
        }

        if (!baseSession.isOpen()) {
            log.error("注册ASR工作会话失败 | 原因: 基础会话已关闭 | 会话ID: {}", sessionId);
            return null;
        }

        AsrWorkerSession workerSession = new AsrWorkerSession(sessionId, objectMapper, baseSession);
        workerSessions.put(sessionId, workerSession);

        log.info("注册ASR工作会话成功 | 基础会话ID: {} | 工作会话ID: {} | 当前工作会话数: {}",
                sessionId,
                workerSession.getSessionId(),
                workerSessions.size());

        return workerSession;
    }

    /**
     * 注销工作会话
     * @param sessionId 要注销的工作会话ID
     * @return 是否成功注销
     */
    public boolean unregisterWorkerSession(String sessionId) {
        AsrWorkerSession removed = workerSessions.remove(sessionId);
        if (removed != null) {
            log.info("注销ASR工作会话成功 | 会话ID: {} | 剩余工作会话数: {}",
                    sessionId, workerSessions.size());
            return true;
        }
        log.warn("注销ASR工作会话失败 | 原因: 工作会话不存在 | 会话ID: {}", sessionId);
        return false;
    }
}
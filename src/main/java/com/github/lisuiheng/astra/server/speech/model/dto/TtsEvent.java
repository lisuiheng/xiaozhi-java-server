package com.github.lisuiheng.astra.server.speech.model.dto;

import com.github.lisuiheng.astra.server.speech.protocol.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * TTS 事件 JavaBean
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TtsEvent {
    /**
     * 事件类型
     */
    private EventType eventType;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 连接ID
     */
    private String connectId;

    /**
     * 是否为系统事件
     */
    private boolean system;

    /**
     * 事件数据
     */
    private Object data;

    /**
     * 原始消息（可选）
     */
    private Map<String, Object> rawMessage;

    /**
     * 错误代码（仅当事件类型为错误时有效）
     */
    private Integer errorCode;

    /**
     * 错误消息
     */
    private String errorMessage;

    /**
     * 时间戳
     */
    @Builder.Default
    private long timestamp = System.currentTimeMillis();

    // 便捷方法

    public boolean isAudioEvent() {
        return eventType == EventType.TTS_RESPONSE;
    }

    public boolean isSessionStarted() {
        return eventType == EventType.SESSION_STARTED;
    }

    public boolean isSessionFinished() {
        return eventType == EventType.SESSION_FINISHED;
    }

    public boolean isConnectionStarted() {
        return eventType == EventType.CONNECTION_STARTED;
    }

    public boolean isConnectionFinished() {
        return eventType == EventType.CONNECTION_FINISHED;
    }

    public boolean isErrorEvent() {
        return eventType == EventType.SESSION_FAILED ||
               eventType == EventType.CONNECTION_FAILED ||
               errorCode != null;
    }
}
package com.github.lisuiheng.astra.server.asr.model.dto.outbound;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TtsOutbound implements OutboundMessage  {
    private String sessionId;
    private String type; // "tts"
    private String state; // "start", "stop", "sentence_start", "sentence_end", "subsentence_start", "subsentence_end", "sentence_complete", "sentence_timeout"
    private String text;
    private String messageId;
    private String sentenceId; // 新增：句子ID，用于跟踪
    private Map<String, Object> metadata; // 新增：元数据，如子句子索引等

    // 构造方法
    public TtsOutbound() {
        this.metadata = new HashMap<>();
    }

    // 添加元数据
    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
}
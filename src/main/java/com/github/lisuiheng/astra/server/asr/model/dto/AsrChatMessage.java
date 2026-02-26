package com.github.lisuiheng.astra.server.asr.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AsrChatMessage {
    private String type;
    @JsonProperty("session_id")
    private String sessionId;
    private String content;
    private List<List<Float>> embedding; // 假设 embedding 是 float 数组或 List<Float>
}

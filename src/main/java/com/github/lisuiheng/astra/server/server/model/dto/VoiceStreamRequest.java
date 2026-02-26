package com.github.lisuiheng.astra.server.server.model.dto;

import lombok.Data;

@Data
public class VoiceStreamRequest {
    private String sessionId;
    private String audioFormat;
    private byte[] audioData;
    private Boolean streamResponse;
}
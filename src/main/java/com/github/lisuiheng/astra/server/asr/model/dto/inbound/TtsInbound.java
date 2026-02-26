package com.github.lisuiheng.astra.server.asr.model.dto.inbound;


import lombok.Data;

@Data
public class TtsInbound extends InboundMessage {
    private final String type = "tts";
    private String command; // "stop" 等
}

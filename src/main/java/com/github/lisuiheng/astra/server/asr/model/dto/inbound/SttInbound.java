package com.github.lisuiheng.astra.server.asr.model.dto.inbound;


import lombok.Data;

@Data
public class SttInbound extends InboundMessage {
    private final String type = "stt";
    private String text;
}
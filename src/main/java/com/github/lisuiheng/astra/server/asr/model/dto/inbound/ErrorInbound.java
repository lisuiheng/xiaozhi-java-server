package com.github.lisuiheng.astra.server.asr.model.dto.inbound;


import lombok.Data;

@Data
public class ErrorInbound extends InboundMessage {
    private final String type = "error";
    private String message;
    private String status;
}
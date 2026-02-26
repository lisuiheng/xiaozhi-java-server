package com.github.lisuiheng.astra.server.asr.model.dto.inbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.lisuiheng.astra.server.asr.model.dto.AudioParams;
import lombok.Data;

@Data
public class HelloInbound extends InboundMessage {
    private final String type = "hello";
    private int version;
    private String transport;
    @JsonProperty("audio_params")
    private AudioParams audioParams;

}
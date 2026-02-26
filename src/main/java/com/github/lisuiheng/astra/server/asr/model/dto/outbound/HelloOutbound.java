package com.github.lisuiheng.astra.server.asr.model.dto.outbound;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.lisuiheng.astra.server.asr.model.dto.AudioParams;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HelloOutbound implements OutboundMessage {
    private final String type = "hello";
    private int version;
    private String transport;
    @JsonProperty("session_id")
    private String sessionId;
    @JsonProperty("audio_params")  // 映射 JSON 中的 audio_params 到 audioParams
    private AudioParams audioParams;
}
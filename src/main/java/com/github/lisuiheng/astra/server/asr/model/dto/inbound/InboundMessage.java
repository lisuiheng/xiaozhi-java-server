package com.github.lisuiheng.astra.server.asr.model.dto.inbound;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = HelloInbound.class, name = "hello"),
        @JsonSubTypes.Type(value = ListenInbound.class, name = "listen"),
        @JsonSubTypes.Type(value = SttInbound.class, name = "stt"),
        @JsonSubTypes.Type(value = TtsInbound.class, name = "tts"),
        @JsonSubTypes.Type(value = IotInbound.class, name = "iot"),
        @JsonSubTypes.Type(value = ErrorInbound.class, name = "error"),
        @JsonSubTypes.Type(value = McpInbound.class, name = "mcp")
})
public abstract class InboundMessage {
    public abstract String getType();
}
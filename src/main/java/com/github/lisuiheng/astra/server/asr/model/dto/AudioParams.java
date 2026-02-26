package com.github.lisuiheng.astra.server.asr.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AudioParams {
    private String format;
    private Features features;
    @JsonProperty("sample_rate")
    private Integer sampleRate;
    private Integer channels;
    @JsonProperty("frame_duration")
    private Integer frameDuration;
}

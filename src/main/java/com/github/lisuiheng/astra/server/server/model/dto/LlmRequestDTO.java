package com.github.lisuiheng.astra.server.server.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 大模型请求DTO
 */
@Data
public class LlmRequestDTO {
    private String agentId;
    private String model;
    private List<MessageDTO> messages;
    private BigDecimal temperature;
    private Integer maxTokens;
    private BigDecimal topP;
    private BigDecimal presencePenalty;
    private BigDecimal frequencyPenalty;
    private Boolean stream;
    private List<String> stop;
    private List<FunctionDTO> functions;
}
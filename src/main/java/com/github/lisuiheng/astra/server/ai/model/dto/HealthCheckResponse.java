package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

import java.util.Map;

/**
 * 健康检查响应
 */
@Data
public class HealthCheckResponse {
    private String status;
    private String service;
    private Map<String, String> components;
    private String timestamp;
    private String version;
    private String error;
}
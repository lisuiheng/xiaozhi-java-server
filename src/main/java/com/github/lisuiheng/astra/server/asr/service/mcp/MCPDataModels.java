package com.github.lisuiheng.astra.server.asr.service.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * MCP初始化参数
 */
@Data
class MCPInitializeParams {
    private String protocolVersion;
    private MCPCapabilities capabilities;
    private MCPClientInfo clientInfo;
}

/**
 * MCP客户端能力配置
 */
@Data
class MCPCapabilities {
    private MCPVisionCapability vision;
    private JsonNode tools;
}

/**
 * MCP视觉能力配置
 */
@Data
class MCPVisionCapability {
    private String url;
    private String token;
}

/**
 * MCP客户端信息
 */
@Data
class MCPClientInfo {
    private String name;
    private String version;
}

/**
 * MCP初始化结果
 */
@Data
class MCPInitializeResult {
    private String protocolVersion;
    private MCPServerCapabilities capabilities;
    private MCPServerInfo serverInfo;
}

/**
 * MCP服务器能力配置
 */
@Data
class MCPServerCapabilities {
    private JsonNode tools;
}

/**
 * MCP服务器信息
 */
@Data
class MCPServerInfo {
    private String name;
    private String version;
}
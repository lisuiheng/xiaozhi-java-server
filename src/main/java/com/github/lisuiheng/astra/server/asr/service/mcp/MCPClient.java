package com.github.lisuiheng.astra.server.asr.service.mcp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.lisuiheng.astra.server.asr.model.dto.GenericConnection;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * MCP客户端（使用ObjectMapper）
 */
@Slf4j
public class MCPClient {
    private static final String TYPE_MCP = "mcp";
    private static final String JSON_RPC_VERSION = "2.0";

    private final ObjectMapper objectMapper;
    private final AtomicLong requestId = new AtomicLong(10000);

    private Map<Object, MCPResponseHandler> pendingRequests = new ConcurrentHashMap<>();
    private MCPInitializeResult initializeResult;

    /**
     * 构造函数
     */
    public MCPClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * 发送初始化请求
     */
    public void initialize(GenericConnection connection) throws IOException {
        // 构建初始化参数
        ObjectNode params = objectMapper.createObjectNode();
        params.put("protocolVersion", "2024-11-05");

        // 构建capabilities
        ObjectNode capabilities = objectMapper.createObjectNode();
        ObjectNode vision = objectMapper.createObjectNode();
        vision.put("url", "http://api.xiaozhi.me/vision/explain");
        vision.put("token", "test-token");
        capabilities.set("vision", vision);
        params.set("capabilities", capabilities);

        // 构建clientInfo
        ObjectNode clientInfo = objectMapper.createObjectNode();
        clientInfo.put("name", "xiaozhi-mqtt-client");
        clientInfo.put("version", "1.0.0");
        params.set("clientInfo", clientInfo);

        // 构建JSON-RPC请求
        ObjectNode jsonRpcRequest = objectMapper.createObjectNode();
        jsonRpcRequest.put("jsonrpc", JSON_RPC_VERSION);
        jsonRpcRequest.put("method", "initialize");
        jsonRpcRequest.put("id", requestId.getAndIncrement());
        jsonRpcRequest.set("params", params);

        // 注册回调
        pendingRequests.put(jsonRpcRequest.get("id"), this::handleInitializeResponse);

        // 发送MCP消息
        sendMcpMessage(connection, jsonRpcRequest);

        log.info("已发送MCP初始化请求");
    }

    /**
     * 处理初始化响应
     */
    private void handleInitializeResponse(JsonRpcMessage response) {
        if (response.getError() != null) {
            log.error("MCP初始化错误: {}", response.getError());
            return;
        }

        try {
            this.initializeResult = objectMapper.treeToValue(response.getResult(), MCPInitializeResult.class);
            log.info("MCP初始化成功 | 服务器: {} v{}",
                initializeResult.getServerInfo().getName(),
                initializeResult.getServerInfo().getVersion());
        } catch (JsonProcessingException e) {
            log.error("解析MCP初始化响应失败", e);
        }
    }

    /**
     * 发送通知已初始化
     */
    public void sendNotificationInitialized(GenericConnection connection) throws IOException {
        ObjectNode notification = objectMapper.createObjectNode();
        notification.put("jsonrpc", JSON_RPC_VERSION);
        notification.put("method", "notifications/initialized");

        sendMcpMessage(connection, notification);
        log.debug("已发送notifications/initialized通知");
    }

    /**
     * 处理工具列表请求
     */
    public void handleToolsListRequest(GenericConnection connection, JsonRpcMessage request) throws IOException {
        // 构建响应
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", JSON_RPC_VERSION);
        response.set("id", request.getId());

        // 构建结果 - 空工具列表
        ObjectNode result = objectMapper.createObjectNode();
        result.set("tools", objectMapper.createObjectNode());
        response.set("result", result);

        // 发送响应
        sendMcpMessage(connection, response);

        log.info("已响应tools/list请求");
    }

    /**
     * 处理传入的MCP消息
     */
    public void handleIncomingMessage(GenericConnection connection, String jsonMessage) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonMessage);

            // 检查是否是MCP消息
            if (!rootNode.has("type") || !TYPE_MCP.equals(rootNode.get("type").asText())) {
                return;
            }

            // 获取payload
            JsonNode payload = rootNode.get("payload");
            if (payload == null) {
                log.warn("MCP消息缺少payload");
                return;
            }

            JsonRpcMessage jsonRpcMessage = parseJsonRpcMessage(payload);

            if (jsonRpcMessage.isRequest()) {
                handleIncomingRequest(connection, jsonRpcMessage);
            } else if (jsonRpcMessage.isResponse()) {
                handleIncomingResponse(jsonRpcMessage);
            }

        } catch (Exception e) {
            log.error("处理MCP消息失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 处理传入请求
     */
    private void handleIncomingRequest(GenericConnection connection, JsonRpcMessage request) {
        try {
            switch (request.getMethod()) {
                case "tools/list":
                    handleToolsListRequest(connection, request);
                    break;
                case "notifications/initialized":
                    log.debug("收到notifications/initialized通知");
                    break;
                default:
                    log.warn("未知的MCP方法: {}", request.getMethod());
                    break;
            }
        } catch (IOException e) {
            log.error("处理MCP请求失败: {}", request.getMethod(), e);
        }
    }

    /**
     * 处理传入响应
     */
    private void handleIncomingResponse(JsonRpcMessage response) {
        MCPResponseHandler callback = pendingRequests.remove(response.getId());
        if (callback != null) {
            callback.handle(response);
        } else {
            log.warn("收到未注册的响应: {}", response.getId());
        }
    }

    /**
     * 发送JSON-RPC请求
     */
    public void sendRequest(GenericConnection connection, String method, Object params, MCPResponseHandler callback) throws IOException {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("jsonrpc", JSON_RPC_VERSION);
        request.put("method", method);
        Object id = requestId.getAndIncrement();
        request.putPOJO("id", id);

        if (params != null) {
            request.set("params", objectMapper.valueToTree(params));
        }

        pendingRequests.put(id, callback);
        sendMcpMessage(connection, request);

        log.debug("已发送MCP请求: {} | ID: {}", method, id);
    }

    /**
     * 发送MCP消息
     */
    private void sendMcpMessage(GenericConnection connection, ObjectNode jsonRpcMessage) throws IOException {
        ObjectNode mcpMessage = objectMapper.createObjectNode();
        mcpMessage.put("type", TYPE_MCP);
        mcpMessage.set("payload", jsonRpcMessage);

        String jsonMessage = objectMapper.writeValueAsString(mcpMessage);
        connection.sendMessage(jsonMessage);
    }

    /**
     * 解析JSON-RPC消息
     */
    private JsonRpcMessage parseJsonRpcMessage(JsonNode payload) {
        JsonRpcMessage message = new JsonRpcMessage();

        if (payload.has("jsonrpc")) {
            message.setJsonRpc(payload.get("jsonrpc").asText());
        }

        if (payload.has("method")) {
            message.setMethod(payload.get("method").asText());
        }

        if (payload.has("id")) {
            message.setId(payload.get("id"));
        }

        if (payload.has("params")) {
            message.setParams(payload.get("params"));
        }

        if (payload.has("result")) {
            message.setResult(payload.get("result"));
        }

        if (payload.has("error")) {
            message.setError(payload.get("error"));
        }

        return message;
    }

    /**
     * 获取初始化结果
     */
    public MCPInitializeResult getInitializeResult() {
        return initializeResult;
    }

    /**
     * 清理资源
     */
    public void cleanup() {
        pendingRequests.clear();
    }

    /**
     * JSON-RPC消息包装类
     */
    @Data
    public static class JsonRpcMessage {
        private String jsonRpc;
        private String method;
        private JsonNode id;
        private JsonNode params;
        private JsonNode result;
        private JsonNode error;

        public boolean isRequest() {
            return method != null;
        }

        public boolean isResponse() {
            return result != null || error != null;
        }
    }

    /**
     * MCP响应处理器接口
     */
    public interface MCPResponseHandler {
        void handle(JsonRpcMessage response);
    }
}
package com.github.lisuiheng.astra.server.server.interceptor;

import com.github.lisuiheng.astra.server.asr.constant.AttributeKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
public class VoiceHttpSessionHandshakeInterceptor extends HttpSessionHandshakeInterceptor  {

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        // 【修正点 1】: 应该使用 HTTP_HEADER_DEVICE_ID 从请求头中获取 DeviceId
        String deviceId = request.getHeaders().getFirst(AttributeKeys.HTTP_HEADER_DEVICE_ID);

        // 1. 检查是否存在
        if (deviceId == null) {
            log.warn("WebSocket handshake rejected: missing '{}' header", AttributeKeys.HTTP_HEADER_DEVICE_ID);
            return false; // 拒绝连接
        }

        deviceId = deviceId.trim();

        // 2. 检查是否为空
        if (deviceId.isEmpty()) {
            log.warn("WebSocket handshake rejected: empty '{}' header", AttributeKeys.HTTP_HEADER_DEVICE_ID);
            return false;
        }

        // 3. 【修正点 2】: 使用常量 AttributeKeys.DEVICE_ID 存入 attributes，供后续业务使用
        attributes.put(AttributeKeys.DEVICE_ID, deviceId);

        log.debug("Accepted WebSocket connection with DeviceId: {}", deviceId);
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        // 握手完成后可执行清理逻辑（通常为空）
        if (exception != null) {
            log.error("WebSocket handshake failed", exception);
        }
    }
}
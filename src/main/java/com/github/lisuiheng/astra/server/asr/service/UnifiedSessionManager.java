package com.github.lisuiheng.astra.server.asr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lisuiheng.astra.server.server.constant.DeviceState;
import com.github.lisuiheng.astra.server.server.model.entity.DeviceInfo;
import com.github.lisuiheng.astra.server.server.service.DeviceInfoService;
import com.github.lisuiheng.astra.server.asr.constant.AttributeKeys;
import com.github.lisuiheng.astra.server.asr.model.dto.GenericConnection;
import com.github.lisuiheng.astra.server.asr.model.dto.ProtocolType;
import com.github.lisuiheng.astra.server.asr.model.dto.WebSocketConnection;
import com.github.lisuiheng.astra.server.asr.util.SessionIdGenerator;
import com.github.lisuiheng.astra.common.util.CallContext;
import com.github.lisuiheng.astra.common.util.RequestContext;
import com.github.lisuiheng.astra.server.speech.model.dto.MediaProcessor;
import com.github.lisuiheng.astra.server.speech.service.MediaProcessorManager;
import com.github.lisuiheng.astra.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UnifiedSessionManager {

    // 依赖注入
    @Autowired private DeviceInfoService deviceInfoService;
    @Autowired private AsrWebSocketSessionManager asrWebSocketSessionManager;
    @Autowired private MediaProcessorManager mediaProcessorManager;
    @Autowired private ObjectMapper objectMapper;
    // @Autowired(required = false) private MqttService mqttService; // 如果支持MQTT，保持可选注入

    // 核心连接存储 Map：使用 DeviceId 作为 Key 进行 O(1) 查找
    // Key: DeviceId (String), Value: GenericConnection
    private final Map<String, GenericConnection> connectionMap = new ConcurrentHashMap<>();

    /**
     * 【核心方法】注册新的 WebSocket 连接。
     * 负责：设备校验、Call Session 创建、GenericConnection 封装、资源初始化。
     * * @param session 传入的原始 WebSocketSession
     * @return 封装后的 GenericConnection 对象
     */
    public GenericConnection registerConnection(WebSocketSession session) {
        // 1. 从 Session Attributes 中获取 DeviceId
        String deviceId = (String) session.getAttributes().get(AttributeKeys.DEVICE_ID);

        if (StringUtils.isBlank(deviceId)) {
            throw new IllegalStateException("WebSocket Session Attributes 中缺少 DeviceId");
        }

        // 创建调用上下文
        CallContext callContext = CallContext.create();
        callContext.putIntoMDC();

        String connReqId = callContext.generateRequestId("CONN");
        RequestContext.runWithRequestId(connReqId, () -> {
            log.info("📢 开始注册连接 | DeviceId: {}", deviceId);
        });

        // 2. 设备信息处理 (查询或创建)
        // 假设 deviceInfoService.queryDeviceBySerialNumber 接收 deviceId (即 serialNumber)
        DeviceInfo deviceInfo = deviceInfoService.queryDeviceBySerialNumber(deviceId);
        if (deviceInfo == null) {
            Integer code = StringUtils.generateRandomNumber(6);
            String deviceReqId = callContext.generateRequestId("DEVICE");
            // 创建 effectively final 的变量
            String deviceIdFinal = deviceId;
            Integer codeFinal = code;
            RequestContext.runWithRequestId(deviceReqId, () -> {
                log.warn("⚠️ 新设备注册 | DeviceId: {} | 生成验证码: {}", deviceIdFinal, codeFinal);
            });

            deviceInfo = new DeviceInfo();
            deviceInfo.setSerialNumber(deviceId); // 数据库字段仍是 serialNumber
            deviceInfo.setDeviceState(DeviceState.PENDING);
            deviceInfo.setVerifyCode(code.toString());
            deviceInfoService.saveOrUpdateDevice(deviceInfo);

            String saveReqId = callContext.generateRequestId("SAVE");
            // 创建 effectively final 的变量
            DeviceState deviceState = deviceInfo.getDeviceState();
            RequestContext.runWithRequestId(saveReqId, () -> {
                log.info("✅ 新设备已注册 | DeviceId: {} | 状态: {}", deviceIdFinal, deviceState);
            });
        } else {
            String existingReqId = callContext.generateRequestId("EXISTING");
            // 创建 effectively final 的变量
            DeviceState deviceState = deviceInfo.getDeviceState();
            RequestContext.runWithRequestId(existingReqId, () -> {
                log.info("🔄 已有设备连接 | DeviceId: {} | 当前状态: {}",
                        deviceId, deviceState);
            });
        }

        // 3. 创建业务会话 (Call Session ID)
        String callSessionId = SessionIdGenerator.generateSessionId(deviceId);

        // 4. 构建连接对象
        WebSocketConnection connection = new WebSocketConnection(objectMapper, deviceInfo, callSessionId, session);
        String wsReqId = callContext.generateRequestId("WEBSOCK");
        // 创建 effectively final 的变量
        String callSessionIdFinal = callSessionId;
        String deviceIdFinal = deviceId;
        RequestContext.runWithRequestId(wsReqId, () -> {
            log.info("🎙️ 创建通话会话 | SessionID: {} | DeviceId: {}", callSessionIdFinal, deviceIdFinal);
        });

        // 5. 处理旧连接 (重连/踢出逻辑)
        GenericConnection existingConnection = connectionMap.get(deviceId);
        if (existingConnection != null) {
            String replaceReqId = callContext.generateRequestId("REPLACE");
            // 创建 effectively final 的变量
            String sessionIdFinal = existingConnection.getSessionId();
            RequestContext.runWithRequestId(replaceReqId, () -> {
                log.warn("⚠️ 设备重连，替换旧连接 | DeviceId: {} | 旧 SessionID: {}",
                        deviceIdFinal, sessionIdFinal);
            });
            // 移除旧的 ASR 资源并关闭旧连接
            removeConnection(existingConnection, callContext);
        }

        // 6. 注册新连接并初始化资源
        connectionMap.put(deviceId, connection);

        try {
            // ASR会话初始化
            asrWebSocketSessionManager.registerWorkerSession(callSessionId);
            String asrReqId = callContext.generateRequestId("ASR");
            RequestContext.runWithRequestId(asrReqId, () -> {
                log.info("✅ ASR工作会话注册成功 | SessionID: {}", callSessionId);
            });


            // 不再需要 CallSessionService
            String mediaReqId = callContext.generateRequestId("MEDIA");
            RequestContext.runWithRequestId(mediaReqId, () -> {
                log.info("⚙️ 媒体处理器已配置 | SessionID: {}", callSessionId);
            });

        } catch (Exception e) {
            String errorReqId = callContext.generateRequestId("ERROR");
            // 创建 effectively final 的变量
            String errorMessage = e.getMessage();
            RequestContext.runWithRequestId(errorReqId, () -> {
                log.error("❌ 资源初始化失败 | SessionID: {} | 错误: {}", callSessionIdFinal, errorMessage);
            });
            // 如果核心资源失败，则回滚并关闭连接
            connectionMap.remove(deviceId);
            connection.close();
            throw new RuntimeException("资源初始化失败", e);
        }

        String completeReqId = callContext.generateRequestId("COMPLETE");
        // 创建 effectively final 的变量
        RequestContext.runWithRequestId(completeReqId, () -> {
            log.info("🚀 连接初始化完成 | DeviceId: {} | SessionID: {}", deviceIdFinal, callSessionIdFinal);
        });
        return connection;
    }

    /**
     * 通过 WebSocketSession 查找对应的 GenericConnection (供 Handler 使用)
     * * @param session 原始 WebSocketSession
     * @return GenericConnection
     */
    public GenericConnection getConnection(WebSocketSession session) {
        String deviceId = (String) session.getAttributes().get(AttributeKeys.DEVICE_ID);
        if (StringUtils.isBlank(deviceId)) {
            return null; // 无法识别
        }
        return connectionMap.get(deviceId);
    }


// 在 UnifiedSessionManager 中添加这个方法
    /**
     * 通过会话ID查找 GenericConnection
     */
    public GenericConnection getConnectionBySessionId(String sessionId) {
        return connectionMap.values().stream()
                .filter(conn -> sessionId.equals(conn.getSessionId()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 通过sessionId解析出deviceId
     */
    public String getDeviceIdFromSessionId(String sessionId) {
        return SessionIdGenerator.extractDeviceId(sessionId);
    }

    /**
     * 内部方法：移除连接并清理资源
     * * @param connection 要移除的连接对象
     */
    private void removeConnection(GenericConnection connection) {
        CallContext callContext = CallContext.fromCallId(RequestContext.getCurrentCallId() != null ? 
            RequestContext.getCurrentCallId() : "unknown");
        removeConnection(connection, callContext);
    }
    
    /**
     * 内部方法：移除连接并清理资源
     * * @param connection 要移除的连接对象
     * @param callContext 调用上下文
     */
    private void removeConnection(GenericConnection connection, CallContext callContext) {
        // 清理 ASR 工作会话
        asrWebSocketSessionManager.unregisterWorkerSession(connection.getSessionId());

        // 清理媒体处理器资源（假设 MediaProcessorManager 存在 cleanup 方法）
        // mediaProcessorManager.cleanup(connection.getSessionId());

        // 关闭物理连接（确保旧连接被终止）
        connection.close();

        String cleanupReqId = callContext.generateRequestId("CLEANUP");
        // 创建 effectively final 的变量
        String deviceIdFinal = connection.getDeviceId();
        String sessionIdFinal = connection.getSessionId();
        RequestContext.runWithRequestId(cleanupReqId, () -> {
            log.info("🗑️ 连接已移除并清理资源 | DeviceId: {} | SessionID: {}",
                    deviceIdFinal, sessionIdFinal);
        });
    }

    // --- 辅助查询方法 ---

    /**
     * 获取所有活跃的连接快照
     */
    public List<GenericConnection> getAllConnections() {
        return connectionMap.values().stream()
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * 获取指定协议类型的所有连接
     */
    public List<GenericConnection> getConnectionsByProtocol(ProtocolType type) {
        return connectionMap.values().stream()
                .filter(conn -> type == conn.getProtocolType())
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * 检查某个业务会话ID是否仍在活跃
     */
    public boolean isSessionActive(String sessionId) {
        return connectionMap.values().stream()
                .anyMatch(conn -> sessionId.equals(conn.getSessionId()));
    }
}
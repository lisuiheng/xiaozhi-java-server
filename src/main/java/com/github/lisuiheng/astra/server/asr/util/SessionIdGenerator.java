package com.github.lisuiheng.astra.server.asr.util;

public class SessionIdGenerator {
    
    /**
     * 生成会话ID：设备ID_时间戳_随机数
     */
    public static String generateSessionId(String deviceId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf((int)(Math.random() * 10000));
        return deviceId + "_" + timestamp + "_" + random;
    }
    
    /**
     * 从sessionId中提取设备ID
     */
    public static String extractDeviceId(String sessionId) {
        if (sessionId == null || !sessionId.contains("_")) {
            return null;
        }
        return sessionId.split("_")[0];
    }
    
    /**
     * 从sessionId中提取时间戳
     */
    public static long extractTimestamp(String sessionId) {
        if (sessionId == null || !sessionId.contains("_")) {
            return 0L;
        }
        String[] parts = sessionId.split("_");
        return parts.length > 1 ? Long.parseLong(parts[1]) : 0L;
    }
}
package com.github.lisuiheng.astra.server.asr.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserMemoryService {

    private final Map<String, String> userMemories = new ConcurrentHashMap<>();

    public void saveMemory(String deviceId, String text) {
        // 实际实现：将文本存入Mem0服务
        userMemories.put(deviceId, text);
        System.out.println("Memory saved for device " + deviceId + ": " + text);
    }
}
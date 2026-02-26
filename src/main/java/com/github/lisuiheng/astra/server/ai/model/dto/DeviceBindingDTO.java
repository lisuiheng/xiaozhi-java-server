package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class DeviceBindingDTO {
    
    private String id;
    
    private String agentId; // 智能体ID
    
    private String deviceId; // 设备ID
    
    private String bindingType; // 绑定类型：default-默认，schedule-计划任务
    
    private LocalDateTime bindingTime; // 绑定时间
    
    private LocalDateTime expireTime; // 过期时间
    
    private Boolean isActive = true; // 是否激活
    
    private LocalDateTime createdTime;
    
    private LocalDateTime updatedTime;
}
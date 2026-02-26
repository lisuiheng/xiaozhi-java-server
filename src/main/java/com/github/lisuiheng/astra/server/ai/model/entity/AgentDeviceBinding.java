package com.github.lisuiheng.astra.server.ai.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("server_agent_device_binding")
public class AgentDeviceBinding {
    
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("agent_id")
    private String agentId; // 智能体ID
    
    @TableField("device_id")
    private String deviceId; // 设备ID
    
    @TableField("binding_type")
    private String bindingType; // 绑定类型：default-默认，schedule-计划任务
    
    @TableField("binding_time")
    private LocalDateTime bindingTime; // 绑定时间
    
    @TableField("expire_time")
    private LocalDateTime expireTime; // 过期时间
    
    @TableField("is_active")
    private Boolean isActive = true; // 是否激活
    
    @TableField("created_time")
    private LocalDateTime createdTime;
    
    @TableField("updated_time")
    private LocalDateTime updatedTime;
}
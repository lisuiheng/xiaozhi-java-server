package com.github.lisuiheng.astra.server.user.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("server_user_device")
public class UserDevice {
    
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("user_id")
    private String userId; // 用户ID
    
    @TableField("device_id")
    private String deviceId; // 设备ID
    
    @TableField("ownership_type")
    private String ownershipType; // 所有权类型：owner-所有者，shared-共享
    
    @TableField("permission_level")
    private Integer permissionLevel = 1; // 权限级别：1-只读，2-控制，3-管理
    
    @TableField("created_time")
    private LocalDateTime createdTime;
    
    @TableField("updated_time")
    private LocalDateTime updatedTime;
}
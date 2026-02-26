package com.github.lisuiheng.astra.server.user.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.lisuiheng.astra.common.core.domain.BaseEntity;
import com.github.lisuiheng.astra.server.server.model.entity.DeviceInfo;
import com.github.lisuiheng.astra.server.ai.model.entity.Agent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "server_user", autoResultMap = true)
public class User extends BaseEntity {
    
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id; // 用户ID
    
    @TableField("username")
    private String username; // 用户名
    
    @TableField("nickname")
    private String nickname; // 用户昵称
    
    @TableField("email")
    private String email; // 邮箱
    
    @TableField("phone")
    private String phone; // 手机号
    
    @TableField("avatar_url")
    private String avatarUrl; // 头像
    
    @TableField("password")
    @JsonIgnore
    private String password; // 密码（加密）
    
    @TableField("status")
    private Integer status = 1; // 状态：0-禁用，1-启用
    
    @TableField("user_type")
    private Integer userType = 0; // 用户类型：0-普通用户，1-管理员
    
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime; // 最后登录时间
    
    @TableField("last_login_ip")
    private String lastLoginIp; // 最后登录IP
    
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    @TableLogic
    private Integer deleted = 0; // 逻辑删除
    
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    // 非数据库字段
    @TableField(exist = false)
    private List<DeviceInfo> devices; // 用户的设备列表
    
    @TableField(exist = false)
    private List<Agent> agents; // 用户创建的智能体列表
}
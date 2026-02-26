package com.github.lisuiheng.astra.server.server.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.lisuiheng.astra.common.core.domain.BaseEntity;
import com.github.lisuiheng.astra.server.server.constant.DeviceState;
import com.github.lisuiheng.astra.server.ai.model.entity.Agent;
import com.github.lisuiheng.astra.server.user.model.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@TableName("server_device_info")
public class DeviceInfo extends BaseEntity {
    @JsonSerialize(using = ToStringSerializer.class)
    private String id; // 设备ID

    private String name; // 设备名称
    private String serialNumber; // 设备串号，如MAC

    @TableField(value = "uuid")
    private String uuid; // 设备UUID

    private String verifyCode; // 设备验证码
    private String deviceKind; // 设备类型，如：esp32-S1
    private DeviceState deviceState; // 设备状态
    private String programKind; // 程序名称
    private String programVer; // 程序版本
    private Integer volume; // 音量
    private Integer brightness; // 亮度
    private String isUpdatable; // 版本是否可更新

    @JsonSerialize(using = ToStringSerializer.class)
    private String versionType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime programUpdateTime; // 程序更新时间

    @TableField(value = "activated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activatedTime; // 激活时间

    @TableField(value = "last_seen_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastSeenTime; // 最后连接时间

    @TableField(value = "secret_key")
    private String secretKey; // 设备密钥（Base64编码）

    @TableField(value = "hmac_key_index")
    private Integer hmacKeyIndex; // HMAC密钥索引（0-7）

    @TableField(value = "production_batch")
    private String productionBatch; // 生产批次

    @TableField(value = "production_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date productionDate; // 生产日期

    private String detailInfo; // 设备拓展信息,json格式的mapList
    private String otaUpdateUrl; // OTA更新的URL
    private String aesKey;
    private String aesNonce;
    private String remark; // 备注

    // 数据库字段 - 绑定的智能体ID
    @TableField("agent_id")
    private String agentId; // 绑定的智能体ID

    // 非数据库字段
    @TableField(exist = false)
    private Agent agent; // 绑定的智能体信息

    @TableField(exist = false)
    private User owner; // 设备所有者信息
}
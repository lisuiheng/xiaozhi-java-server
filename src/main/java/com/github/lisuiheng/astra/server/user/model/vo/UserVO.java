package com.github.lisuiheng.astra.server.user.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "用户VO")
public class UserVO {

    @Schema(description = "用户ID", example = "1234567890")
    private String id;

    @Schema(description = "用户名", example = "john_doe")
    private String username;

    @Schema(description = "昵称", example = "John")
    private String nickname;

    @Schema(description = "邮箱", example = "john@example.com")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "用户状态: 0-禁用, 1-启用", example = "1")
    private Integer status;

    @Schema(description = "用户类型: 0-普通用户, 1-管理员", example = "0")
    private Integer userType;

    @Schema(description = "最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    @Schema(description = "最后登录IP", example = "192.168.1.100")
    private String lastLoginIp;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    @Schema(description = "设备数量", example = "5")
    private Long deviceCount;

    @Schema(description = "智能体数量", example = "3")
    private Long agentCount;
}
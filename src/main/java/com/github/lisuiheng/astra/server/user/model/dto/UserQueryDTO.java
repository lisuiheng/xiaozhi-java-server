package com.github.lisuiheng.astra.server.user.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户查询DTO")
public class UserQueryDTO {

    @Schema(description = "用户名", example = "john")
    private String username;

    @Schema(description = "昵称", example = "John")
    private String nickname;

    @Schema(description = "邮箱", example = "john@example.com")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "用户状态: 0-禁用, 1-启用")
    private Integer status;

    @Schema(description = "用户类型: 0-普通用户, 1-管理员")
    private Integer userType;

    @Schema(description = "开始时间", example = "2024-01-01 00:00:00")
    private String startTime;

    @Schema(description = "结束时间", example = "2024-12-31 23:59:59")
    private String endTime;
}
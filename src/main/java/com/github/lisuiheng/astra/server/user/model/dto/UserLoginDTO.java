package com.github.lisuiheng.astra.server.user.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
@Schema(description = "用户登录DTO")
public class UserLoginDTO {

    @NotBlank(message = "用户名/邮箱不能为空")
    @Schema(description = "用户名或邮箱", required = true, example = "john_doe")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", required = true, example = "password123")
    private String password;

    @Schema(description = "客户端IP", hidden = true)
    private String clientIp;
}
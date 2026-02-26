package com.github.lisuiheng.astra.server.user.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@Schema(description = "用户更新DTO")
public class UserUpdateDTO {

    @Size(min = 2, max = 20, message = "昵称长度必须在2-20个字符之间")
    @Schema(description = "昵称", example = "John Updated")
    private String nickname;

    @Pattern(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "john.updated@example.com")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13900139000")
    private String phone;

    @Schema(description = "头像URL", example = "https://example.com/new-avatar.jpg")
    private String avatarUrl;

    @Schema(description = "用户状态: 0-禁用, 1-启用", example = "1")
    private Integer status;
}
package com.github.lisuiheng.astra.server.user.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 密码修改DTO
 */
@Data
public class PasswordChangeDTO {
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    private String newPassword;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}
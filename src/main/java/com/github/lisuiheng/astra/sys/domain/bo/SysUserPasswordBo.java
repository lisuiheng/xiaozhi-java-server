package com.github.lisuiheng.astra.sys.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户密码业务处理
 *
 * @author Michelle.Chung
 */
@Data
public class SysUserPasswordBo {

    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空")
    @Size(min = 6, max = 20, message = "旧密码长度必须在{min}到{max}个字符之间")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "新密码长度必须在{min}到{max}个字符之间")
    private String newPassword;
}
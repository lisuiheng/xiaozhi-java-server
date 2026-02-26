package com.github.lisuiheng.astra.sys.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 用户对象 sys_user
 *
 * @author Michelle.Chung
 */
@Data
@NoArgsConstructor
@TableName("sys_user")
public class SysUser {

    /**
     * 用户ID
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 部门ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phonenumber;

    /**
     * 用户性别
     */
    private String sex;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 密码
     */
    private String password;

    /**
     * 帐号状态（0正常 1停用）
     */
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 用户类型（sys_user系统用户）
     */
    private String userType;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 登录IP地址
     */
    private String loginIp;

    /**
     * 登录日期
     */
    private String loginDate;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 备注
     */
    private String remark;
}
package com.github.lisuiheng.astra.sys.domain.entity;

import com.github.lisuiheng.astra.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 租户表实体
 * 
 * @author Qoder
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tenant")
public class Tenant extends TenantEntity {

    /**
     * id
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 租户编号
     */
    @TableField("tenant_id")
    private String tenantId;

    /**
     * 联系人
     */
    @TableField("contact_user_name")
    private String contactUserName;

    /**
     * 联系电话
     */
    @TableField("contact_phone")
    private String contactPhone;

    /**
     * 企业名称
     */
    @NotBlank(message = "企业名称不能为空")
    @TableField("company_name")
    private String companyName;

    /**
     * 统一社会信用代码
     */
    @TableField("license_number")
    private String licenseNumber;

    /**
     * 地址
     */
    @TableField("address")
    private String address;

    /**
     * 域名
     */
    @TableField("domain")
    private String domain;

    /**
     * 企业简介
     */
    @TableField("intro")
    private String intro;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 租户套餐编号
     */
    @TableField("package_id")
    private Long packageId;

    /**
     * 过期时间
     */
    @TableField("expire_time")
    private java.util.Date expireTime;

    /**
     * 用户数量（-1不限制）
     */
    @TableField("account_count")
    private Long accountCount;

    /**
     * 租户状态（0正常 1停用）
     */
    @TableField("status")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    @TableField("del_flag")
    private String delFlag;
}
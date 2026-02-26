package com.github.lisuiheng.astra.sys.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.lisuiheng.astra.sys.domain.SysTenant;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.util.Date;

/**
 * 租户视图对象
 * 
 * @author Qoder
 */
@Data
@AutoMapper(target = TenantListVo.class)
@AutoMapper(target = SysTenant.class, reverseConvertGenerate = false)
public class SysTenantVo {

    /**
     * id
     */
    private Long id;

    /**
     * 租户编号
     */
    private String tenantId;

    /**
     * 联系人
     */
    private String contactUserName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 统一社会信用代码
     */
    private String licenseNumber;

    /**
     * 地址
     */
    private String address;

    /**
     * 域名
     */
    private String domain;

    /**
     * 企业简介
     */
    private String intro;

    /**
     * 备注
     */
    private String remark;

    /**
     * 租户套餐编号
     */
    private Long packageId;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    /**
     * 用户数量（-1不限制）
     */
    private Long accountCount;

    /**
     * 租户状态（0正常 1停用）
     */
    private String status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
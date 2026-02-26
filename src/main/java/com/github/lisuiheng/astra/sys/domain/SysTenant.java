package com.github.lisuiheng.astra.sys.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.lisuiheng.astra.common.core.domain.BaseEntity;
import com.github.lisuiheng.astra.sys.domain.vo.SysTenantVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.github.linpeilie.annotations.AutoMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serial;
import java.util.Date;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
/**
 * 租户对象 sys_tenant
 *
 * @author Qoder
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysTenantVo.class, reverseConvertGenerate = false)
@TableName("sys_tenant")
public class SysTenant extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
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
    @JsonSerialize(using = ToStringSerializer.class)
    private Long packageId;
    /**
     * 过期时间
     */
    private Date expireTime;
    /**
     * 用户数量（-1不限制）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long accountCount;
    /**
     * 租户状态（0正常 1停用）
     */
    private String status;
    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
package com.github.lisuiheng.astra.sys.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.lisuiheng.astra.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serial;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 租户套餐对象 sys_tenant_package
 *
 * @author Qoder
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tenant_package")
public class SysTenantPackage extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 租户套餐id
     */
    @TableId(value = "package_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long packageId;

    /**
     * 套餐名称
     */
    private String packageName;

    /**
     * 关联菜单id
     */
    private String menuIds;

    /**
     * 备注
     */
    private String remark;

    /**
     * 菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）
     */
    private Boolean menuCheckStrictly;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
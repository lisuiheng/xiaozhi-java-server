package com.github.lisuiheng.astra.sys.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.lisuiheng.astra.common.tenant.core.TenantEntity;
import lombok.Data;

/**
 * 角色表
 *
 * @author lisuiheng
 */
@Data
@TableName("sys_role")
public class SysRole extends TenantEntity {

    /**
     * 角色ID
     */
    @TableId
    private Long roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色权限字符串
     */
    private String roleKey;

    /**
     * 显示顺序
     */
    private Integer roleSort;

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限）
     */
    private String dataScope;

    /**
     * 菜单树选择项是否关联显示
     */
    private Boolean menuCheckStrictly;

    /**
     * 部门树选择项是否关联显示
     */
    private Boolean deptCheckStrictly;

    /**
     * 角色状态（0正常 1停用）
     */
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 备注
     */
    private String remark;

    /**
     * 租户ID
     */
    private String tenantId;
}
package com.github.lisuiheng.astra.sys.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色和菜单关联表
 *
 * @author lisuiheng
 */
@Data
@TableName("sys_role_menu")
public class SysRoleMenu {

    /**
     * 角色ID
     */
    @TableId
    private Long roleId;

    /**
     * 菜单ID
     */
    private Long menuId;
}
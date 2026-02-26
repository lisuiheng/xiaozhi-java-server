package com.github.lisuiheng.astra.sys.domain.vo;

import com.github.lisuiheng.astra.sys.domain.entity.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户视图对象 sys_user
 *
 * @author Michelle.Chung
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserVo extends SysUser {

    private static final long serialVersionUID = 1L;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 密码盐
     */
    private String salt;
    
    /**
     * 角色列表
     */
    private List<SysRoleVo> roles;
}
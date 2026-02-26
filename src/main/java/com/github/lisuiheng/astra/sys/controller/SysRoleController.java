package com.github.lisuiheng.astra.sys.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.domain.R;
import com.github.lisuiheng.astra.common.log.annotation.Log;
import com.github.lisuiheng.astra.common.log.enums.BusinessType;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.sys.domain.bo.SysRoleBo;
import com.github.lisuiheng.astra.sys.domain.bo.SysUserBo;
import com.github.lisuiheng.astra.sys.domain.entity.SysUserRole;
import com.github.lisuiheng.astra.sys.domain.vo.SysRoleVo;
import com.github.lisuiheng.astra.sys.domain.vo.SysUserVo;
import com.github.lisuiheng.astra.sys.service.ISysRoleService;
import com.github.lisuiheng.astra.sys.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色信息
 *
 * @author lisuiheng
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    private final ISysRoleService roleService;
    private final ISysUserService userService;

    /**
     * 获取角色信息列表
     */
    @SaCheckPermission("system:role:list")
    @GetMapping("/list")
    public TableDataInfo<SysRoleVo> list(SysRoleBo role, PageQuery pageQuery) {
        return roleService.selectPageRoleList(role, pageQuery);
    }

    /**
     * 根据角色编号获取详细信息
     *
     * @param roleId 角色ID
     */
    @SaCheckPermission("system:role:query")
    @GetMapping(value = "/{roleId}")
    public R<SysRoleVo> getInfo(@PathVariable Long roleId) {
        roleService.checkRoleDataScope(roleId);
        return R.ok(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     */
    @SaCheckPermission("system:role:add")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysRoleBo bo) {
        roleService.checkRoleAllowed(bo);
        int rows = roleService.insertRole(bo);
        return rows > 0 ? R.ok() : R.fail("新增角色失败");
    }

    /**
     * 修改角色
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysRoleBo bo) {
        roleService.checkRoleAllowed(bo);
        roleService.checkRoleDataScope(bo.getRoleId());
        int rows = roleService.updateRole(bo);
        return rows > 0 ? R.ok() : R.fail("修改角色失败");
    }

    /**
     * 角色状态修改
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysRoleBo role) {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        int rows = roleService.updateRoleStatus(role.getRoleId(), role.getStatus());
        return rows > 0 ? R.ok() : R.fail("修改角色状态失败");
    }

    /**
     * 删除角色
     *
     * @param roleIds 角色ID串
     */
    @SaCheckPermission("system:role:remove")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public R<Void> remove(@PathVariable Long[] roleIds) {
        int rows = roleService.deleteRoleByIds(roleIds);
        return rows > 0 ? R.ok() : R.fail("删除角色失败");
    }

    /**
     * 修改保存数据权限
     *
     * @param bo 角色信息
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/dataScope")
    public R<Void> dataScope(@RequestBody SysRoleBo bo) {
        roleService.checkRoleAllowed(bo);
        roleService.checkRoleDataScope(bo.getRoleId());
        int rows = roleService.authDataScope(bo);
        return rows > 0 ? R.ok() : R.fail("更新数据权限失败");
    }

    /**
     * 获取角色选择框列表
     */
    @SaCheckPermission("system:role:query")
    @GetMapping("/optionselect")
    public R<List<SysRoleVo>> optionselect() {
        List<SysRoleVo> roles = roleService.selectRoleList(new SysRoleBo());
        return R.ok(roles);
    }

    /**
     * 查询已分配用户角色列表
     */
    @SaCheckPermission("system:role:list")
    @GetMapping("/authUser/allocatedList")
    public TableDataInfo<SysUserVo> allocatedList(SysUserBo user, PageQuery pageQuery) {
        return userService.selectAllocatedList(user, pageQuery);
    }

    /**
     * 查询未分配用户角色列表
     */
    @SaCheckPermission("system:role:list")
    @GetMapping("/authUser/unallocatedList")
    public TableDataInfo<SysUserVo> unallocatedList(SysUserBo user, PageQuery pageQuery) {
        return userService.selectUnallocatedList(user, pageQuery);
    }

    /**
     * 取消授权用户
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    public R<Void> cancelAuthUser(@RequestBody SysUserRole userRole) {
        int rows = roleService.deleteAuthUser(userRole);
        return rows > 0 ? R.ok() : R.fail("取消授权用户失败");
    }

    /**
     * 批量取消授权用户
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll")
    public R<Void> cancelAuthUserAll(Long roleId, Long[] userIds) {
        int rows = roleService.deleteAuthUsers(roleId, userIds);
        return rows > 0 ? R.ok() : R.fail("批量取消授权用户失败");
    }

    /**
     * 批量选择用户授权
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll")
    public R<Void> selectAuthUserAll(Long roleId, Long[] userIds) {
        int rows = roleService.insertAuthUsers(roleId, userIds);
        return rows > 0 ? R.ok() : R.fail("批量选择用户授权失败");
    }
}
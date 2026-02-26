package com.github.lisuiheng.astra.sys.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.domain.R;
import com.github.lisuiheng.astra.common.domain.model.LoginUser;
import com.github.lisuiheng.astra.common.log.annotation.Log;
import com.github.lisuiheng.astra.common.log.enums.BusinessType;
import com.github.lisuiheng.astra.common.mybatis.helper.DataPermissionHelper;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.satoken.utils.LoginHelper;
import com.github.lisuiheng.astra.common.tenant.helper.TenantHelper;
import com.github.lisuiheng.astra.common.utils.StringUtils;
import com.github.lisuiheng.astra.common.idempotent.annotation.RepeatSubmit;
import com.github.lisuiheng.astra.common.web.core.BaseController;
import com.github.lisuiheng.astra.sys.domain.bo.SysDeptBo;
import com.github.lisuiheng.astra.sys.domain.bo.SysUserBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysRoleVo;
import com.github.lisuiheng.astra.sys.domain.vo.SysUserInfoVo;
import com.github.lisuiheng.astra.sys.domain.vo.SysUserVo;
import com.github.lisuiheng.astra.sys.domain.vo.UserInfoVo;
import com.github.lisuiheng.astra.sys.service.ISysDeptService;
import com.github.lisuiheng.astra.sys.service.ISysRoleService;
import com.github.lisuiheng.astra.sys.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户信息
 * 
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {

    private final ISysUserService userService;
    private final ISysDeptService deptService;
    private final ISysRoleService roleService;

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getInfo")
    public R<UserInfoVo> getInfo() {
        UserInfoVo userInfoVo = new UserInfoVo();
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (TenantHelper.isEnable() && LoginHelper.isSuperAdmin()) {
            // 超级管理员 如果重新加载用户信息需清除动态租户
            TenantHelper.clearDynamic();
        }

        SysUserVo user = DataPermissionHelper.ignore(() -> userService.selectUserById(loginUser.getUserId()));
        if (ObjectUtil.isNull(user)) {
            return R.fail("没有权限访问用户数据!");
        }
        userInfoVo.setUser(user);
        userInfoVo.setPermissions(loginUser.getMenuPermission());
        userInfoVo.setRoles(loginUser.getRolePermission());
        return R.ok(userInfoVo);
    }
    
    /**
     * 获取部门树列表
     */
    @SaCheckPermission("system:user:list")
    @GetMapping("/deptTree")
    public R<List<Tree<Long>>> deptTree(SysDeptBo dept) {
        return R.ok(deptService.selectDeptTreeList(dept));
    }
    
    /**
     * 获取用户列表
     */
    @SaCheckPermission("system:user:list")
    @GetMapping("/list")
    public TableDataInfo<SysUserVo> list(SysUserBo user, PageQuery pageQuery) {
        return userService.selectPageUserList(user, pageQuery);
    }

    /**
     * 根据用户ID获取用户信息
     */
    @SaCheckPermission("system:user:query")
    @GetMapping(value = "/{userId}")
    public R<SysUserInfoVo> getUserInfo(@PathVariable("userId") Long userId) {
        SysUserInfoVo userInfoVo = userService.selectSysUserInfoByUserId(userId);
        return R.ok(userInfoVo);
    }
    
    /**
     * 根据用户编号获取授权角色
     *
     * @param userId 用户ID
     */
    @SaCheckPermission("system:user:query")
    @GetMapping("/authRole/{userId}")
    public R<SysUserInfoVo> authRole(@PathVariable Long userId) {
        // 获取用户基本信息
        SysUserVo user = userService.selectUserById(userId);
        // 获取所有角色并标记用户已授权的角色
        List<SysRoleVo> roles = roleService.selectRolesAuthByUserId(userId);
        
        SysUserInfoVo userInfoVo = new SysUserInfoVo();
        userInfoVo.setUser(user);
        userInfoVo.setRoles(roles);
        return R.ok(userInfoVo);
    }

    /**
     * 修改用户
     */
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysUserBo user) {
        userService.checkUserAllowed(user.getUserId());
        userService.checkUserDataScope(user.getUserId());
        deptService.checkDeptDataScope(user.getDeptId());
        if (!userService.checkUserNameUnique(user)) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        int result = userService.updateUser(user);
        return result > 0 ? R.ok() : R.fail("更新用户失败");
    }
    
    /**
     * 删除用户
     */
    @SaCheckPermission("system:user:remove")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public R<Void> remove(@PathVariable Long[] userIds) {
        if (ArrayUtil.contains(userIds, LoginHelper.getUserId())) {
            return R.fail("当前用户不能删除");
        }
        return toAjax(userService.deleteUserByIds(userIds));
    }
}
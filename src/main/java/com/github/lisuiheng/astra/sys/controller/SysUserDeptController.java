package com.github.lisuiheng.astra.sys.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.domain.R;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.sys.domain.vo.SysUserVo;
import com.github.lisuiheng.astra.sys.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户部门关联控制器
 *
 * @author
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/user/list")
public class SysUserDeptController {

    private final ISysUserService userService;

    /**
     * 根据部门ID获取用户列表
     * 
     * @param deptId 部门ID
     * @return 用户列表
     */
    @SaCheckPermission("system:user:list")
    @GetMapping("/dept/{deptId}")
    public R<List<SysUserVo>> getUserListByDeptId(@PathVariable Long deptId) {
        List<SysUserVo> userList = userService.selectUserListByDeptId(deptId);
        return R.ok(userList);
    }

    /**
     * 根据部门ID分页获取用户列表
     * 
     * @param deptId 部门ID
     * @param user 用户查询条件
     * @param pageQuery 分页参数
     * @return 用户分页列表
     */
    @SaCheckPermission("system:user:list")
    @GetMapping("/dept/{deptId}/page")
    public TableDataInfo<SysUserVo> getUserPageListByDeptId(@PathVariable Long deptId, 
            com.github.lisuiheng.astra.sys.domain.bo.SysUserBo user, 
            PageQuery pageQuery) {
        // 设置部门ID过滤条件
        user.setDeptId(deptId);
        TableDataInfo<SysUserVo> dataTable = userService.selectPageUserList(user, pageQuery);
        return dataTable;
    }
}
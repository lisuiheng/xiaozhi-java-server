package com.github.lisuiheng.astra.sys.controller;

import com.github.lisuiheng.astra.common.constant.SystemConstants;
import com.github.lisuiheng.astra.common.domain.R;
import com.github.lisuiheng.astra.common.utils.StringUtils;
import cn.hutool.core.lang.tree.Tree;
import com.github.lisuiheng.astra.common.satoken.utils.LoginHelper;
import com.github.lisuiheng.astra.sys.domain.entity.SysMenu;
import com.github.lisuiheng.astra.sys.domain.vo.MenuTreeSelectVo;
import com.github.lisuiheng.astra.sys.domain.vo.RouterVo;
import com.github.lisuiheng.astra.sys.domain.vo.SysMenuVo;
import com.github.lisuiheng.astra.sys.domain.bo.SysMenuBo;
import com.github.lisuiheng.astra.sys.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜单信息
 */
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    @Autowired
    private ISysMenuService menuService;

    /**
     * 获取菜单列表
     */
    @GetMapping("/list")
    public R<List<SysMenuVo>> list(SysMenuBo menu) {
        List<SysMenuVo> menus = menuService.selectMenuList(menu, LoginHelper.getUserId());
        return R.ok(menus);
    }

    /**
     * 获取单个菜单信息
     */
    @GetMapping("/{menuId}")
    public R<SysMenuVo> getInfo(@PathVariable Long menuId) {
        return R.ok(menuService.selectMenuById(menuId));
    }

    /**
     * 更新菜单信息
     */
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysMenuBo menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.fail("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (SystemConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath())) {
            return R.fail("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        } else if (menu.getMenuId().equals(menu.getParentId())) {
            return R.fail("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        return toAjax(menuService.updateMenu(menu));
    }

    /**
     * 新增菜单信息
     */
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysMenuBo menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.fail("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (SystemConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath())) {
            return R.fail("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        return toAjax(menuService.insertMenu(menu));
    }

    /**
     * 删除菜单信息
     */
    @DeleteMapping("/{menuId}")
    public R<Void> remove(@PathVariable("menuId") Long menuId) {
        if (menuService.hasChildByMenuId(menuId)) {
            return R.warn("存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId)) {
            return R.warn("菜单已分配,不允许删除");
        }
        return toAjax(menuService.deleteMenuById(menuId));
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("/getRouters")
    public R<List<RouterVo>> getRouters() {
        // 这里需要获取当前登录用户ID，暂时使用一个测试ID
        // 在实际实现中，这里应该从安全上下文获取用户ID
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(getCurrentUserId());
        return R.ok(menuService.buildMenus(menus));
    }

    /**
     * 获取当前登录用户ID
     * 这里是一个模拟实现，实际应用中需要从安全上下文获取
     */
    private Long getCurrentUserId() {
        // 在实际应用中，这里应该从安全上下文获取当前用户ID
        // 例如：return SecurityUtils.getLoginUser().getUserId();
        return 1L; // 模拟用户ID
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected R<Void> toAjax(int rows) {
        return rows > 0 ? R.ok() : R.fail();
    }

    /**
     * 加载对应角色菜单列表树
     *
     * @param roleId 角色ID
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public R<MenuTreeSelectVo> roleMenuTreeselect(@PathVariable("roleId") Long roleId) {
        List<SysMenuVo> menus = menuService.selectMenuListByUserId(getCurrentUserId());
        MenuTreeSelectVo selectVo = new MenuTreeSelectVo(
            menuService.selectMenuListByRoleId(roleId),
            menuService.buildMenuTreeSelect(menus));
        return R.ok(selectVo);
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    public R<List<Tree<Long>>> treeselect() {
        List<SysMenuVo> menus = menuService.selectMenuListByUserId(getCurrentUserId());
        return R.ok(menuService.buildMenuTreeSelect(menus));
    }
}
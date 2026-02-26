package com.github.lisuiheng.astra.sys.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.lang.tree.parser.NodeParser;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.lisuiheng.astra.common.core.utils.TreeBuildUtils;
import com.github.lisuiheng.astra.sys.domain.bo.SysMenuBo;
import com.github.lisuiheng.astra.sys.domain.entity.SysMenu;
import com.github.lisuiheng.astra.sys.domain.entity.SysRoleMenu;
import com.github.lisuiheng.astra.sys.domain.vo.MetaVo;
import com.github.lisuiheng.astra.sys.domain.vo.RouterVo;
import com.github.lisuiheng.astra.sys.domain.vo.SysMenuVo;
import com.github.lisuiheng.astra.common.utils.StringUtils;
import com.github.lisuiheng.astra.sys.mapper.SysMenuMapper;
import com.github.lisuiheng.astra.sys.mapper.SysRoleMenuMapper;
import com.github.lisuiheng.astra.sys.service.ISysMenuService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 菜单服务实现类
 */
@Service
public class SysMenuServiceImpl implements ISysMenuService {

    @Autowired
    private SysMenuMapper menuMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    /**
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(Long userId) {
        // 这里可以实现基于用户权限的菜单查询逻辑
        // 由于没有用户角色关联，暂时返回所有可用菜单
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getStatus, "0"); // 0表示正常状态
        queryWrapper.orderByAsc(SysMenu::getParentId).orderByAsc(SysMenu::getOrderNum);
        return menuMapper.selectList(queryWrapper);
    }

    @Override
    public List<SysMenuVo> selectMenuList(SysMenuBo menu, Long userId) {
        // 这里可以实现基于用户权限和菜单条件的菜单查询逻辑
        // 由于没有用户角色关联，暂时返回符合条件的所有菜单
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getStatus, "0"); // 0表示正常状态
        
        // 根据传入的菜单条件进行筛选
        if (StringUtils.isNotBlank(menu.getMenuName())) {
            queryWrapper.like(SysMenu::getMenuName, menu.getMenuName());
        }
        if (StringUtils.isNotBlank(menu.getVisible())) {
            queryWrapper.eq(SysMenu::getVisible, menu.getVisible());
        }
        if (StringUtils.isNotBlank(menu.getMenuType())) {
            queryWrapper.eq(SysMenu::getMenuType, menu.getMenuType());
        }
        
        queryWrapper.orderByAsc(SysMenu::getParentId).orderByAsc(SysMenu::getOrderNum);
        
        List<SysMenu> sysMenus = menuMapper.selectList(queryWrapper);
        List<SysMenuVo> menuVos = new ArrayList<>();
        for (SysMenu sysMenu : sysMenus) {
            SysMenuVo menuVo = new SysMenuVo();
            BeanUtils.copyProperties(sysMenu, menuVo);
            menuVos.add(menuVo);
        }
        return menuVos;
    }

    /**
     * 根据用户ID查询菜单列表（返回Vo）
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenuVo> selectMenuListByUserId(Long userId) {
        List<SysMenu> sysMenus = this.selectMenuList(userId);
        List<SysMenuVo> menuVos = new ArrayList<>();
        for (SysMenu sysMenu : sysMenus) {
            SysMenuVo menuVo = new SysMenuVo();
            BeanUtils.copyProperties(sysMenu, menuVo);
            menuVos.add(menuVo);
        }
        return menuVos;
    }

    /**
     * 根据用户ID查询菜单树信息
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId) {
        List<SysMenu> menus = this.selectMenuList(userId);
        return buildMenuTree(menus);
    }

    /**
     * 构建菜单树
     *
     * @param menus 菜单列表
     * @return 构建后的菜单树
     */
    private List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> menuTree = new ArrayList<>();
        for (SysMenu menu : menus) {
            // 查找根节点
            if (menu.getParentId().equals(0L)) {
                menuTree.add(findChild(menu, menus));
            }
        }
        return menuTree;
    }

    /**
     * 查找子菜单
     *
     * @param menu  父菜单
     * @param menus 菜单列表
     * @return 子菜单
     */
    private SysMenu findChild(SysMenu menu, List<SysMenu> menus) {
        for (SysMenu child : menus) {
            if (child.getParentId().equals(menu.getMenuId())) {
                if (menu.getChildren() == null) {
                    menu.setChildren(new ArrayList<>());
                }
                menu.getChildren().add(findChild(child, menus));
            }
        }
        return menu;
    }

    /**
     * 构建前端路由所需的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    @Override
    public List<RouterVo> buildMenus(List<SysMenu> menus) {
        List<RouterVo> routers = new LinkedList<>();
        for (SysMenu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu) + menu.getMenuId());
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQueryParam());
            router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), "1".equals(menu.getIsCache()), null, menu.getRemark()));

            List<SysMenu> cMenus = menu.getChildren();
            if (CollUtil.isNotEmpty(cMenus) && "M".equals(menu.getMenuType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                List<RouterVo> children = buildMenus(cMenus);
                router.setChildren(children);
            } else if (isMenuFrame(menu)) {
                router.setMeta(null);
                List<RouterVo> childrenList = new ArrayList<>();
                RouterVo children = new RouterVo();
                String frameName = StrUtil.upperFirst(menu.getPath()) + menu.getMenuId();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(frameName);
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), "1".equals(menu.getIsCache()), menu.getPath(), menu.getRemark()));
                children.setQuery(menu.getQueryParam());
                childrenList.add(children);
                router.setChildren(childrenList);
            } else if (menu.getParentId().equals(0L) && isInnerLink(menu)) {
                router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<RouterVo> childrenList = new ArrayList<>();
                RouterVo children = new RouterVo();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                String innerLinkName = StrUtil.upperFirst(routerPath) + menu.getMenuId();
                children.setPath(routerPath);
                children.setComponent("InnerLink");
                children.setName(innerLinkName);
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 获取路由名称
     *
     * @param menu 菜单
     * @return 路由名称
     */
    public String getRouteName(SysMenu menu) {
        String routerName = StrUtil.upperFirst(menu.getPath());
        if (StrUtil.isEmpty(routerName)) {
            routerName = "";
        }
        return routerName;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = menu.getPath();
        // 内链打开外网方式
        if (menu.getParentId() != 0L && isInnerLink(menu)) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if (menu.getParentId().equals(0L) && "M".equals(menu.getMenuType())
                && "1".equals(menu.getIsFrame())) {
            routerPath = "/" + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

    @Override
    public Set<String> selectMenuPermsByUserId(Long userId) {
        return menuMapper.selectMenuPermsByUserId(userId);
    }

    /**
     * 获取组件信息
     *
     * @param menu 菜单
     * @return 组件信息
     */
    public String getComponent(SysMenu menu) {
        String component = menu.getComponentInfo();
        return component;
    }

    /**
     * 是否为菜单内部跳转
     */
    public boolean isMenuFrame(SysMenu menu) {
        return menu.getParentId() != null && menu.getParentId().equals(0L) && "C".equals(menu.getMenuType()) && "1".equals(menu.getIsFrame());
    }

    /**
     * 是否为内链
     */
    public boolean isInnerLink(SysMenu menu) {
        return "1".equals(menu.getIsFrame()) && StrUtil.startWithAny(menu.getPath(), "http://", "https://");
    }

    /**
     * 是否为parent_view组件
     */
    public boolean isParentView(SysMenu menu) {
        return menu.getParentId() != null && !menu.getParentId().equals(0L) && "C".equals(menu.getMenuType());
    }

    /**
     * 内链域名特殊字符替换
     */
    public String innerLinkReplaceEach(String path) {
        return StrUtil.replace(path, ":", "/");
    }

    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(queryWrapper);
        return roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    @Override
    public List<Tree<Long>> buildMenuTreeSelect(List<SysMenuVo> menus) {
        if (CollUtil.isEmpty(menus)) {
            return CollUtil.newArrayList();
        }
        // 配置树形结构的构建参数
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        treeNodeConfig.setNameKey("label");
        
        // 使用NodeParser来解析菜单节点
        NodeParser<SysMenuVo, Long> nodeParser = (menu, tree) -> {
            tree.setId(menu.getMenuId());
            tree.setParentId(menu.getParentId());
            tree.setName(menu.getMenuName());
            tree.setWeight(menu.getOrderNum());
            
            // 添加额外属性
            tree.putExtra("menuType", menu.getMenuType());
            tree.putExtra("icon", menu.getIcon());
            tree.putExtra("visible", menu.getVisible());
            tree.putExtra("status", menu.getStatus());
        };
        
        return TreeUtil.build(menus, 0L, treeNodeConfig, nodeParser);
    }

    @Override
    public SysMenuVo selectMenuById(Long menuId) {
        SysMenu menu = menuMapper.selectById(menuId);
        if (menu != null) {
            SysMenuVo menuVo = new SysMenuVo();
            BeanUtils.copyProperties(menu, menuVo);
            return menuVo;
        }
        return null;
    }

    @Override
    public Boolean updateMenuById(SysMenuBo bo) {
        SysMenu entity = new SysMenu();
        BeanUtils.copyProperties(bo, entity);
        return menuMapper.updateById(entity) > 0;
    }

    @Override
    public boolean checkMenuNameUnique(SysMenuBo menu) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getMenuName, menu.getMenuName())
            .eq(SysMenu::getParentId, menu.getParentId());
        
        if (ObjectUtil.isNotNull(menu.getMenuId())) {
            queryWrapper.ne(SysMenu::getMenuId, menu.getMenuId());
        }
        
        boolean exist = menuMapper.exists(queryWrapper);
        return !exist;
    }

    @Override
    public int updateMenu(SysMenuBo bo) {
        SysMenu entity = new SysMenu();
        BeanUtils.copyProperties(bo, entity);
        return menuMapper.updateById(entity);
    }

    @Override
    public int insertMenu(SysMenuBo bo) {
        SysMenu entity = new SysMenu();
        BeanUtils.copyProperties(bo, entity);
        return menuMapper.insert(entity);
    }

    @Override
    public int deleteMenuById(Long menuId) {
        return menuMapper.deleteById(menuId);
    }

    @Override
    public boolean hasChildByMenuId(Long menuId) {
        return menuMapper.exists(new LambdaQueryWrapper<SysMenu>()
            .eq(SysMenu::getParentId, menuId));
    }

    @Override
    public boolean checkMenuExistRole(Long menuId) {
        return roleMenuMapper.exists(new LambdaQueryWrapper<SysRoleMenu>()
            .eq(SysRoleMenu::getMenuId, menuId));
    }
}
package com.github.lisuiheng.astra.sys.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.exception.ServiceException;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.tenant.helper.TenantHelper;
import com.github.lisuiheng.astra.common.utils.MapstructUtils;
import com.github.lisuiheng.astra.sys.utils.SecurityUtils;
import com.github.lisuiheng.astra.sys.domain.entity.SysRole;
import com.github.lisuiheng.astra.sys.domain.entity.SysRoleMenu;
import com.github.lisuiheng.astra.sys.domain.entity.SysUserRole;
import com.github.lisuiheng.astra.sys.domain.vo.SysRoleVo;
import com.github.lisuiheng.astra.sys.domain.bo.SysRoleBo;
import com.github.lisuiheng.astra.sys.mapper.SysRoleMapper;
import com.github.lisuiheng.astra.sys.mapper.SysRoleMenuMapper;
import com.github.lisuiheng.astra.sys.mapper.SysUserRoleMapper;
import com.github.lisuiheng.astra.sys.service.ISysRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色 业务层处理
 *
 * @author lisuiheng
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysRoleServiceImpl implements ISysRoleService {

    private final SysRoleMapper baseMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysUserRoleMapper userRoleMapper;

    /**
     * 分页查询角色列表
     *
     * @param role      查询条件
     * @param pageQuery 分页参数
     * @return 角色分页列表
     */
    @Override
    public TableDataInfo<SysRoleVo> selectPageRoleList(SysRoleBo role, PageQuery pageQuery) {
        IPage<SysRoleVo> page = baseMapper.selectPageRoleList(pageQuery.build(), role);
        return TableDataInfo.build(page);
    }

    /**
     * 根据条件查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    @Override
    public List<SysRoleVo> selectRoleList(SysRoleBo role) {
        return baseMapper.selectRoleList(role);
    }

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<SysRoleVo> selectRolesByUserId(Long userId) {
        List<SysRoleVo> roles = baseMapper.selectRolesByUserId(userId);
        return roles;
    }

    /**
     * 根据角色ID查询角色信息
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    @Override
    public SysRoleVo selectRoleById(Long roleId) {
        return baseMapper.selectRoleById(roleId);
    }

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    @Override
    public void checkRoleAllowed(SysRoleBo role) {
        if (ObjectUtil.isNotNull(role.getRoleId()) && role.isSuperAdmin()) {
            throw new ServiceException("不允许操作超级管理员角色");
        }
    }

    /**
     * 校验角色是否有数据权限
     *
     * @param roleId 角色id
     */
    @Override
    public void checkRoleDataScope(Long roleId) {
        // 如果启用了租户模式，则进行租户数据权限校验
        if (TenantHelper.isEnable()) {
            LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysRole::getRoleId, roleId);
            // 添加租户条件（如果有）
            SecurityUtils.buildTenantConditionForSysRole().ifPresent(condition -> condition.accept(wrapper));
            List<SysRole> list = baseMapper.selectList(wrapper);
            if (CollUtil.isEmpty(list)) {
                throw new ServiceException("没有权限访问角色数据！");
            }
        }
    }

    /**
     * 新增保存角色信息
     *
     * @param bo 角色信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRole(SysRoleBo bo) {
        SysRole sysRole = MapstructUtils.convert(bo, SysRole.class);
        sysRole.setCreateBy(SecurityUtils.getUserId());
        // 设置租户ID
        if (TenantHelper.isEnable()) {
            sysRole.setTenantId(TenantHelper.getTenantId());
        }
        int rows = baseMapper.insert(sysRole);
        // 新增角色与菜单关联
        insertRoleMenu(bo);
        return rows;
    }

    /**
     * 修改保存角色信息
     *
     * @param bo 角色信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRole(SysRoleBo bo) {
        SysRole sysRole = MapstructUtils.convert(bo, SysRole.class);
        sysRole.setUpdateBy(SecurityUtils.getUserId());
        // 确保租户ID不会被修改
        if (TenantHelper.isEnable()) {
            sysRole.setTenantId(TenantHelper.getTenantId());
        }
        int rows = baseMapper.updateById(sysRole);
        if (rows > 0) {
            // 更新角色与菜单关联
            updateRoleMenu(bo);
        }
        return rows;
    }

    /**
     * 修改角色状态
     *
     * @param roleId 角色ID
     * @param status 角色状态
     * @return 结果
     */
    @Override
    public int updateRoleStatus(Long roleId, String status) {
        this.checkRoleAllowed(new SysRoleBo(roleId));
        SysRole sysRole = new SysRole();
        sysRole.setRoleId(roleId);
        sysRole.setStatus(status);
        sysRole.setUpdateBy(SecurityUtils.getUserId());
        // 确保租户ID不会被修改
        if (TenantHelper.isEnable()) {
            sysRole.setTenantId(TenantHelper.getTenantId());
        }
        return baseMapper.updateById(sysRole);
    }

    /**
     * 通过角色ID删除角色
     *
     * @param roleIds 角色ID集合
     * @return 结果
     */
    @Override
    public int deleteRoleByIds(Long[] roleIds) {
        for (Long roleId : roleIds) {
            checkRoleAllowed(new SysRoleBo(roleId));
            checkRoleDataScope(roleId);
        }
        return baseMapper.deleteBatchIds(Arrays.asList(roleIds));
    }

    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     */
    public void insertRoleMenu(SysRoleBo role) {
        this.updateRoleMenu(role);
    }

    /**
     * 更新角色菜单信息
     *
     * @param role 角色对象
     */
    public void updateRoleMenu(SysRoleBo role) {
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenuByRoleIds(Arrays.asList(role.getRoleId()));
        
        // 新增角色与菜单关联
        if (role.getMenuIds() != null && role.getMenuIds().length > 0) {
            List<SysRoleMenu> roleMenuList = Arrays.stream(role.getMenuIds())
                    .map(menuId -> {
                        SysRoleMenu rm = new SysRoleMenu();
                        rm.setRoleId(role.getRoleId());
                        rm.setMenuId(menuId);
                        return rm;
                    }).collect(Collectors.toList());
            if (!roleMenuList.isEmpty()) {
                roleMenuMapper.batchRoleMenu(roleMenuList);
            }
        }
    }

    @Override
    public long countUserRoleByRoleId(Long roleId) {
        return userRoleMapper.countUserRoleByRoleId(roleId);
    }

    @Override
    public void checkRoleDataScope(List<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return;
        }
        // 如果启用了租户模式，则进行租户数据权限校验
        if (TenantHelper.isEnable()) {
            LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(SysRole::getRoleId, roleIds);
            // 添加租户条件（如果有）
            SecurityUtils.buildTenantConditionForSysRole().ifPresent(condition -> condition.accept(wrapper));
            List<SysRole> list = baseMapper.selectList(wrapper);
            if (list.size() != roleIds.size()) {
                throw new ServiceException("没有权限访问角色数据！");
            }
        }
    }

    @Override
    public List<SysRoleVo> selectRoleByIds(List<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return CollUtil.newArrayList();
        }
        return baseMapper.selectRoleList(null).stream()
                .filter(role -> roleIds.contains(role.getRoleId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int authDataScope(SysRoleBo bo) {
        // 修改角色信息
        SysRole sysRole = MapstructUtils.convert(bo, SysRole.class);
        sysRole.setUpdateBy(SecurityUtils.getUserId());
        // 确保租户ID不会被修改
        if (TenantHelper.isEnable()) {
            sysRole.setTenantId(TenantHelper.getTenantId());
        }
        int rows = baseMapper.updateById(sysRole);

        // 删除角色与菜单关联信息
        roleMenuMapper.deleteRoleMenuByRoleIds(Arrays.asList(bo.getRoleId()));

        // 新增角色与菜单关联信息
        if (bo.getMenuIds() != null && bo.getMenuIds().length > 0) {
            List<SysRoleMenu> roleMenuList = Arrays.stream(bo.getMenuIds())
                    .map(menuId -> {
                        SysRoleMenu rm = new SysRoleMenu();
                        rm.setRoleId(bo.getRoleId());
                        rm.setMenuId(menuId);
                        return rm;
                    }).collect(Collectors.toList());
            if (!roleMenuList.isEmpty()) {
                roleMenuMapper.batchRoleMenu(roleMenuList);
            }
        }
        return rows;
    }

    @Override
    public List<SysRoleVo> selectRolesAuthByUserId(Long userId) {
        List<SysRoleVo> userRoles = baseMapper.selectRolesByUserId(userId);
        List<SysRoleVo> roles = selectRoleList(new SysRoleBo());
        
        // 使用HashSet提高查找效率
        Set<Long> userRoleIds = userRoles.stream()
            .map(SysRoleVo::getRoleId)
            .collect(Collectors.toSet());
        
        for (SysRoleVo role : roles) {
            role.setFlag(userRoleIds.contains(role.getRoleId()));
        }
        return roles;
    }

    @Override
    public Set<String> selectRolePermissionByUserId(Long userId) {
        List<SysRoleVo> perms = baseMapper.selectRolesByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRoleVo perm : perms) {
            if (ObjectUtil.isNotNull(perm)) {
                permsSet.addAll(StrUtil.splitTrim(perm.getRoleKey(), ","));
            }
        }
        return permsSet;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAuthUser(SysUserRole userRole) {
        if (SecurityUtils.getUserId().equals(userRole.getUserId())) {
            throw new ServiceException("不允许修改当前用户角色!");
        }
        int rows = userRoleMapper.delete(
            new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, userRole.getRoleId())
                .eq(SysUserRole::getUserId, userRole.getUserId()));
        if (rows > 0) {
            // 清理在线用户
            SecurityUtils.cleanOnlineUserByRole(userRole.getRoleId());
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAuthUsers(Long roleId, Long[] userIds) {
        List<Long> ids = Arrays.asList(userIds);
        if (ids.contains(SecurityUtils.getUserId())) {
            throw new ServiceException("不允许修改当前用户角色!");
        }
        int rows = userRoleMapper.delete(
            new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, roleId)
                .in(SysUserRole::getUserId, ids));
        if (rows > 0) {
            // 清理在线用户
            SecurityUtils.cleanOnlineUserByRole(roleId);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertAuthUsers(Long roleId, Long[] userIds) {
        List<SysUserRole> userRoleList = Arrays.stream(userIds)
            .map(userId -> {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                return ur;
            }).collect(Collectors.toList());
        
        int rows = userRoleMapper.batchUserRole(userRoleList);
        if (rows > 0) {
            // 清理在线用户
            SecurityUtils.cleanOnlineUserByRole(roleId);
        }
        return rows;
    }
}
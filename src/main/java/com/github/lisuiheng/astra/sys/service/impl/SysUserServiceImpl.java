package com.github.lisuiheng.astra.sys.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.hutool.core.bean.BeanUtil;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.exception.ServiceException;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.satoken.utils.LoginHelper;
import com.github.lisuiheng.astra.sys.domain.bo.SysUserBo;
import com.github.lisuiheng.astra.sys.domain.entity.SysUser;
import com.github.lisuiheng.astra.sys.domain.vo.SysPostVo;
import com.github.lisuiheng.astra.sys.domain.vo.SysRoleVo;
import com.github.lisuiheng.astra.sys.domain.vo.SysUserInfoVo;
import com.github.lisuiheng.astra.sys.domain.vo.SysUserVo;
import com.github.lisuiheng.astra.sys.mapper.SysMenuMapper;
import com.github.lisuiheng.astra.sys.mapper.SysPostMapper;
import com.github.lisuiheng.astra.sys.mapper.SysRoleMapper;
import com.github.lisuiheng.astra.sys.mapper.SysUserMapper;
import com.github.lisuiheng.astra.sys.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 用户 业务层处理
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class SysUserServiceImpl implements ISysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;
    private final SysPostMapper sysPostMapper;

    @Override
    public SysUserVo selectUserById(Long userId) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserId, userId));
        if (user == null) {
            return null;
        }
        // 将实体转换为VO
        SysUserVo userVo = new SysUserVo();
        userVo.setUserId(user.getUserId());
        userVo.setDeptId(user.getDeptId());
        userVo.setUserName(user.getUserName());
        userVo.setNickName(user.getNickName());
        userVo.setEmail(user.getEmail());
        userVo.setPhonenumber(user.getPhonenumber());
        userVo.setSex(user.getSex());
        userVo.setAvatar(user.getAvatar());
        userVo.setPassword(user.getPassword());
        userVo.setStatus(user.getStatus());
        userVo.setDelFlag(user.getDelFlag());
        userVo.setUserType(user.getUserType());
        userVo.setTenantId(user.getTenantId());
        userVo.setLoginIp(user.getLoginIp());
        userVo.setLoginDate(user.getLoginDate());
        userVo.setCreateBy(user.getCreateBy());
        userVo.setCreateTime(user.getCreateTime());
        userVo.setUpdateBy(user.getUpdateBy());
        userVo.setUpdateTime(user.getUpdateTime());
        userVo.setRemark(user.getRemark());
        
        // 查询用户的角色信息
        List<SysRoleVo> roles = sysRoleMapper.selectRolesByUserId(userId);
        userVo.setRoles(roles);
        
        return userVo;
    }

    @Override
    public List<SysRoleVo> selectRolesByUserId(Long userId) {
        return sysRoleMapper.selectRolesByUserId(userId);
    }

    @Override
    public Set<String> selectMenuListByUserId(Long userId) {
        return sysMenuMapper.selectMenuPermsByUserId(userId);
    }

    @Override
    public TableDataInfo<SysUserVo> selectAllocatedList(SysUserBo user, PageQuery pageQuery) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        
        var result = sysUserMapper.selectAllocatedList(page, user, null);
        return TableDataInfo.build(result);
    }

    @Override
    public TableDataInfo<SysUserVo> selectUnallocatedList(SysUserBo user, PageQuery pageQuery) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        
        var result = sysUserMapper.selectUnallocatedList(page, user, null);
        return TableDataInfo.build(result);
    }

    @Override
    public int updateUserProfile(SysUserBo user) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(user.getUserId());
        sysUser.setNickName(user.getNickName());
        sysUser.setEmail(user.getEmail());
        sysUser.setPhonenumber(user.getPhonenumber());
        sysUser.setSex(user.getSex());
        return sysUserMapper.updateById(sysUser);
    }

    @Override
    public TableDataInfo<SysUserVo> selectPageUserList(SysUserBo user, PageQuery pageQuery) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        
        var result = sysUserMapper.selectPageUserList(page, user.getUserName(), user.getStatus(), user.getPhonenumber(), user.getParams());
        
        TableDataInfo<SysUserVo> tableDataInfo = new TableDataInfo<>();
        tableDataInfo.setRows(result.getRecords());
        tableDataInfo.setTotal(result.getTotal());
        
        return tableDataInfo;
    }

    @Override
    public SysUserInfoVo selectSysUserInfoByUserId(Long userId) {
        SysUserInfoVo userInfoVo = new SysUserInfoVo();
        
        // 获取用户基本信息
        SysUserVo userVo = selectUserById(userId);
        userInfoVo.setUser(userVo);
        
        // 获取角色ID列表
        List<SysRoleVo> roles = sysRoleMapper.selectRolesByUserId(userId);
        List<Long> roleIds = roles.stream().map(SysRoleVo::getRoleId).toList();
        userInfoVo.setRoles(roles);
        userInfoVo.setRoleIds(roleIds);
        
        // 获取岗位ID列表
        List<SysPostVo> posts = sysPostMapper.selectPostsByUserId(userId);
        List<Long> postIds = posts.stream().map(SysPostVo::getPostId).toList();
        userInfoVo.setPosts(posts);
        userInfoVo.setPostIds(postIds);
        
        return userInfoVo;
    }

    @Override
    public boolean updateUserAvatar(Long userId, Long avatar) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setAvatar(String.valueOf(avatar));
        return sysUserMapper.updateById(sysUser) > 0;
    }

    @Override
    public int resetUserPwd(Long userId, String password) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setPassword(password);
        return sysUserMapper.updateById(sysUser);
    }

    @Override
    public String selectUserRoleGroup(Long userId) {
        List<SysRoleVo> roleVos = sysRoleMapper.selectRolesByUserId(userId);
        StringBuilder roleGroup = new StringBuilder();
        for (SysRoleVo roleVo : roleVos) {
            roleGroup.append(roleVo.getRoleName()).append(",");
        }
        if (roleGroup.length() > 0) {
            roleGroup.deleteCharAt(roleGroup.length() - 1);
        }
        return roleGroup.toString();
    }

    @Override
    public String selectUserPostGroup(Long userId) {
        List<String> posts = sysPostMapper.selectPostNamesByUserId(userId);
        StringBuilder postGroup = new StringBuilder();
        for (String post : posts) {
            postGroup.append(post).append(",");
        }
        if (postGroup.length() > 0) {
            postGroup.deleteCharAt(postGroup.length() - 1);
        }
        return postGroup.toString();
    }

    @Override
    public boolean checkPhoneUnique(SysUserBo user) {
        int count = sysUserMapper.checkPhoneUnique(user.getPhonenumber(), user.getUserId());
        return count <= 0;
    }

    @Override
    public boolean checkEmailUnique(SysUserBo user) {
        int count = sysUserMapper.checkEmailUnique(user.getEmail(), user.getUserId());
        return count <= 0;
    }

    @Override
    public int updateUser(SysUserBo user) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(user.getUserId());
        sysUser.setDeptId(user.getDeptId());
        sysUser.setUserName(user.getUserName());
        sysUser.setNickName(user.getNickName());
        sysUser.setEmail(user.getEmail());
        sysUser.setPhonenumber(user.getPhonenumber());
        sysUser.setSex(user.getSex());
        sysUser.setStatus(user.getStatus());
        sysUser.setRemark(user.getRemark());
        return sysUserMapper.updateById(sysUser);
    }

    @Override
    public void checkUserAllowed(Long userId) {
        if (ObjectUtil.isNotNull(userId) && LoginHelper.isSuperAdmin(userId)) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    @Override
    public void checkUserDataScope(Long userId) {
        if (ObjectUtil.isNull(userId)) {
            return;
        }
        if (LoginHelper.isSuperAdmin()) {
            return;
        }
        // 检查用户数据权限范围
        // 这里可以根据具体需求实现权限检查逻辑
    }

    @Override
    public boolean checkUserNameUnique(SysUserBo user) {
        int count = sysUserMapper.checkUserNameUnique(user.getUserName(), user.getUserId());
        return count <= 0;
    }

    @Override
    public int deleteUserById(Long userId) {
        checkUserAllowed(userId);
        checkUserDataScope(userId);
        return sysUserMapper.deleteById(userId);
    }

    @Override
    public int deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            checkUserAllowed(userId);
            checkUserDataScope(userId);
        }
        return sysUserMapper.deleteBatchIds(List.of(userIds));
    }

    @Override
    public List<SysUserVo> selectUserListByDeptId(Long deptId) {
        List<SysUser> users = sysUserMapper.selectUserListByDeptId(deptId);
        return BeanUtil.copyToList(users, SysUserVo.class);
    }
}
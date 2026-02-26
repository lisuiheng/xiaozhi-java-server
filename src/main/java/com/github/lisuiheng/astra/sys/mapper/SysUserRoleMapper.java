package com.github.lisuiheng.astra.sys.mapper;

import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import com.github.lisuiheng.astra.sys.domain.entity.SysUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户与角色关联表 Mapper接口
 *
 * @author lisuiheng
 */
public interface SysUserRoleMapper extends BaseMapperPlus<SysUserRole, SysUserRole> {

    /**
     * 通过用户ID删除用户和角色关联
     *
     * @param userId 用户ID
     * @return 结果
     */
    int deleteUserRoleByUserId(@Param("userId") Long userId);

    /**
     * 批量删除用户和角色关联
     *
     * @param ids 需要删除的数据ID集合
     * @return 结果
     */
    int deleteUserRole(@Param("ids") List<Long> ids);

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    int countUserRoleByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量新增用户角色信息
     *
     * @param userRoleList 用户角色列表
     * @return 结果
     */
    int batchUserRole(List<SysUserRole> userRoleList);
}
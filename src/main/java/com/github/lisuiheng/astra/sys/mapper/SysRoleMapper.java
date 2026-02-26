package com.github.lisuiheng.astra.sys.mapper;

import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.lisuiheng.astra.sys.domain.entity.SysRole;
import com.github.lisuiheng.astra.sys.domain.vo.SysRoleVo;
import com.github.lisuiheng.astra.sys.domain.bo.SysRoleBo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色表 Mapper接口
 *
 * @author lisuiheng
 */
public interface SysRoleMapper extends BaseMapperPlus<SysRole, SysRoleVo> {

    /**
     * 分页查询角色列表
     *
     * @param page 分页参数
     * @param role 查询条件
     * @return 角色分页列表
     */
    IPage<SysRoleVo> selectPageRoleList(Page<SysRoleVo> page, @Param("role") SysRoleBo role);

    /**
     * 根据条件查询角色数据
     *
     * @param role 查询条件
     * @return 角色数据集合信息
     */
    List<SysRoleVo> selectRoleList(@Param("role") SysRoleBo role);

    /**
     * 根据角色ID查询角色信息
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    SysRoleVo selectRoleById(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRoleVo> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 查询所有角色列表
     *
     * @return 角色列表
     */
    List<SysRoleVo> selectRoleAll();
}
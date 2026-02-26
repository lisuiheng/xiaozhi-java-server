package com.github.lisuiheng.astra.sys.mapper;

import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import com.github.lisuiheng.astra.sys.domain.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色与菜单关联表 Mapper接口
 *
 * @author lisuiheng
 */
public interface SysRoleMenuMapper extends BaseMapperPlus<SysRoleMenu, SysRoleMenu> {

    /**
     * 批量删除角色菜单关联信息
     *
     * @param roleIds 需要删除的数据ID集合
     * @return 结果
     */
    int deleteRoleMenuByRoleIds(@Param("roleIds") List<Long> roleIds);

    /**
     * 查询菜单使用数量
     *
     * @param menuIds 菜单ID集合
     * @return 结果
     */
    int checkMenuExistRole(@Param("menuIds") List<Long> menuIds);

    /**
     * 批量新增角色菜单信息
     *
     * @param roleMenuList 角色菜单列表
     * @return 结果
     */
    int batchRoleMenu(List<SysRoleMenu> roleMenuList);
}
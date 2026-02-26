package com.github.lisuiheng.astra.sys.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import com.github.lisuiheng.astra.common.utils.StreamUtils;
import com.github.lisuiheng.astra.common.utils.StringUtils;
import com.github.lisuiheng.astra.sys.domain.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 菜单数据层
 */
@Mapper
public interface SysMenuMapper extends BaseMapperPlus<SysMenu, SysMenu> {

    /**
     * 构建用户权限菜单 SQL
     *
     * <p>
     * 查询用户所属角色所拥有的菜单权限，用于权限判断、菜单加载等场景
     * </p>
     *
     * @param userId 用户ID
     * @return SQL 字符串，用于 inSql 条件
     */
    default String buildMenuByUserSql(Long userId) {
        return """
                select menu_id from sys_role_menu where role_id in (
                    select sur.role_id from sys_user_role sur
                        left join sys_role sr on sr.role_id = sur.role_id
                        where sur.user_id = %d and sr.status = '0'
                )
            """.formatted(userId);
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    default Set<String> selectMenuPermsByUserId(Long userId) {
        List<String> list = this.selectObjs(
            new LambdaQueryWrapper<SysMenu>()
                .select(SysMenu::getPerms)
                .inSql(SysMenu::getMenuId, this.buildMenuByUserSql(userId))
                .isNotNull(SysMenu::getPerms)
        );
        return new HashSet<>(StreamUtils.filter(list, StringUtils::isNotBlank));
    }
}
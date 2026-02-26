package com.github.lisuiheng.astra.sys.domain.bo;

import com.github.lisuiheng.astra.common.core.domain.BaseEntity;
import com.github.lisuiheng.astra.sys.domain.entity.SysMenu;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysMenu.class, reverseConvertGenerate = false)
public class SysMenuBo extends BaseEntity {

    private static final long serialVersionUID = 1L;
    
    /**
     * 菜单ID
     */
    private Long menuId;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 路由参数
     */
    private String queryParam;

    /**
     * 是否为外链（0是 1否）
     */
    private String isFrame;

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    private String isCache;

    /**
     * 类型（M目录 C菜单 F按钮）
     */
    private String menuType;

    /**
     * 显示状态（0显示 1隐藏）
     */
    private String visible;

    /**
     * 菜单状态（0正常 1停用）
     */
    private String status;

    /**
     * 权限字符串
     */
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 备注
     */
    private String remark;
}
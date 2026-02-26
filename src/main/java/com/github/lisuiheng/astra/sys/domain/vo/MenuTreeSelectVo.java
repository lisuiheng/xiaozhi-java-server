package com.github.lisuiheng.astra.sys.domain.vo;

import cn.hutool.core.lang.tree.Tree;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 角色菜单树选择 Vo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuTreeSelectVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 选中的菜单列表
     */
    private List<Long> checkedKeys;

    /**
     * 菜单下拉树结构列表
     */
    private List<Tree<Long>> menus;
}
package com.github.lisuiheng.astra.sys.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户和角色关联表
 *
 * @author lisuiheng
 */
@Data
@TableName("sys_user_role")
public class SysUserRole {

    /**
     * 用户ID
     */
    @TableId
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;
}
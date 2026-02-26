package com.github.lisuiheng.astra.sys.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.lisuiheng.astra.common.tenant.core.TenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.github.lisuiheng.astra.common.core.domain.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.lisuiheng.astra.common.core.entity.ITreeEntity;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 部门表 sys_dept
 *
 * @author 
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends TenantEntity implements ITreeEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    @TableId(value = "dept_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;

    /**
     * 父部门ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门类别编码
     */
    private String deptCategory;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 负责人
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long leader;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 部门状态:0正常,1停用
     */
    private String status;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;

    /**
     * 祖级列表
     */
    private String ancestors;

    /**
     * 子部门
     */
    @TableField(exist = false)
    private List<SysDept> children = new ArrayList<>();

    @Override
    public Long getId() {
        return this.deptId;
    }

    @Override
    public Long getParentId() {
        return this.parentId;
    }
}
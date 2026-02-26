package com.github.lisuiheng.astra.sys.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 部门视图对象 sys_dept
 *
 * @author 
 */
@Data
public class SysDeptVo {

    /**
     * 部门id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;

    /**
     * 父部门id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    /**
     * 父部门名称
     */
    private String parentName;

    /**
     * 祖级列表
     */
    private String ancestors;

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
     * 负责人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long leader;

    /**
     * 负责人
     */
    private String leaderName;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 部门状态（0正常 1停用）
     */
    private String status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 子部门
     */
    private List<SysDeptVo> children;
}
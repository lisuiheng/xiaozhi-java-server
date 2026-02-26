package com.github.lisuiheng.astra.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 工作流spel表达式定义实体类
 */
@Data
@TableName("flow_spel")
public class WorkflowSpel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId
    private Long id;

    /**
     * 组件名称
     */
    private String componentName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 参数
     */
    private String methodParams;

    /**
     * 预览spel表达式
     */
    private String viewSpel;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;

    /**
     * 创建部门
     */
    private Long createDept;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;
}
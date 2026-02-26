package com.github.lisuiheng.astra.sys.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.lisuiheng.astra.sys.domain.entity.SysPost;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.io.Serializable;
import java.util.Date;

/**
 * 岗位信息视图对象 sys_post
 *
 * @author Michelle.Chung
 */
@Data
@AutoMapper(target = SysPost.class)
public class SysPostVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 岗位ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long postId;

    /**
     * 部门id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;

    /**
     * 岗位编码
     */
    private String postCode;

    /**
     * 岗位名称
     */
    private String postName;

    /**
     * 岗位类别编码
     */
    private String postCategory;

    /**
     * 显示顺序
     */
    private Integer postSort;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 部门名
     */
    private String deptName;
}
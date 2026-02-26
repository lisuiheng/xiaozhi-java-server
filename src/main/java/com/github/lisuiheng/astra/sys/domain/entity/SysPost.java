package com.github.lisuiheng.astra.sys.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.lisuiheng.astra.sys.domain.vo.SysPostVo;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 岗位信息表 sys_post
 *
 * @author Michelle.Chung
 */
@Data
@NoArgsConstructor
@TableName("sys_post")
@AutoMapper(target = SysPostVo.class)
public class SysPost {

    /**
     * 岗位ID
     */
    @TableId
    private Long postId;

    /**
     * 部门id
     */
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
     * 岗位排序
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
}
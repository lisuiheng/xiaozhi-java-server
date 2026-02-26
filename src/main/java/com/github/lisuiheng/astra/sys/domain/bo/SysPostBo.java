package com.github.lisuiheng.astra.sys.domain.bo;

import com.github.lisuiheng.astra.common.core.domain.BaseEntity;
import com.github.lisuiheng.astra.sys.domain.entity.SysPost;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位信息业务对象 sys_post
 *
 * @author Michelle.Chung
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysPost.class, reverseConvertGenerate = false)
public class SysPostBo extends BaseEntity {

    /**
     * 岗位ID
     */
    private Long postId;

    /**
     * 部门id（单部门）
     */
    private Long deptId;

    /**
     * 归属部门id（部门树）
     */
    private Long belongDeptId;

    /**
     * 岗位编码
     */
    private String postCode;

    /**
     * 岗位名称
     */
    private String postName;

    /**
     * 岗位分类
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
}
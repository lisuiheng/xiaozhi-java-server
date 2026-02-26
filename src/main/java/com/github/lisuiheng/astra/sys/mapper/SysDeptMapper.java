package com.github.lisuiheng.astra.sys.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import com.github.lisuiheng.astra.sys.domain.SysDept;
import org.apache.ibatis.annotations.Param;

/**
 * 部门管理 数据层
 * 
 * @author 
 */
public interface SysDeptMapper extends BaseMapperPlus<SysDept, SysDept> {

    /**
     * 根据部门ID查询部门信息及其子部门数量
     * 
     * @param deptId 部门ID
     * @return 部门信息
     */
    SysDept selectDeptById(@Param("deptId") Long deptId);

    /**
     * 根据ID查询所有子部门数（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    long selectNormalChildrenDeptById(@Param("deptId") Long deptId);

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果
     */
    boolean checkDeptExistUser(@Param("deptId") Long deptId);

    /**
     * 统计指定部门ID的部门数量
     *
     * @param deptId 部门ID
     * @return 该部门ID的部门数量
     */
    default long countDeptById(Long deptId) {
        return this.selectCount(new LambdaQueryWrapper<SysDept>().eq(SysDept::getDeptId, deptId));
    }
}
package com.github.lisuiheng.astra.sys.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.lisuiheng.astra.sys.domain.SysDept;
import com.github.lisuiheng.astra.sys.domain.bo.SysDeptBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysDeptVo;

import java.util.List;

/**
 * 部门服务接口
 * 
 * @author 
 */
public interface ISysDeptService extends IService<SysDept> {

    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    List<SysDeptVo> selectDeptList(SysDept dept);

    /**
     * 查询部门树结构信息
     *
     * @param dept 部门信息
     * @return 部门树信息集合
     */
    List<Tree<Long>> selectDeptTreeList(SysDeptBo dept);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    List<Tree<Long>> buildDeptTreeSelect(List<SysDeptVo> depts);

    /**
     * 根据部门ID查询部门
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    SysDeptVo selectDeptById(Long deptId);

    /**
     * 通过部门ID串查询部门
     *
     * @param deptIds 部门id串
     * @return 部门列表信息
     */
    List<SysDeptVo> selectDeptByIds(List<Long> deptIds);

    /**
     * 根据ID查询所有子部门数（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    long selectNormalChildrenDeptById(Long deptId);

    /**
     * 是否存在部门子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    boolean hasChildByDeptId(Long deptId);

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    boolean checkDeptExistUser(Long deptId);

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    boolean checkDeptNameUnique(SysDeptBo dept);

    /**
     * 校验部门是否有数据权限
     *
     * @param deptId 部门id
     */
    void checkDeptDataScope(Long deptId);

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    int insertDept(SysDeptBo dept);

    /**
     * 修改保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    int updateDept(SysDeptBo dept);

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    int deleteDeptById(Long deptId);
}
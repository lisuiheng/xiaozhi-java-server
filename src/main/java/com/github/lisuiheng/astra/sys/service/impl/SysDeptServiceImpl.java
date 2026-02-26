package com.github.lisuiheng.astra.sys.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.lang.tree.parser.NodeParser;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.lisuiheng.astra.common.constant.CacheNames;
import com.github.lisuiheng.astra.common.exception.ServiceException;
import com.github.lisuiheng.astra.common.core.utils.TreeBuildUtils;
import com.github.lisuiheng.astra.common.satoken.utils.LoginHelper;
import com.github.lisuiheng.astra.common.utils.StringUtils;
import com.github.lisuiheng.astra.sys.domain.SysDept;
import com.github.lisuiheng.astra.sys.domain.bo.SysDeptBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysDeptVo;
import com.github.lisuiheng.astra.sys.mapper.SysDeptMapper;
import com.github.lisuiheng.astra.sys.service.ISysDeptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门服务实现类
 * 
 * @author 
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

    @Override
    public List<SysDeptVo> selectDeptList(SysDept dept) {
        LambdaQueryWrapper<SysDept> lqw = buildQueryWrapper(dept);
        List<SysDept> depts = this.list(lqw);
        // 将SysDept转换为SysDeptVo
        List<SysDeptVo> deptVos = TreeBuildUtils.build(depts, 0L, (SysDept dept1, List<SysDeptVo> children) -> {
            SysDeptVo vo = new SysDeptVo();
            vo.setDeptId(dept1.getDeptId());
            vo.setParentId(dept1.getParentId());
            vo.setAncestors(dept1.getAncestors());
            vo.setDeptName(dept1.getDeptName());
            vo.setDeptCategory(dept1.getDeptCategory());
            vo.setOrderNum(dept1.getOrderNum());
            vo.setLeader(dept1.getLeader());
            vo.setPhone(dept1.getPhone());
            vo.setEmail(dept1.getEmail());
            vo.setStatus(dept1.getStatus());
            vo.setCreateTime(dept1.getCreateTime());
            vo.setChildren(children); // 直接设置children
            return vo;
        });
        return deptVos;
    }

    @Override
    public List<Tree<Long>> selectDeptTreeList(SysDeptBo dept) {
        LambdaQueryWrapper<SysDept> lqw = buildQueryWrapper(dept);
        List<SysDept> depts = this.list(lqw);
        return buildDeptTreeSelect(depts.stream()
            .map(this::convertToVo)
            .toList());
    }

    @Override
    public List<Tree<Long>> buildDeptTreeSelect(List<SysDeptVo> depts) {
        // 配置TreeNodeConfig
        TreeNodeConfig config = new TreeNodeConfig();
        // 自定义属性名
        config.setIdKey("id");
        config.setParentIdKey("parentId");
        config.setWeightKey("orderNum");
        config.setNameKey("label");
        
        // 转换器
        NodeParser<SysDeptVo, Long> nodeParser = (deptVo, tree) -> {
            tree.setId(deptVo.getDeptId());
            tree.setParentId(deptVo.getParentId());
            tree.setWeight(deptVo.getOrderNum());
            tree.setName(deptVo.getDeptName());
            tree.putExtra("deptCategory", deptVo.getDeptCategory());
            tree.putExtra("leader", deptVo.getLeader());
            tree.putExtra("phone", deptVo.getPhone());
            tree.putExtra("email", deptVo.getEmail());
            tree.putExtra("status", deptVo.getStatus());
        };
        
        return TreeUtil.build(depts, 0L, config, nodeParser);
    }
    

    
    @Override
    @Cacheable(cacheNames = CacheNames.SYS_DEPT, key = "#deptIds")
    public List<SysDeptVo> selectDeptByIds(List<Long> deptIds) {
        LambdaQueryWrapper<SysDept> lqw = new LambdaQueryWrapper<>();
        lqw.in(SysDept::getDeptId, deptIds);
        lqw.eq(SysDept::getStatus, "0");
        lqw.orderByAsc(SysDept::getParentId).orderByAsc(SysDept::getOrderNum);
        List<SysDept> list = this.list(lqw);
        return TreeBuildUtils.build(list, 0L, (SysDept dept, List<SysDeptVo> children) -> {
            SysDeptVo vo = new SysDeptVo();
            vo.setDeptId(dept.getDeptId());
            vo.setParentId(dept.getParentId());
            vo.setAncestors(dept.getAncestors());
            vo.setDeptName(dept.getDeptName());
            vo.setDeptCategory(dept.getDeptCategory());
            vo.setOrderNum(dept.getOrderNum());
            vo.setLeader(dept.getLeader());
            vo.setPhone(dept.getPhone());
            vo.setEmail(dept.getEmail());
            vo.setStatus(dept.getStatus());
            vo.setCreateTime(dept.getCreateTime());
            vo.setChildren(children); // 直接设置children
            return vo;
        });
    }

    @Override
    public SysDeptVo selectDeptById(Long deptId) {
        SysDept sysDept = this.getById(deptId);
        SysDeptVo vo = new SysDeptVo();
        if (sysDept != null) {
            vo.setDeptId(sysDept.getDeptId());
            vo.setParentId(sysDept.getParentId());
            vo.setAncestors(sysDept.getAncestors());
            vo.setDeptName(sysDept.getDeptName());
            vo.setDeptCategory(sysDept.getDeptCategory());
            vo.setOrderNum(sysDept.getOrderNum());
            vo.setLeader(sysDept.getLeader());
            vo.setPhone(sysDept.getPhone());
            vo.setEmail(sysDept.getEmail());
            vo.setStatus(sysDept.getStatus());
            vo.setCreateTime(sysDept.getCreateTime());
        }
        return vo;
    }

    @Override
    public long selectNormalChildrenDeptById(Long deptId) {
        return baseMapper.selectNormalChildrenDeptById(deptId);
    }

    @Override
    public boolean hasChildByDeptId(Long deptId) {
        // 递归查询子部门
        LambdaQueryWrapper<SysDept> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysDept::getParentId, deptId);
        return this.count(lqw) > 0;
    }

    @Override
    public boolean checkDeptExistUser(Long deptId) {
        return baseMapper.checkDeptExistUser(deptId);
    }

    @Override
    public boolean checkDeptNameUnique(SysDeptBo deptBo) {
        boolean exist = this.exists(new LambdaQueryWrapper<SysDept>()
            .eq(SysDept::getDeptName, deptBo.getDeptName())
            .eq(SysDept::getParentId, deptBo.getParentId())
            .ne(ObjectUtil.isNotNull(deptBo.getDeptId()), SysDept::getDeptId, deptBo.getDeptId()));
        return !exist;
    }

    @Override
    public void checkDeptDataScope(Long deptId) {
        if (ObjectUtil.isNull(deptId)) {
            return;
        }
        if (LoginHelper.isSuperAdmin()) {
            return;
        }
        if (baseMapper.countDeptById(deptId) == 0) {
            throw new ServiceException("没有权限访问部门数据！");
        }
    }


    @Override
    public int insertDept(SysDeptBo deptBo) {
        SysDept dept = new SysDept();
        BeanUtil.copyProperties(deptBo, dept);
        if (ObjectUtil.isNull(dept.getParentId())) {
            dept.setParentId(0L);
        }
        // 获取父节点信息
        if (!dept.getParentId().equals(0L)) {
            SysDept parentDept = this.getById(dept.getParentId());
            if (ObjectUtil.isNotNull(parentDept)) {
                dept.setAncestors(parentDept.getAncestors() + "," + dept.getParentId());
            }
        } else {
            dept.setAncestors("0");
        }
        return baseMapper.insert(dept);
    }

    @Override
    public int updateDept(SysDeptBo deptBo) {
        SysDept dept = new SysDept();
        BeanUtil.copyProperties(deptBo, dept);
        SysDept oldDept = this.getById(dept.getDeptId());
        if (ObjectUtil.isNotNull(oldDept)) {
            String oldAncestors = oldDept.getAncestors();
            String newAncestors = dept.getAncestors();
            if (!StringUtils.equals(oldAncestors, newAncestors)) {
                // 更新子部门的祖级列表
                List<SysDept> childrenDepts = this.list(new LambdaQueryWrapper<SysDept>()
                    .likeRight(SysDept::getAncestors, oldDept.getDeptId()));
                for (SysDept childrenDept : childrenDepts) {
                    childrenDept.setAncestors(childrenDept.getAncestors().replaceFirst(oldAncestors, newAncestors));
                    baseMapper.updateById(childrenDept);
                }
            }
        }
        return baseMapper.updateById(dept);
    }
    


    @Override
    public int deleteDeptById(Long deptId) {
        // 检查是否存在子部门
        if (hasChildByDeptId(deptId)) {
            throw new ServiceException("存在下级部门,不允许删除");
        }
        // 检查部门是否关联用户
        if (checkDeptExistUser(deptId)) {
            throw new ServiceException("部门存在用户,不允许删除");
        }
        return baseMapper.deleteById(deptId);
    }

    private SysDeptVo convertToVo(SysDept dept) {
        SysDeptVo vo = new SysDeptVo();
        vo.setDeptId(dept.getDeptId());
        vo.setParentId(dept.getParentId());
        vo.setAncestors(dept.getAncestors());
        vo.setDeptName(dept.getDeptName());
        vo.setDeptCategory(dept.getDeptCategory());
        vo.setOrderNum(dept.getOrderNum());
        vo.setLeader(dept.getLeader());
        vo.setPhone(dept.getPhone());
        vo.setEmail(dept.getEmail());
        vo.setStatus(dept.getStatus());
        vo.setCreateTime(dept.getCreateTime());
        return vo;
    }
    
    private LambdaQueryWrapper<SysDept> buildQueryWrapper(SysDept dept) {
        LambdaQueryWrapper<SysDept> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysDept::getStatus, "0");
        if (StringUtils.isNotBlank(dept.getDeptName())) {
            lqw.like(SysDept::getDeptName, dept.getDeptName());
        }
        if (dept.getParentId() != null) {
            lqw.eq(SysDept::getParentId, dept.getParentId());
        }
        lqw.orderByAsc(SysDept::getParentId).orderByAsc(SysDept::getOrderNum);
        return lqw;
    }
    
    private LambdaQueryWrapper<SysDept> buildQueryWrapper(SysDeptBo bo) {
        LambdaQueryWrapper<SysDept> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysDept::getStatus, "0");
        if (StringUtils.isNotBlank(bo.getDeptName())) {
            lqw.like(SysDept::getDeptName, bo.getDeptName());
        }
        if (bo.getParentId() != null) {
            lqw.eq(SysDept::getParentId, bo.getParentId());
        }
        lqw.orderByAsc(SysDept::getParentId).orderByAsc(SysDept::getOrderNum);
        return lqw;
    }
}
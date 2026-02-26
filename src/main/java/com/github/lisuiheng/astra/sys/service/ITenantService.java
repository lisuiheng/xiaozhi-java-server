package com.github.lisuiheng.astra.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.sys.domain.entity.Tenant;
import com.github.lisuiheng.astra.sys.domain.vo.SysTenantVo;
import com.github.lisuiheng.astra.sys.domain.bo.TenantBo;

import java.util.Collection;
import java.util.List;

/**
 * 租户表 服务层
 * 
 * @author Qoder
 */
public interface ITenantService extends IService<Tenant> {

    /**
     * 查询租户
     * 
     * @param id 租户ID
     * @return 租户信息
     */
    SysTenantVo queryById(Long id);

    /**
     * 基于租户ID查询租户
     */
    SysTenantVo queryByTenantId(String tenantId);

    /**
     * 租户列表
     * 
     * @param tenant 租户信息
     * @param pageQuery 分页查询对象
     * @return 租户集合
     */
    TableDataInfo<SysTenantVo> queryPageList(Tenant tenant, PageQuery pageQuery);

    /**
     * 查询租户列表
     */
    List<SysTenantVo> queryList(Tenant tenant);

    /**
     * 新增租户
     */
    Boolean insertByBo(TenantBo bo);

    /**
     * 修改租户
     */
    Boolean updateByBo(TenantBo bo);

    /**
     * 修改租户状态
     */
    int updateTenantStatus(TenantBo bo);

    /**
     * 校验租户是否允许操作
     */
    void checkTenantAllowed(String tenantId);

    /**
     * 校验并批量删除租户信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 校验企业名称是否唯一
     */
    boolean checkCompanyNameUnique(TenantBo bo);
}
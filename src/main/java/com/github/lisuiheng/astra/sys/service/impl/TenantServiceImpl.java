package com.github.lisuiheng.astra.sys.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.lisuiheng.astra.common.constant.TenantConstants;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.exception.ServiceException;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.utils.StringUtils;
import com.github.lisuiheng.astra.sys.domain.entity.Tenant;
import com.github.lisuiheng.astra.sys.domain.bo.TenantBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysTenantVo;
import com.github.lisuiheng.astra.sys.mapper.TenantMapper;
import com.github.lisuiheng.astra.sys.service.ITenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 租户表 服务实现类
 * 
 * @author Qoder
 */
@Service
@RequiredArgsConstructor
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements ITenantService {

    private final TenantMapper tenantMapper;

    @Override
    @Cacheable(cacheNames = "sys_tenant", key = "#id")
    public SysTenantVo queryById(Long id) {
        Tenant tenant = tenantMapper.selectById(id);
        return BeanUtil.copyProperties(tenant, SysTenantVo.class);
    }

    @Override
    @Cacheable(cacheNames = "sys_tenant", key = "#tenantId")
    public SysTenantVo queryByTenantId(String tenantId) {
        Tenant tenant = tenantMapper.selectOne(new LambdaQueryWrapper<Tenant>().eq(Tenant::getTenantId, tenantId));
        return BeanUtil.copyProperties(tenant, SysTenantVo.class);
    }

    @Override
    public TableDataInfo<SysTenantVo> queryPageList(Tenant tenant, PageQuery pageQuery) {
        LambdaQueryWrapper<Tenant> lqw = buildQueryWrapper(tenant);
        Page<Tenant> page = tenantMapper.selectPage(pageQuery.build(), lqw);
        Page<SysTenantVo> voPage = new Page<>();
        voPage.setCurrent(page.getCurrent());
        voPage.setSize(page.getSize());
        voPage.setTotal(page.getTotal());
        voPage.setRecords(BeanUtil.copyToList(page.getRecords(), SysTenantVo.class));
        return TableDataInfo.build(voPage);
    }

    @Override
    public List<SysTenantVo> queryList(Tenant tenant) {
        LambdaQueryWrapper<Tenant> lqw = buildQueryWrapper(tenant);
        List<Tenant> tenants = tenantMapper.selectList(lqw);
        return BeanUtil.copyToList(tenants, SysTenantVo.class);
    }

    private LambdaQueryWrapper<Tenant> buildQueryWrapper(Tenant tenant) {
        LambdaQueryWrapper<Tenant> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotNull(tenant.getId()), Tenant::getId, tenant.getId());
        lqw.eq(StringUtils.isNotBlank(tenant.getTenantId()), Tenant::getTenantId, tenant.getTenantId());
        lqw.like(StringUtils.isNotBlank(tenant.getContactUserName()), Tenant::getContactUserName, tenant.getContactUserName());
        lqw.eq(StringUtils.isNotBlank(tenant.getContactPhone()), Tenant::getContactPhone, tenant.getContactPhone());
        lqw.like(StringUtils.isNotBlank(tenant.getCompanyName()), Tenant::getCompanyName, tenant.getCompanyName());
        lqw.eq(StringUtils.isNotBlank(tenant.getLicenseNumber()), Tenant::getLicenseNumber, tenant.getLicenseNumber());
        lqw.eq(StringUtils.isNotBlank(tenant.getAddress()), Tenant::getAddress, tenant.getAddress());
        lqw.eq(StringUtils.isNotBlank(tenant.getIntro()), Tenant::getIntro, tenant.getIntro());
        lqw.like(StringUtils.isNotBlank(tenant.getDomain()), Tenant::getDomain, tenant.getDomain());
        lqw.eq(tenant.getPackageId() != null, Tenant::getPackageId, tenant.getPackageId());
        lqw.eq(tenant.getExpireTime() != null, Tenant::getExpireTime, tenant.getExpireTime());
        lqw.eq(tenant.getAccountCount() != null, Tenant::getAccountCount, tenant.getAccountCount());
        lqw.eq(StringUtils.isNotBlank(tenant.getStatus()), Tenant::getStatus, tenant.getStatus());
        lqw.orderByAsc(Tenant::getId);
        return lqw;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(TenantBo bo) {
        Tenant add = BeanUtil.copyProperties(bo, Tenant.class);

        // 获取所有租户编号
        List<Tenant> allTenants = tenantMapper.selectList(new LambdaQueryWrapper<Tenant>().select(Tenant::getTenantId));
        List<String> tenantIds = allTenants.stream().map(Tenant::getTenantId).collect(Collectors.toList());
        String tenantId = generateTenantId(tenantIds);
        add.setTenantId(tenantId);
        boolean flag = tenantMapper.insert(add) > 0;
        if (!flag) {
            throw new ServiceException("创建租户失败");
        }
        bo.setId(add.getId());

        return flag;
    }

    @Override
    @CacheEvict(cacheNames = "sys_tenant", key = "#bo.tenantId")
    public Boolean updateByBo(TenantBo bo) {
        Tenant tenant = BeanUtil.copyProperties(bo, Tenant.class);
        tenant.setTenantId(null);
        tenant.setPackageId(null);
        return tenantMapper.updateById(tenant) > 0;
    }

    @Override
    @CacheEvict(cacheNames = "sys_tenant", key = "#bo.tenantId")
    public int updateTenantStatus(TenantBo bo) {
        Tenant tenant = new Tenant();
        tenant.setId(bo.getId());
        tenant.setStatus(bo.getStatus());
        return tenantMapper.updateById(tenant);
    }

    @Override
    public void checkTenantAllowed(String tenantId) {
        if (ObjectUtil.isNotNull(tenantId) && TenantConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            throw new ServiceException("不允许操作管理租户");
        }
    }

    @Override
    @CacheEvict(cacheNames = "sys_tenant", allEntries = true)
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
            if (ids.contains(TenantConstants.SUPER_ADMIN_ID)) {
                throw new ServiceException("超管租户不能删除");
            }
        }
        return tenantMapper.deleteByIds(ids) > 0;
    }

    @Override
    public boolean checkCompanyNameUnique(TenantBo bo) {
        boolean exist = tenantMapper.exists(new LambdaQueryWrapper<Tenant>()
            .eq(Tenant::getCompanyName, bo.getCompanyName())
            .ne(ObjectUtil.isNotNull(bo.getTenantId()), Tenant::getTenantId, bo.getTenantId()));
        return !exist;
    }

    /**
     * 生成租户id
     *
     * @param tenantIds 已有租户id列表
     * @return 租户id
     */
    private String generateTenantId(List<String> tenantIds) {
        // 随机生成6位
        String numbers = RandomUtil.randomNumbers(6);
        // 判断是否存在，如果存在则重新生成
        if (tenantIds.contains(numbers)) {
            return generateTenantId(tenantIds);
        }
        return numbers;
    }
}
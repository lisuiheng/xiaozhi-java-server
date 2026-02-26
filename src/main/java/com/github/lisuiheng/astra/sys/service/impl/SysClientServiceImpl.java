package com.github.lisuiheng.astra.sys.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.lisuiheng.astra.common.constant.CacheNames;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.utils.MapstructUtils;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.utils.StringUtils;
import com.github.lisuiheng.astra.sys.domain.SysClient;
import com.github.lisuiheng.astra.sys.domain.bo.SysClientBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysClientVo;
import com.github.lisuiheng.astra.sys.mapper.SysClientMapper;
import com.github.lisuiheng.astra.sys.service.ISysClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 客户端管理Service业务层处理
 *
 * @author Michelle.Chung
 * @date 2023-06-18
 */
@RequiredArgsConstructor
@Service
public class SysClientServiceImpl implements ISysClientService {
    private final SysClientMapper baseMapper;

    @Override
    @Cacheable(cacheNames = CacheNames.SYS_CLIENT, key = "#clientId")
    public SysClientVo queryByClientId(String clientId) {
        return baseMapper.selectVoOne(new LambdaQueryWrapper<SysClient>().eq(SysClient::getClientId, clientId));
    }

    @Override
    public SysClientVo queryById(Long id) {
        SysClientVo vo = baseMapper.selectVoById(id);
        vo.setGrantTypeList(StringUtils.splitList(vo.getGrantType()));
        return vo;
    }

    @Override
    public TableDataInfo<SysClientVo> queryPageList(SysClientBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysClient> lqw = buildQueryWrapper(bo);
        Page<SysClientVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        result.getRecords().forEach(r -> r.setGrantTypeList(StringUtils.splitList(r.getGrantType())));
        return TableDataInfo.build(result);
    }

    @Override
    public List<SysClientVo> queryList(SysClientBo bo) {
        LambdaQueryWrapper<SysClient> lqw = buildQueryWrapper(bo);
        List<SysClientVo> result = baseMapper.selectVoList(lqw);
        result.forEach(r -> r.setGrantTypeList(StringUtils.splitList(r.getGrantType())));
        return result;
    }

    private LambdaQueryWrapper<SysClient> buildQueryWrapper(SysClientBo bo) {
        LambdaQueryWrapper<SysClient> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getId()), SysClient::getId, bo.getId());
        lqw.eq(StringUtils.isNotBlank(bo.getClientId()), SysClient::getClientId, bo.getClientId());
        lqw.eq(StringUtils.isNotBlank(bo.getClientKey()), SysClient::getClientKey, bo.getClientKey());
        lqw.eq(StringUtils.isNotBlank(bo.getClientSecret()), SysClient::getClientSecret, bo.getClientSecret());
        lqw.eq(StringUtils.isNotBlank(bo.getGrantType()), SysClient::getGrantType, bo.getGrantType());
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceType()), SysClient::getDeviceType, bo.getDeviceType());
        lqw.eq(ObjectUtil.isNotNull(bo.getActiveTimeout()), SysClient::getActiveTimeout, bo.getActiveTimeout());
        lqw.eq(ObjectUtil.isNotNull(bo.getTimeout()), SysClient::getTimeout, bo.getTimeout());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), SysClient::getStatus, bo.getStatus());
        lqw.orderByAsc(SysClient::getId);
        return lqw;
    }

    @Override
    @CacheEvict(cacheNames = CacheNames.SYS_CLIENT, key = "#bo.clientId")
    public Boolean insertByBo(SysClientBo bo) {
        SysClient add = MapstructUtils.convert(bo, SysClient.class);
        add.setGrantType(CollUtil.join(bo.getGrantTypeList(), StringUtils.SEPARATOR));
        String clientKey = bo.getClientKey();
        String clientSecret = bo.getClientSecret();
        add.setClientId(SecureUtil.md5(clientKey + clientSecret));
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    @Override
    @CacheEvict(cacheNames = CacheNames.SYS_CLIENT, key = "#bo.clientId")
    public Boolean updateByBo(SysClientBo bo) {
        SysClient update = MapstructUtils.convert(bo, SysClient.class);
        update.setGrantType(CollUtil.join(bo.getGrantTypeList(), StringUtils.SEPARATOR));
        return baseMapper.updateById(update) > 0;
    }

    @Override
    @CacheEvict(cacheNames = CacheNames.SYS_CLIENT, key = "#clientId")
    public int updateClientStatus(String clientId, String status) {
        return baseMapper.update(null,
            new LambdaUpdateWrapper<SysClient>()
                .set(SysClient::getStatus, status)
                .eq(SysClient::getClientId, clientId));
    }

    @Override
    @CacheEvict(cacheNames = CacheNames.SYS_CLIENT, allEntries = true)
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        return baseMapper.deleteByIds(ids) > 0;
    }

    @Override
    public boolean checkClickKeyUnique(SysClientBo client) {
        boolean exist = baseMapper.exists(new LambdaQueryWrapper<SysClient>()
            .eq(SysClient::getClientKey, client.getClientKey())
            .ne(ObjectUtil.isNotNull(client.getId()), SysClient::getId, client.getId()));
        return !exist;
    }
}
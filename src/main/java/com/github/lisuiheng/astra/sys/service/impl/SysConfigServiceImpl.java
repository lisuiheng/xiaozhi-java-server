package com.github.lisuiheng.astra.sys.service.impl;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.exception.ServiceException;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.tenant.helper.TenantHelper;
import com.github.lisuiheng.astra.sys.domain.SysConfig;
import com.github.lisuiheng.astra.sys.domain.bo.SysConfigBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysConfigVo;
import com.github.lisuiheng.astra.sys.mapper.SysConfigMapper;
import com.github.lisuiheng.astra.sys.service.ISysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统配置服务实现
 *
 * @author Qoder
 */
@RequiredArgsConstructor
@Service
public class SysConfigServiceImpl implements ISysConfigService {
    private final SysConfigMapper baseMapper;
    
    @Override
    public boolean selectRegisterEnabled(String tenantId) {
        String configValue = TenantHelper.dynamic(tenantId, () ->
            this.selectConfigByKey("sys.account.registerUser")
        );
        return Convert.toBool(configValue);
    }
    
    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey) {
        SysConfig retConfig = baseMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
            .eq(SysConfig::getConfigKey, configKey));
        return retConfig != null ? retConfig.getConfigValue() : "";
    }
    
    @Override
    public TableDataInfo<SysConfigVo> selectPageConfigList(SysConfigBo config, PageQuery pageQuery) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(config.getConfigName() != null, SysConfig::getConfigName, config.getConfigName())
                .eq(config.getConfigKey() != null, SysConfig::getConfigKey, config.getConfigKey())
                .eq(config.getConfigType() != null, SysConfig::getConfigType, config.getConfigType());
            
        Page<SysConfig> page = baseMapper.selectPage(pageQuery.build(), wrapper);
        List<SysConfigVo> voList = BeanUtil.copyToList(page.getRecords(), SysConfigVo.class);
            
        // 创建一个新的 Page 对象用于返回 VO
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysConfigVo> voPage = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
        voPage.setRecords(voList);
        voPage.setTotal(page.getTotal());
        voPage.setCurrent(page.getCurrent());
        voPage.setSize(page.getSize());
            
        return TableDataInfo.build(voPage);
    }
    
    @Override
    public List<SysConfig> selectConfigList(SysConfig config) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(config.getConfigName() != null, SysConfig::getConfigName, config.getConfigName())
                .eq(config.getConfigKey() != null, SysConfig::getConfigKey, config.getConfigKey())
                .eq(config.getConfigType() != null, SysConfig::getConfigType, config.getConfigType());

        return baseMapper.selectList(wrapper);
    }
    
    @Override
    public SysConfigVo selectConfigById(Long configId) {
        return BeanUtil.toBean(baseMapper.selectById(configId), SysConfigVo.class);
    }
    
    @Override
    public String insertConfig(SysConfigBo bo) {
        if (!checkConfigKeyUnique(bo)) {
            throw new ServiceException("新增参数'" + bo.getConfigName() + "'失败，参数键名已存在");
        }
        SysConfig config = BeanUtil.toBean(bo, SysConfig.class);
        int row = baseMapper.insert(config);
        if (row > 0) {
            return config.getConfigValue();
        }
        throw new ServiceException("操作失败");
    }
    
    @Override
    public String updateConfig(SysConfigBo bo) {
        int row = 0;
        SysConfig config = BeanUtil.toBean(bo, SysConfig.class);
        if (config.getConfigId() != null) {
            SysConfig temp = baseMapper.selectById(config.getConfigId());
            row = baseMapper.updateById(config);
        } else {
            row = baseMapper.update(config, new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, config.getConfigKey()));
        }
        if (row > 0) {
            return config.getConfigValue();
        }
        throw new ServiceException("操作失败");
    }
    
    @Override
    public void deleteConfigByIds(List<Long> configIds) {
        baseMapper.deleteBatchIds(configIds);
    }
    
    @Override
    public void resetConfigCache() {
        // 在当前实现中暂不使用缓存，所以直接返回
    }
    
    @Override
    public boolean checkConfigKeyUnique(SysConfigBo config) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, config.getConfigKey());
        if (ObjectUtil.isNotNull(config.getConfigId())) {
            wrapper.ne(SysConfig::getConfigId, config.getConfigId());
        }
        List<SysConfig> sysConfigs = baseMapper.selectList(wrapper);
        return sysConfigs.size() <= 0;
    }
    
    private LambdaQueryWrapper<SysConfig> buildQueryWrapper(SysConfigBo bo) {
        LambdaQueryWrapper<SysConfig> lqw = Wrappers.lambdaQuery();
        lqw.like(StrUtil.isNotBlank(bo.getConfigName()), SysConfig::getConfigName, bo.getConfigName());
        lqw.eq(StrUtil.isNotBlank(bo.getConfigType()), SysConfig::getConfigType, bo.getConfigType());
        lqw.like(StrUtil.isNotBlank(bo.getConfigKey()), SysConfig::getConfigKey, bo.getConfigKey());
        return lqw;
    }
    
    @Override
    public String updateConfigByKey(SysConfigBo bo) {
        SysConfig config = BeanUtil.toBean(bo, SysConfig.class);
        int row = baseMapper.update(config, new LambdaQueryWrapper<SysConfig>()
            .eq(SysConfig::getConfigKey, config.getConfigKey()));
        if (row > 0) {
            return config.getConfigValue();
        }
        throw new ServiceException("操作失败");
    }
}
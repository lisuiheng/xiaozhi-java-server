package com.github.lisuiheng.astra.sys.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.lisuiheng.astra.common.core.constant.CacheConstants;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.redis.utils.RedisUtils;
import com.github.lisuiheng.astra.sys.domain.SysDictType;
import com.github.lisuiheng.astra.sys.domain.bo.SysDictTypeBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysDictDataVo;
import com.github.lisuiheng.astra.sys.domain.vo.SysDictTypeVo;
import com.github.lisuiheng.astra.sys.mapper.SysDictTypeMapper;
import com.github.lisuiheng.astra.sys.service.ISysDictDataService;
import com.github.lisuiheng.astra.sys.service.ISysDictTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 字典类型 业务层处理
 *
 * @author Qoder
 */
@RequiredArgsConstructor
@Service
public class SysDictTypeServiceImpl implements ISysDictTypeService {

    private final SysDictTypeMapper baseMapper;
    private final ISysDictDataService dictDataService;

    @Override
    public TableDataInfo<SysDictTypeVo> selectPageDictTypeList(SysDictTypeBo dictType, PageQuery pageQuery) {
        LambdaQueryWrapper<SysDictType> lqw = buildQueryWrapper(dictType);
        var page = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public List<SysDictTypeVo> selectDictTypeList(SysDictTypeBo dictType) {
        LambdaQueryWrapper<SysDictType> lqw = buildQueryWrapper(dictType);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysDictType> buildQueryWrapper(SysDictTypeBo bo) {
        LambdaQueryWrapper<SysDictType> lqw = Wrappers.lambdaQuery();
        lqw.likeRight(ObjectUtil.isNotNull(bo.getDictName()), SysDictType::getDictName, bo.getDictName());
        lqw.likeRight(ObjectUtil.isNotNull(bo.getDictType()), SysDictType::getDictType, bo.getDictType());
        lqw.orderByAsc(SysDictType::getDictType);
        return lqw;
    }

    @Override
    public List<SysDictTypeVo> selectDictTypeAll() {
        return baseMapper.selectVoList();
    }

    @Override
    public SysDictTypeVo selectDictTypeById(Long dictId) {
        return baseMapper.selectVoById(dictId);
    }

    @Override
    public SysDictTypeVo selectDictTypeByType(String dictType) {
        return baseMapper.selectVoOne(new LambdaQueryWrapper<SysDictType>().eq(SysDictType::getDictType, dictType));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertDictType(SysDictTypeBo bo) {
        SysDictType dictType = bo.toEntity();
        boolean result = baseMapper.insert(dictType) > 0;
        if (result) {
            RedisUtils.deleteObject(CacheConstants.SYS_DICT_KEY + dictType.getDictType());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateDictType(SysDictTypeBo bo) {
        SysDictType dictType = bo.toEntity();
        // 旧的字典类型
        String oldDictType = baseMapper.selectVoById(dictType.getDictId()).getDictType();
        boolean result = baseMapper.updateById(dictType) > 0;
        if (result) {
            // 删除缓存
            RedisUtils.deleteObject(CacheConstants.SYS_DICT_KEY + oldDictType);
            // 如果类型被修改，则更新所有相关字典数据的类型
            if (!oldDictType.equals(dictType.getDictType())) {
                dictDataService.updateDictDataType(oldDictType, dictType.getDictType());
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictTypeByIds(List<Long> dictIds) {
        List<SysDictType> list = baseMapper.selectByIds(dictIds);
        for (SysDictType dictType : list) {
            // 检查字典类型下是否有关联的字典数据
            boolean hasAssignedData = dictDataService.existsDictDataByType(dictType.getDictType());
            if (hasAssignedData) {
                throw new RuntimeException(dictType.getDictName() + "已分配,不能删除");
            }
        }
        // 删除字典类型
        baseMapper.deleteByIds(dictIds);
        // 删除对应缓存
        for (SysDictType dictType : list) {
            RedisUtils.deleteObject(CacheConstants.SYS_DICT_KEY + dictType.getDictType());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictTypeById(Long dictId) {
        SysDictTypeVo dictType = this.selectDictTypeById(dictId);
        if (dictType != null) {
            // 删除缓存
            RedisUtils.deleteObject(CacheConstants.SYS_DICT_KEY + dictType.getDictType());
            // 删除此类型下的所有字典数据
            dictDataService.deleteDictDataByType(dictType.getDictType());
        }
        baseMapper.deleteById(dictId);
    }

    @Override
    public Boolean checkDictTypeUnique(SysDictTypeBo dictType) {
        boolean exist = baseMapper.exists(new LambdaQueryWrapper<SysDictType>()
                .eq(SysDictType::getDictType, dictType.getDictType())
                .ne(ObjectUtil.isNotNull(dictType.getDictId()), SysDictType::getDictId, dictType.getDictId()));
        return !exist;
    }

    @Override
    public List<SysDictDataVo> selectDictDataByType(String dictType) {
        return dictDataService.selectDictDataByType(dictType);
    }

    @Override
    public void resetDictCache() {
        // 清除所有字典缓存
        RedisUtils.deleteKeys(CacheConstants.SYS_DICT_KEY + "*");
    }
}
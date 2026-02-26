package com.github.lisuiheng.astra.sys.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.lisuiheng.astra.common.core.constant.CacheConstants;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.redis.utils.RedisUtils;
import com.github.lisuiheng.astra.sys.domain.SysDictData;
import com.github.lisuiheng.astra.sys.domain.bo.SysDictDataBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysDictDataVo;
import com.github.lisuiheng.astra.sys.mapper.SysDictDataMapper;
import com.github.lisuiheng.astra.sys.service.ISysDictDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典 业务层处理
 *
 * @author Qoder
 */
@RequiredArgsConstructor
@Service
public class SysDictDataServiceImpl implements ISysDictDataService {

    private final SysDictDataMapper baseMapper;

    @Override
    public TableDataInfo<SysDictDataVo> selectPageDictDataList(SysDictDataBo dictData, PageQuery pageQuery) {
        LambdaQueryWrapper<SysDictData> lqw = buildQueryWrapper(dictData);
        var page = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public List<SysDictDataVo> selectDictDataList(SysDictDataBo dictData) {
        LambdaQueryWrapper<SysDictData> lqw = buildQueryWrapper(dictData);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysDictData> buildQueryWrapper(SysDictDataBo bo) {
        LambdaQueryWrapper<SysDictData> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getDictType()), SysDictData::getDictType, bo.getDictType());
        lqw.likeRight(ObjectUtil.isNotNull(bo.getDictLabel()), SysDictData::getDictLabel, bo.getDictLabel());
        lqw.orderByAsc(SysDictData::getDictSort);
        return lqw;
    }

    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        return baseMapper.selectOne(new LambdaQueryWrapper<SysDictData>()
                .select(SysDictData::getDictLabel)
                .eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getDictValue, dictValue))
            .getDictLabel();
    }

    @Override
    public SysDictDataVo selectDictDataById(Long dictCode) {
        return baseMapper.selectVoById(dictCode);
    }

    @Override
    public List<SysDictDataVo> selectDictDataByType(String dictType) {
        return baseMapper.selectDictDataByType(dictType);
    }

    @Override
    public Boolean insertDictData(SysDictDataBo bo) {
        SysDictData data = bo.toEntity();
        RedisUtils.deleteObject(CacheConstants.SYS_DICT_KEY + data.getDictType());
        return baseMapper.insert(data) > 0;
    }

    @Override
    public Boolean updateDictData(SysDictDataBo bo) {
        SysDictData data = bo.toEntity();
        RedisUtils.deleteObject(CacheConstants.SYS_DICT_KEY + data.getDictType());
        return baseMapper.updateById(data) > 0;
    }

    @Override
    public Boolean deleteDictDataByIds(Long[] dictCodes) {
        List<SysDictDataVo> dictDataList = this.selectDictDataList(new SysDictDataBo());
        if (CollUtil.isNotEmpty(dictDataList)) {
            // 删除缓存
            dictDataList.forEach(dictData -> RedisUtils.deleteObject(CacheConstants.SYS_DICT_KEY + dictData.getDictType()));
        }
        return baseMapper.deleteBatchIds(java.util.Arrays.asList(dictCodes)) > 0;
    }

    @Override
    public Boolean deleteDictDataById(Long dictCode) {
        SysDictDataVo dictData = this.selectDictDataById(dictCode);
        if (dictData != null) {
            RedisUtils.deleteObject(CacheConstants.SYS_DICT_KEY + dictData.getDictType());
        }
        return baseMapper.deleteById(dictCode) > 0;
    }

    @Override
    public Boolean updateDictDataType(String oldDictType, String newDictType) {
        LambdaQueryWrapper<SysDictData> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysDictData::getDictType, oldDictType);
        SysDictData data = new SysDictData();
        data.setDictType(newDictType);
        boolean result = baseMapper.update(data, lqw) > 0;
        if (result) {
            // 删除旧类型的缓存
            RedisUtils.deleteObject(CacheConstants.SYS_DICT_KEY + oldDictType);
        }
        return result;
    }

    @Override
    public void deleteDictDataByType(String dictType) {
        LambdaQueryWrapper<SysDictData> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysDictData::getDictType, dictType);
        baseMapper.delete(lqw);
        // 删除缓存
        RedisUtils.deleteObject(CacheConstants.SYS_DICT_KEY + dictType);
    }

    @Override
    public Boolean existsDictDataByType(String dictType) {
        LambdaQueryWrapper<SysDictData> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysDictData::getDictType, dictType);
        return baseMapper.exists(lqw);
    }
}
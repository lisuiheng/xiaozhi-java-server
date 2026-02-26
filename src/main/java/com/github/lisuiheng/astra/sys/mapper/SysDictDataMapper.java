package com.github.lisuiheng.astra.sys.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import com.github.lisuiheng.astra.sys.domain.SysDictData;
import com.github.lisuiheng.astra.sys.domain.vo.SysDictDataVo;

import java.util.List;

/**
 * 字典表 数据层
 *
 * @author Qoder
 */
public interface SysDictDataMapper extends BaseMapperPlus<SysDictData, SysDictDataVo> {

    /**
     * 根据字典类型查询字典数据列表
     *
     * @param dictType 字典类型
     * @return 符合条件的字典数据列表
     */
    default List<SysDictDataVo> selectDictDataByType(String dictType) {
        return selectVoList(
            new LambdaQueryWrapper<SysDictData>()
                .eq(SysDictData::getDictType, dictType)
                .orderByAsc(SysDictData::getDictSort));
    }
}
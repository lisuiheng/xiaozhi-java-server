package com.github.lisuiheng.astra.sys.service;


import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.sys.domain.bo.SysDictTypeBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysDictDataVo;
import com.github.lisuiheng.astra.sys.domain.vo.SysDictTypeVo;

import java.util.List;

/**
 * 字典 业务层
 *
 * @author Qoder
 */
public interface ISysDictTypeService {

    /**
     * 分页查询字典类型列表
     *
     * @param dictType  查询条件
     * @param pageQuery 分页参数
     * @return 字典类型分页列表
     */
    TableDataInfo<SysDictTypeVo> selectPageDictTypeList(SysDictTypeBo dictType, PageQuery pageQuery);

    /**
     * 根据条件分页查询字典类型
     *
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    List<SysDictTypeVo> selectDictTypeList(SysDictTypeBo dictType);

    /**
     * 根据所有字典类型
     *
     * @return 字典类型集合信息
     */
    List<SysDictTypeVo> selectDictTypeAll();

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    List<SysDictDataVo> selectDictDataByType(String dictType);

    /**
     * 根据字典类型ID查询信息
     *
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    SysDictTypeVo selectDictTypeById(Long dictId);

    /**
     * 根据字典类型查询信息
     *
     * @param dictType 字典类型
     * @return 字典类型
     */
    SysDictTypeVo selectDictTypeByType(String dictType);

    /**
     * 新增字典类型信息
     *
     * @param bo 字典类型信息
     * @return 结果
     */
    Boolean insertDictType(SysDictTypeBo bo);

    /**
     * 修改字典类型信息
     *
     * @param bo 字典类型信息
     * @return 结果
     */
    Boolean updateDictType(SysDictTypeBo bo);

    /**
     * 批量删除字典类型信息
     *
     * @param dictIds 需要删除的字典类型ID
     */
    void deleteDictTypeByIds(List<Long> dictIds);

    /**
     * 删除字典类型信息
     *
     * @param dictId 字典类型ID
     */
    void deleteDictTypeById(Long dictId);

    /**
     * 校验字典类型称是否唯一
     *
     * @param dictType 字典类型
     * @return 结果
     */
    Boolean checkDictTypeUnique(SysDictTypeBo dictType);

    /**
     * 重置字典缓存数据
     */
    void resetDictCache();
}
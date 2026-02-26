package com.github.lisuiheng.astra.sys.service;

import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.sys.domain.bo.SysDictDataBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysDictDataVo;

import java.util.List;

/**
 * 字典 业务层
 *
 * @author Qoder
 */
public interface ISysDictDataService {

    /**
     * 分页查询字典数据列表
     *
     * @param dictData  查询条件
     * @param pageQuery 分页参数
     * @return 字典数据分页列表
     */
    TableDataInfo<SysDictDataVo> selectPageDictDataList(SysDictDataBo dictData, PageQuery pageQuery);

    /**
     * 根据条件分页查询字典数据
     *
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    List<SysDictDataVo> selectDictDataList(SysDictDataBo dictData);

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    String selectDictLabel(String dictType, String dictValue);

    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    SysDictDataVo selectDictDataById(Long dictCode);

    /**
     * 根据字典类型查询字典数据信息
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    List<SysDictDataVo> selectDictDataByType(String dictType);

    /**
     * 新增字典数据信息
     *
     * @param bo 字典数据信息
     * @return 结果
     */
    Boolean insertDictData(SysDictDataBo bo);

    /**
     * 修改字典数据信息
     *
     * @param bo 字典数据信息
     * @return 结果
     */
    Boolean updateDictData(SysDictDataBo bo);

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     * @return 结果
     */
    Boolean deleteDictDataByIds(Long[] dictCodes);

    /**
     * 删除字典数据信息
     *
     * @param dictCode 字典数据ID
     * @return 结果
     */
    Boolean deleteDictDataById(Long dictCode);

    /**
     * 修改字典数据类型
     *
     * @param oldDictType 旧字典类型
     * @param newDictType 新字典类型
     * @return 结果
     */
    Boolean updateDictDataType(String oldDictType, String newDictType);

    /**
     * 删除字典数据信息
     *
     * @param dictType 字典类型
     * @return 结果
     */
    void deleteDictDataByType(String dictType);

    /**
     * 检查字典类型下是否存在字典数据
     *
     * @param dictType 字典类型
     * @return 结果
     */
    Boolean existsDictDataByType(String dictType);
}
package com.github.lisuiheng.astra.common.core.service;

import com.github.lisuiheng.astra.common.core.domain.dto.DictDataDTO;

import java.util.List;
import java.util.Map;

/**
 * 通用 字典服务
 *
 * @author Lion Li
 */
public interface DictService {

    /**
     * 分隔符
     */
    String SEPARATOR = ",";

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @return 字典标签
     */
    default String getDictLabel(String dictType, String dictValue) {
        return getDictLabel(dictType, dictValue, SEPARATOR);
    }

    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType  字典类型
     * @param dictLabel 字典标签
     * @return 字典值
     */
    default String getDictValue(String dictType, String dictLabel) {
        return getDictValue(dictType, dictLabel, SEPARATOR);
    }

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @param separator 分隔符
     * @return 字典标签
     */
    String getDictLabel(String dictType, String dictValue, String separator);

    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType  字典类型
     * @param dictLabel 字典标签
     * @param separator 分隔符
     * @return 字典值
     */
    String getDictValue(String dictType, String dictLabel, String separator);

    /**
     * 根据字典类型获取字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合
     */
    List<DictDataDTO> getDictData(String dictType);

    /**
     * 根据字典类型获取字典数据
     *
     * @param dictTypes 字典类型数组
     * @return 字典数据集合
     */
    Map<String, List<DictDataDTO>> getDictData(String... dictTypes);
}
package com.github.lisuiheng.astra.sys.domain.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.lisuiheng.astra.sys.domain.SysDictData;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典数据业务对象 sys_dict_data
 *
 * @author Qoder
 */
@Data
@AutoMapper(target = com.github.lisuiheng.astra.sys.domain.SysDictData.class, reverseConvertGenerate = false)
public class SysDictDataBo {

    /**
     * 字典编码
     */
    private Long dictCode;

    /**
     * 字典排序
     */
    private Integer dictSort;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 字典键值
     */
    private String dictValue;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 样式属性（其他样式扩展）
     */
    private String cssClass;

    /**
     * 表格字典样式
     */
    private String listClass;

    /**
     * 是否默认（Y是 N否）
     */
    private String isDefault;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonIgnore
    private LocalDateTime createTime;

    /**
     * 状态（0正常 1停用）
     */
    @JsonIgnore
    private String status;

    public SysDictData toEntity() {
        SysDictData entity = new SysDictData();
        entity.setDictCode(dictCode);
        entity.setDictSort(dictSort);
        entity.setDictLabel(dictLabel);
        entity.setDictValue(dictValue);
        entity.setDictType(dictType);
        entity.setCssClass(cssClass);
        entity.setListClass(listClass);
        entity.setIsDefault(isDefault);
        entity.setRemark(remark);
        // 不设置 createTime，因为它是在创建时自动填充的
        return entity;
    }
}
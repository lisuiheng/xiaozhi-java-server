package com.github.lisuiheng.astra.sys.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.lisuiheng.astra.common.tenant.core.TenantEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.lisuiheng.astra.sys.domain.vo.SysDictDataVo;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 字典数据表 sys_dict_data
 *
 * @author Qoder
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_data")
@AutoMapper(target = SysDictDataVo.class)
public class SysDictData extends TenantEntity {

    /**
     * 字典编码
     */
    @TableId(value = "dict_code")
    @JsonSerialize(using = ToStringSerializer.class)
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

}
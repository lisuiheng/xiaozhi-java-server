package com.github.lisuiheng.astra.sys.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.lisuiheng.astra.common.tenant.core.TenantEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.lisuiheng.astra.sys.domain.vo.SysDictTypeVo;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 字典类型表 sys_dict_type
 *
 * @author Qoder
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_type")
@AutoMapper(target = SysDictTypeVo.class)
public class SysDictType extends TenantEntity {

    /**
     * 字典主键
     */
    @TableId(value = "dict_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dictId;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 备注
     */
    private String remark;
}
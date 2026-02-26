package com.github.lisuiheng.astra.sys.domain.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.lisuiheng.astra.sys.domain.SysDictType;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 字典类型业务对象 sys_dict_type
 *
 * @author Qoder
 */
@Data
@AutoMapper(target = com.github.lisuiheng.astra.sys.domain.SysDictType.class, reverseConvertGenerate = false)
public class SysDictTypeBo {

    /**
     * 字典主键
     */
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

    /**
     * 创建时间
     */
    @JsonIgnore
    private LocalDateTime createTime;

    public SysDictType toEntity() {
        SysDictType entity = new SysDictType();
        entity.setDictId(dictId);
        entity.setDictName(dictName);
        entity.setDictType(dictType);
        entity.setRemark(remark);
        // 不设置 createTime，因为它是在创建时自动填充的
        return entity;
    }
}
package com.github.lisuiheng.astra.sys.domain.vo;

import cn.idev.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serial;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.io.Serializable;
import java.util.Date;

/**
 * 字典类型视图对象 sys_dict_type
 *
 * @author Qoder
 */
@Data
public class SysDictTypeVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典主键
     */
    @ExcelProperty(value = "字典主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dictId;

    /**
     * 字典名称
     */
    @ExcelProperty(value = "字典名称")
    private String dictName;

    /**
     * 字典类型
     */
    @ExcelProperty(value = "字典类型")
    private String dictType;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
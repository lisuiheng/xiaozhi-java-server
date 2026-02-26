package com.github.lisuiheng.astra.sys.domain.vo;

import com.github.lisuiheng.astra.sys.domain.entity.SysOss;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * OSS对象存储视图对象 sys_oss
 *
 * @author Lion Li
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysOssVo extends SysOss {

    private static final long serialVersionUID = 1L;

    /**
     * 上传人名称
     */
    private String createByName;
}
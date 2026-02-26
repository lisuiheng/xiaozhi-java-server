package com.github.lisuiheng.astra.sys.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * OSS对象存储对象
 *
 * @author Lion Li
 */
@Data
@NoArgsConstructor
@TableName("sys_oss")
public class SysOss {

    /**
     * 对象存储主键
     */
    @TableId
    private Long ossId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原名
     */
    private String originalName;

    /**
     * 文件后缀名
     */
    private String fileSuffix;

    /**
     * URL地址
     */
    private String url;

    /**
     * 扩展字段
     */
    private String ext1;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 上传人
     */
    private Long createBy;

    /**
     * 服务商
     */
    private String service;
}
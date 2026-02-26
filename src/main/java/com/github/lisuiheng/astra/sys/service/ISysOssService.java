package com.github.lisuiheng.astra.sys.service;

import com.github.lisuiheng.astra.sys.domain.vo.SysOssVo;
import org.springframework.web.multipart.MultipartFile;

/**
 * OSS对象存储服务接口
 *
 * @author Lion Li
 */
public interface ISysOssService {

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 结果
     */
    SysOssVo upload(MultipartFile file);

    /**
     * 通过ID查询OSS对象
     *
     * @param ossId OSS对象ID
     * @return 结果
     */
    SysOssVo getById(Long ossId);
}
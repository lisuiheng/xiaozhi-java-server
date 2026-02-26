package com.github.lisuiheng.astra.sys.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.lisuiheng.astra.common.satoken.utils.LoginHelper;
import com.github.lisuiheng.astra.sys.domain.entity.SysOss;
import com.github.lisuiheng.astra.sys.domain.vo.SysOssVo;
import com.github.lisuiheng.astra.sys.mapper.SysOssMapper;
import com.github.lisuiheng.astra.sys.service.ISysOssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

/**
 * OSS对象存储服务实现
 *
 * @author Lion Li
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysOssServiceImpl implements ISysOssService {

    private final SysOssMapper sysOssMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysOssVo upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String suffix = FileUtil.extName(originalFilename);
        String newFileName = DateUtil.format(new Date(), "yyyyMMdd") + "/" + IdUtil.fastSimpleUUID() + "." + suffix;

        // 创建OSS对象
        SysOss oss = new SysOss();
        oss.setFileName(newFileName);
        oss.setOriginalName(originalFilename);
        oss.setFileSuffix(suffix);
        oss.setUrl("/profile/avatar/" + newFileName); // 模拟存储路径
        oss.setCreateTime(new Date());
        oss.setCreateBy(LoginHelper.getUserId());

        // 保存到数据库
        sysOssMapper.insert(oss);

        // 转换为VO返回
        SysOssVo vo = new SysOssVo();
        BeanUtils.copyProperties(oss, vo);
        return vo;
    }

    @Override
    public SysOssVo getById(Long ossId) {
        SysOss oss = sysOssMapper.selectOne(new LambdaQueryWrapper<SysOss>().eq(SysOss::getOssId, ossId));
        if (oss != null) {
            SysOssVo vo = new SysOssVo();
            BeanUtils.copyProperties(oss, vo);
            return vo;
        }
        return null;
    }
}
package com.github.lisuiheng.astra.sys.service;

import com.github.lisuiheng.astra.sys.domain.bo.SysSocialBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysSocialVo;
import java.util.List;

/**
 * 社交服务接口
 */
public interface ISysSocialService {

    /**
     * 根据ID查询社交信息
     */
    SysSocialVo queryById(String id);

    /**
     * 查询社交关系列表
     */
    List<SysSocialVo> queryList(SysSocialBo bo);

    /**
     * 根据用户ID查询社交关系列表
     *
     * @param userId 用户ID
     * @return 社交关系列表
     */
    List<SysSocialVo> queryListByUserId(Long userId);

    /**
     * 新增授权关系
     */
    Boolean insertByBo(SysSocialBo bo);

    /**
     * 更新社交关系
     */
    Boolean updateByBo(SysSocialBo bo);

    /**
     * 根据ID删除社交信息
     *
     * @param socialId 社交ID
     * @return 删除结果
     */
    Boolean deleteWithValidById(Long socialId);

    /**
     * 根据 authId 查询
     */
    List<SysSocialVo> selectByAuthId(String authId);
}
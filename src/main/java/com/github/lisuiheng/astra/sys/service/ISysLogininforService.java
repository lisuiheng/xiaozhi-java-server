package com.github.lisuiheng.astra.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.sys.domain.entity.SysLogininfor;
import com.github.lisuiheng.astra.sys.domain.bo.SysLogininforBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysLogininforVo;
import java.util.List;

/**
 * 系统访问记录 服务层
 *
 * @author Lion Li
 */
public interface ISysLogininforService extends IService<SysLogininfor> {

    /**
     * 分页查询登录日志列表
     *
     * @param logininforBo 查询条件
     * @param pageQuery  分页参数
     * @return 登录日志分页列表
     */
    TableDataInfo<SysLogininforVo> selectPageLogininforList(SysLogininforBo logininforBo, PageQuery pageQuery);

    /**
     * 查询系统登录日志集合
     *
     * @param logininforBo 访问记录
     * @return 登录记录集合
     */
    List<SysLogininforVo> selectLogininforList(SysLogininforBo logininforBo);

    /**
     * 新增系统登录日志
     *
     * @param bo 访问记录信息
     */
    void insertLogininfor(SysLogininforBo bo);

    /**
     * 查询系统登录日志集合
     *
     * @return 登录记录集合
     */
    List<SysLogininfor> selectLogininforList();

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    int deleteLogininforByIds(Long[] infoIds);

    /**
     * 清空系统登录日志
     */
    void cleanLogininfor();
}
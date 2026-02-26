package com.github.lisuiheng.astra.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.utils.MapstructUtils;
import com.github.lisuiheng.astra.common.utils.StringUtils;
import com.github.lisuiheng.astra.sys.domain.entity.SysOperLog;
import com.github.lisuiheng.astra.sys.domain.bo.SysOperLogBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysOperLogVo;
import com.github.lisuiheng.astra.sys.mapper.SysOperLogMapper;
import com.github.lisuiheng.astra.sys.event.OperLogEvent;
import com.github.lisuiheng.astra.sys.service.ISysOperLogService;
import com.github.lisuiheng.astra.common.utils.AddressUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 操作日志 服务层处理
 *
 * @author xiaozhi
 */
@RequiredArgsConstructor
@Service
public class SysOperLogServiceImpl implements ISysOperLogService {

    private final SysOperLogMapper baseMapper;

    /**
     * 操作日志记录
     *
     * @param operLog 操作日志事件
     */
    @Async
    @EventListener(OperLogEvent.class)
    public void recordOperLog(OperLogEvent operLog) {
        SysOperLog sysOperLog = MapstructUtils.convert(operLog, SysOperLog.class);
        // 远程查询操作地点
        sysOperLog.setOperLocation(AddressUtils.getRealAddressByIP(operLog.getOperIp()));
        sysOperLog.setOperTime(new Date());
        baseMapper.insert(sysOperLog);
    }

    /**
     * 分页查询操作日志列表
     *
     * @param operLog   查询条件
     * @param pageQuery 分页参数
     * @return 操作日志分页列表
     */
    @Override
    public TableDataInfo<SysOperLogVo> selectPageOperLogList(SysOperLogBo operLog, PageQuery pageQuery) {
        Map<String, Object> params = operLog.getParams();
        LambdaQueryWrapper<SysOperLog> lqw = new LambdaQueryWrapper<SysOperLog>()
            .like(StringUtils.isNotBlank(operLog.getOperIp()), SysOperLog::getOperIp, operLog.getOperIp())
            .like(StringUtils.isNotBlank(operLog.getTitle()), SysOperLog::getTitle, operLog.getTitle())
            .like(StringUtils.isNotBlank(operLog.getOperName()), SysOperLog::getOperName, operLog.getOperName())
            .eq(operLog.getBusinessType() != null, SysOperLog::getBusinessType, operLog.getBusinessType())
            .eq(operLog.getStatus() != null, SysOperLog::getStatus, operLog.getStatus())
            .between(params.get("beginTime") != null && params.get("endTime") != null,
                SysOperLog::getOperTime, params.get("beginTime"), params.get("endTime"));
        
        if (StringUtils.isBlank(pageQuery.getOrderByColumn())) {
            lqw.orderByDesc(SysOperLog::getOperId);
        }
        Page<SysOperLogVo> page = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    /**
     * 新增操作日志
     *
     * @param bo 操作日志对象
     */
    @Override
    public void insertOperlog(SysOperLogBo bo) {
        SysOperLog sysOperLog = MapstructUtils.convert(bo, SysOperLog.class);
        sysOperLog.setOperTime(new Date());
        baseMapper.insert(sysOperLog);
    }

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    @Override
    public List<SysOperLogVo> selectOperLogList(SysOperLogBo operLog) {
        Map<String, Object> params = operLog.getParams();
        return baseMapper.selectVoList(new LambdaQueryWrapper<SysOperLog>()
            .like(StringUtils.isNotBlank(operLog.getOperIp()), SysOperLog::getOperIp, operLog.getOperIp())
            .like(StringUtils.isNotBlank(operLog.getTitle()), SysOperLog::getTitle, operLog.getTitle())
            .like(StringUtils.isNotBlank(operLog.getOperName()), SysOperLog::getOperName, operLog.getOperName())
            .eq(operLog.getBusinessType() != null, SysOperLog::getBusinessType, operLog.getBusinessType())
            .eq(operLog.getStatus() != null, SysOperLog::getStatus, operLog.getStatus())
            .between(params.get("beginTime") != null && params.get("endTime") != null,
                SysOperLog::getOperTime, params.get("beginTime"), params.get("endTime"))
            .orderByDesc(SysOperLog::getOperId));
    }

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    @Override
    public int deleteOperLogByIds(Long[] operIds) {
        return baseMapper.deleteByIds(Arrays.asList(operIds));
    }

    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    @Override
    public SysOperLogVo selectOperLogById(Long operId) {
        return baseMapper.selectVoById(operId);
    }

    /**
     * 清空操作日志
     */
    @Override
    public void cleanOperLog() {
        baseMapper.delete(new LambdaQueryWrapper<>());
    }
}
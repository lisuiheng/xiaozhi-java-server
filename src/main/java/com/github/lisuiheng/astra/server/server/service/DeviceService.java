package com.github.lisuiheng.astra.server.server.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.lisuiheng.astra.server.server.mapper.DeviceInfoMapper;
import com.github.lisuiheng.astra.server.server.model.entity.DeviceInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService extends ServiceImpl<DeviceInfoMapper, DeviceInfo> {
    
    private final DeviceInfoMapper deviceInfoMapper;
    
    /**
     * 根据序列号查询设备
     */
    public DeviceInfo getDeviceBySerialNumber(String serialNumber) {
        return deviceInfoMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<DeviceInfo>()
                .eq(DeviceInfo::getSerialNumber, serialNumber)
        );
    }
    
    /**
     * 更新设备的智能体ID
     */
    public boolean updateDeviceAgentId(String deviceId, String agentId) {
        DeviceInfo device = getById(deviceId);
        if (device == null) {
            return false;
        }
        
        device.setAgentId(agentId);
        return updateById(device);
    }
}
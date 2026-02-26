package com.github.lisuiheng.astra.server.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.lisuiheng.astra.server.server.constant.DeviceState;
import com.github.lisuiheng.astra.server.server.mapper.DeviceInfoMapper;
import com.github.lisuiheng.astra.server.server.model.entity.DeviceInfo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class DeviceInfoService extends ServiceImpl<DeviceInfoMapper, DeviceInfo> {



    public boolean saveOrUpdateDevice(DeviceInfo deviceInfo) {
        // 根据序列号检查设备是否存在
        DeviceInfo existingDevice = this.queryDeviceBySerialNumber(deviceInfo.getSerialNumber());

        if (existingDevice != null) {
            // 如果设备已存在，更新设备信息
            deviceInfo.setId(existingDevice.getId()); // 保留原有ID
            return this.updateById(deviceInfo);
        } else {
            // 如果设备不存在，新增设备
            return this.save(deviceInfo);
        }
    }

    /**
     * 根据序列号查询设备
     */
    public DeviceInfo queryDeviceBySerialNumber(String serialNumber) {
        LambdaQueryWrapper<DeviceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceInfo::getSerialNumber, serialNumber);
        return this.getOne(wrapper);
    }

    /**
     * 根据UUID查询设备
     */
    public DeviceInfo queryDeviceByUuid(String uuid) {
        LambdaQueryWrapper<DeviceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceInfo::getUuid, uuid);
        return this.getOne(wrapper);
    }

    /**
     * 保存或更新设备
     */
    public boolean saveOrUpdate(DeviceInfo deviceInfo) {
        if (deviceInfo.getId() == null) {
            // 新设备，检查是否已存在
            DeviceInfo existing = queryDeviceBySerialNumber(deviceInfo.getSerialNumber());
            if (existing != null) {
                // 已存在，更新
                deviceInfo.setId(existing.getId());
                return this.updateById(deviceInfo);
            }
        }
        return this.saveOrUpdate(deviceInfo);
    }

    /**
     * 设置最后连接时间
     */
    public void updateLastSeenTime(String serialNumber) {
        DeviceInfo deviceInfo = this.queryDeviceBySerialNumber(serialNumber);
        if (deviceInfo != null) {
            deviceInfo.setLastSeenTime(LocalDateTime.now());
            this.updateById(deviceInfo);
        }
    }

    /**
     * 设置设备激活状态
     */
    public boolean activateDevice(String serialNumber, String uuid) {
        DeviceInfo deviceInfo = this.queryDeviceBySerialNumber(serialNumber);
        if (deviceInfo == null) {
            deviceInfo = new DeviceInfo();
            deviceInfo.setId(java.util.UUID.randomUUID().toString());
            deviceInfo.setSerialNumber(serialNumber);
            deviceInfo.setUuid(uuid);
            deviceInfo.setDeviceKind("esp32");
            deviceInfo.setName("Device-" + serialNumber.substring(Math.max(0, serialNumber.length() - 6)));
        }

        deviceInfo.setDeviceState(DeviceState.ACTIVE);
        deviceInfo.setActivatedTime(LocalDateTime.now());
        deviceInfo.setLastSeenTime(LocalDateTime.now());

        return this.saveOrUpdate(deviceInfo);
    }
}
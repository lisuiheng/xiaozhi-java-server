package com.github.lisuiheng.astra.server.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.domain.R;
import com.github.lisuiheng.astra.server.server.constant.DeviceState;
import com.github.lisuiheng.astra.server.server.model.entity.DeviceInfo;
import com.github.lisuiheng.astra.server.server.service.DeviceInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceInfoController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private final DeviceInfoService deviceInfoService;

    /**
     * 创建设备
     */
    @PostMapping
    public R<DeviceInfo> createDevice(@RequestBody Map<String, Object> deviceData) {
        try {
            DeviceInfo deviceInfo = new DeviceInfo();
            // 从deviceData中提取字段并设置到DeviceInfo对象
            if (deviceData.containsKey("name")) {
                deviceInfo.setName((String) deviceData.get("name"));
            }
            if (deviceData.containsKey("serialNumber")) {
                deviceInfo.setSerialNumber((String) deviceData.get("serialNumber"));
            }
            if (deviceData.containsKey("verifyCode")) {
                deviceInfo.setVerifyCode((String) deviceData.get("verifyCode"));
            }
            if (deviceData.containsKey("deviceKind")) {
                deviceInfo.setDeviceKind((String) deviceData.get("deviceKind"));
            }
            if (deviceData.containsKey("deviceState")) {
                deviceInfo.setDeviceState(com.github.lisuiheng.astra.server.server.constant.DeviceState.valueOf((String) deviceData.get("deviceState")));
            }
            if (deviceData.containsKey("programKind")) {
                deviceInfo.setProgramKind((String) deviceData.get("programKind"));
            }
            if (deviceData.containsKey("programVer")) {
                deviceInfo.setProgramVer((String) deviceData.get("programVer"));
            }
            if (deviceData.containsKey("volume")) {
                deviceInfo.setVolume((Integer) deviceData.get("volume"));
            }
            if (deviceData.containsKey("brightness")) {
                deviceInfo.setBrightness((Integer) deviceData.get("brightness"));
            }
            if (deviceData.containsKey("isUpdatable")) {
                deviceInfo.setIsUpdatable((String) deviceData.get("isUpdatable"));
            }
            if (deviceData.containsKey("versionType")) {
                deviceInfo.setVersionType((String) deviceData.get("versionType"));
            }
            if (deviceData.containsKey("otaUpdateUrl")) {
                deviceInfo.setOtaUpdateUrl((String) deviceData.get("otaUpdateUrl"));
            }
            if (deviceData.containsKey("aesKey")) {
                deviceInfo.setAesKey((String) deviceData.get("aesKey"));
            }
            if (deviceData.containsKey("aesNonce")) {
                deviceInfo.setAesNonce((String) deviceData.get("aesNonce"));
            }
            if (deviceData.containsKey("remark")) {
                deviceInfo.setRemark((String) deviceData.get("remark"));
            }
            if (deviceData.containsKey("agentId")) {
                deviceInfo.setAgentId((String) deviceData.get("agentId"));
            }
            
            boolean result = deviceInfoService.save(deviceInfo);
            if (result) {
                return R.ok("创建成功", deviceInfo);
            } else {
                return R.fail("创建失败");
            }
        } catch (Exception e) {
            log.error("创建设备失败: ", e);
            return R.fail("创建设备失败: " + e.getMessage());
        }
    }

    /**
     * 更新设备
     */
    @PutMapping("/{deviceId}")
    public R<DeviceInfo> updateDevice(@PathVariable String deviceId, @RequestBody Map<String, Object> deviceData) {
        try {
            DeviceInfo deviceInfo = deviceInfoService.getById(deviceId);
            if (deviceInfo == null) {
                return R.fail("设备不存在");
            }
            
            // 从deviceData中提取字段并更新DeviceInfo对象
            if (deviceData.containsKey("name")) {
                deviceInfo.setName((String) deviceData.get("name"));
            }
            if (deviceData.containsKey("serialNumber")) {
                deviceInfo.setSerialNumber((String) deviceData.get("serialNumber"));
            }
            if (deviceData.containsKey("verifyCode")) {
                deviceInfo.setVerifyCode((String) deviceData.get("verifyCode"));
            }
            if (deviceData.containsKey("deviceKind")) {
                deviceInfo.setDeviceKind((String) deviceData.get("deviceKind"));
            }
            if (deviceData.containsKey("deviceState")) {
                deviceInfo.setDeviceState(com.github.lisuiheng.astra.server.server.constant.DeviceState.valueOf((String) deviceData.get("deviceState")));
            }
            if (deviceData.containsKey("programKind")) {
                deviceInfo.setProgramKind((String) deviceData.get("programKind"));
            }
            if (deviceData.containsKey("programVer")) {
                deviceInfo.setProgramVer((String) deviceData.get("programVer"));
            }
            if (deviceData.containsKey("volume")) {
                deviceInfo.setVolume((Integer) deviceData.get("volume"));
            }
            if (deviceData.containsKey("brightness")) {
                deviceInfo.setBrightness((Integer) deviceData.get("brightness"));
            }
            if (deviceData.containsKey("isUpdatable")) {
                deviceInfo.setIsUpdatable((String) deviceData.get("isUpdatable"));
            }
            if (deviceData.containsKey("versionType")) {
                deviceInfo.setVersionType((String) deviceData.get("versionType"));
            }
            if (deviceData.containsKey("otaUpdateUrl")) {
                deviceInfo.setOtaUpdateUrl((String) deviceData.get("otaUpdateUrl"));
            }
            if (deviceData.containsKey("aesKey")) {
                deviceInfo.setAesKey((String) deviceData.get("aesKey"));
            }
            if (deviceData.containsKey("aesNonce")) {
                deviceInfo.setAesNonce((String) deviceData.get("aesNonce"));
            }
            if (deviceData.containsKey("remark")) {
                deviceInfo.setRemark((String) deviceData.get("remark"));
            }
            if (deviceData.containsKey("agentId")) {
                deviceInfo.setAgentId((String) deviceData.get("agentId"));
            }
            
            boolean result = deviceInfoService.updateById(deviceInfo);
            if (result) {
                return R.ok("更新成功", deviceInfo);
            } else {
                return R.fail("更新失败");
            }
        } catch (Exception e) {
            log.error("更新设备失败: ", e);
            return R.fail("更新设备失败: " + e.getMessage());
        }
    }

    /**
     * 删除设备
     */
    @DeleteMapping("/{deviceId}")
    public R<Boolean> deleteDevice(@PathVariable String deviceId) {
        try {
            boolean result = deviceInfoService.removeById(deviceId);
            return result ? R.ok("删除成功", true) : R.fail("删除失败", false);
        } catch (Exception e) {
            log.error("删除设备失败: ", e);
            return R.fail("删除设备失败: " + e.getMessage(), false);
        }
    }

    /**
     * 批量删除设备
     */
    @DeleteMapping("/batch")
    public R<Boolean> batchDeleteDevices(@RequestBody List<String> deviceIds) {
        try {
            boolean result = deviceInfoService.removeBatchByIds(deviceIds);
            return result ? R.ok("批量删除成功", true) : R.fail("批量删除失败", false);
        } catch (Exception e) {
            log.error("批量删除设备失败: ", e);
            return R.fail("批量删除设备失败: " + e.getMessage(), false);
        }
    }

    /**
     * 获取设备详情
     */
    @GetMapping("/{deviceId}")
    public R<DeviceInfo> getDeviceDetail(@PathVariable String deviceId) {
        try {
            DeviceInfo deviceInfo = deviceInfoService.getById(deviceId);
            if (deviceInfo != null) {
                return R.ok("查询成功", deviceInfo);
            } else {
                return R.fail("设备不存在");
            }
        } catch (Exception e) {
            log.error("获取设备详情失败: ", e);
            return R.fail("获取设备详情失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询设备
     */
    @GetMapping
    public TableDataInfo<DeviceInfo> getDevicePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String deviceKind,
            @RequestParam(required = false) String deviceState,
            @RequestParam(required = false) String serialNumber) {
        
        LambdaQueryWrapper<DeviceInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, DeviceInfo::getName, name)
                .like(deviceKind != null, DeviceInfo::getDeviceKind, deviceKind)
                .eq(deviceState != null, DeviceInfo::getDeviceState, deviceState)
                .like(serialNumber != null, DeviceInfo::getSerialNumber, serialNumber)
                .orderByDesc(DeviceInfo::getCreateTime);

        IPage<DeviceInfo> page = deviceInfoService.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize), queryWrapper);
        return TableDataInfo.build(page);
    }

    /**
     * 搜索设备
     */
    @GetMapping("/search")
    public R<List<DeviceInfo>> searchDevices(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String deviceKind) {
        try {
            LambdaQueryWrapper<DeviceInfo> queryWrapper = new LambdaQueryWrapper<>();
            if (keyword != null) {
                queryWrapper.and(wrapper -> wrapper
                        .like(DeviceInfo::getName, keyword)
                        .or()
                        .like(DeviceInfo::getSerialNumber, keyword)
                        .or()
                        .like(DeviceInfo::getDeviceKind, keyword));
            }
            if (deviceKind != null) {
                queryWrapper.eq(DeviceInfo::getDeviceKind, deviceKind);
            }

            List<DeviceInfo> deviceInfos = deviceInfoService.list(queryWrapper);
            return R.ok("搜索成功", deviceInfos);
        } catch (Exception e) {
            log.error("搜索设备失败: ", e);
            return R.fail("搜索设备失败: " + e.getMessage());
        }
    }

    /**
     * 启用/禁用设备
     */
    @PutMapping("/{deviceId}/state")
    public R<Boolean> toggleDeviceState(@PathVariable String deviceId, @RequestParam String deviceState) {
        try {
            DeviceInfo deviceInfo = deviceInfoService.getById(deviceId);
            if (deviceInfo == null) {
                return R.fail("设备不存在");
            }
            
            deviceInfo.setDeviceState(com.github.lisuiheng.astra.server.server.constant.DeviceState.valueOf(deviceState));
            boolean result = deviceInfoService.updateById(deviceInfo);
            return result ? R.ok("状态更新成功", true) : R.fail("状态更新失败", false);
        } catch (Exception e) {
            log.error("更新设备状态失败: ", e);
            return R.fail("更新设备状态失败: " + e.getMessage(), false);
        }
    }

    /**
     * 批量更新设备状态
     */
    @PutMapping("/batch/state")
    public R<Boolean> batchUpdateDeviceState(@RequestBody List<String> deviceIds, @RequestParam String deviceState) {
        try {
            List<DeviceInfo> deviceInfos = deviceInfoService.listByIds(deviceIds);
            for (DeviceInfo deviceInfo : deviceInfos) {
                deviceInfo.setDeviceState(com.github.lisuiheng.astra.server.server.constant.DeviceState.valueOf(deviceState));
            }
            boolean result = deviceInfoService.updateBatchById(deviceInfos);
            return result ? R.ok("批量状态更新成功", true) : R.fail("批量状态更新失败", false);
        } catch (Exception e) {
            log.error("批量更新设备状态失败: ", e);
            return R.fail("批量更新设备状态失败: " + e.getMessage(), false);
        }
    }

    /**
     * 绑定智能体到设备
     */
    @PostMapping("/{deviceId}/bind-agent")
    public R<Object> bindAgentToDevice(@PathVariable String deviceId, @RequestBody Map<String, Object> data) {
        try {
            DeviceInfo deviceInfo = deviceInfoService.getById(deviceId);
            if (deviceInfo == null) {
                return R.fail("设备不存在");
            }
            
            Object agentId = data.get("agentId");
            if (agentId != null) {
                deviceInfo.setAgentId(agentId.toString());
                boolean result = deviceInfoService.updateById(deviceInfo);
                if (result) {
                    return R.ok("绑定成功", deviceInfo);
                } else {
                    return R.fail("绑定失败");
                }
            } else {
                return R.fail("缺少agentId参数");
            }
        } catch (Exception e) {
            log.error("绑定智能体到设备失败: ", e);
            return R.fail("绑定智能体到设备失败: " + e.getMessage());
        }
    }

    /**
     * 解绑智能体
     */
    @DeleteMapping("/unbind-agent")
    public R<Boolean> unbindAgentFromDevice(@RequestParam String agentId) {
        try {
            LambdaQueryWrapper<DeviceInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DeviceInfo::getAgentId, agentId);
            List<DeviceInfo> deviceInfos = deviceInfoService.list(queryWrapper);
            
            for (DeviceInfo deviceInfo : deviceInfos) {
                deviceInfo.setAgentId(null);
            }
            
            boolean result = deviceInfoService.updateBatchById(deviceInfos);
            return result ? R.ok("解绑成功", true) : R.fail("解绑失败", false);
        } catch (Exception e) {
            log.error("解绑智能体失败: ", e);
            return R.fail("解绑智能体失败: " + e.getMessage(), false);
        }
    }

    /**
     * 获取设备当前绑定的智能体
     */
    @GetMapping("/{deviceId}/binding")
    public R<Object> getDeviceCurrentBinding(@PathVariable String deviceId) {
        try {
            DeviceInfo deviceInfo = deviceInfoService.getById(deviceId);
            if (deviceInfo != null && deviceInfo.getAgentId() != null) {
                return R.ok("查询成功", deviceInfo.getAgentId());
            } else {
                return R.ok("设备未绑定智能体", null);
            }
        } catch (Exception e) {
            log.error("获取设备当前绑定的智能体失败: ", e);
            return R.fail("获取设备当前绑定的智能体失败: " + e.getMessage());
        }
    }

    /**
     * 获取设备绑定的智能体列表
     */
    @GetMapping("/{deviceId}/bindings")
    public R<List<Object>> getDeviceAgentBindings(@PathVariable String deviceId) {
        try {
            // 这里应该查询设备绑定的所有智能体信息
            // 为了示例，我们返回一个包含绑定信息的列表
            DeviceInfo deviceInfo = deviceInfoService.getById(deviceId);
            if (deviceInfo != null && deviceInfo.getAgentId() != null) {
                List<Object> bindings = java.util.List.of(deviceInfo.getAgentId());
                return R.ok("查询成功", bindings);
            } else {
                return R.ok("设备未绑定智能体", java.util.List.of());
            }
        } catch (Exception e) {
            log.error("获取设备绑定的智能体列表失败: ", e);
            return R.fail("获取设备绑定的智能体列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取设备使用统计
     */
    @GetMapping("/{deviceId}/stats")
    public R<Object> getDeviceStats(@PathVariable String deviceId) {
        try {
            // 这里应该实现设备使用统计逻辑
            // 为了示例，我们返回一个统计对象
            Map<String, Object> stats = java.util.Map.of(
                "deviceId", deviceId,
                "usageCount", 0L,
                "lastUsedTime", null
            );
            return R.ok("查询成功", stats);
        } catch (Exception e) {
            log.error("获取设备使用统计失败: ", e);
            return R.fail("获取设备使用统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取总体设备使用统计
     */
    @GetMapping("/stats/total")
    public R<Object> getTotalDeviceStats() {
        try {
            // 这里应该实现总体设备使用统计逻辑
            // 为了示例，我们返回一个统计对象
            Map<String, Object> stats = java.util.Map.of(
                "totalDevices", deviceInfoService.count(),
                "onlineDevices", deviceInfoService.count(new LambdaQueryWrapper<DeviceInfo>().eq(DeviceInfo::getDeviceState, com.github.lisuiheng.astra.server.server.constant.DeviceState.ONLINE)),
                "offlineDevices", deviceInfoService.count(new LambdaQueryWrapper<DeviceInfo>().eq(DeviceInfo::getDeviceState, com.github.lisuiheng.astra.server.server.constant.DeviceState.OFFLINE))
            );
            return R.ok("查询成功", stats);
        } catch (Exception e) {
            log.error("获取总体设备使用统计失败: ", e);
            return R.fail("获取总体设备使用统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取各类型设备统计
     */
    @GetMapping("/stats/type")
    public R<List<Object>> getDeviceTypeStats() {
        try {
            // 这里应该实现各类型设备统计逻辑
            // 为了示例，我们返回一个统计列表
            List<Object> stats = java.util.List.of();
            return R.ok("查询成功", stats);
        } catch (Exception e) {
            log.error("获取各类型设备统计失败: ", e);
            return R.fail("获取各类型设备统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门设备
     */
    @GetMapping("/popular")
    public R<List<Object>> getPopularDevices(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            // 这里应该实现获取热门设备逻辑
            // 为了示例，我们返回一个空列表
            List<Object> devices = java.util.List.of();
            return R.ok("查询成功", devices);
        } catch (Exception e) {
            log.error("获取热门设备失败: ", e);
            return R.fail("获取热门设备失败: " + e.getMessage());
        }
    }

    /**
     * 验证设备配置
     */
    @GetMapping("/{deviceId}/validate")
    public R<Boolean> validateDeviceConfig(@PathVariable String deviceId) {
        try {
            DeviceInfo deviceInfo = deviceInfoService.getById(deviceId);
            if (deviceInfo != null) {
                // 这里应该实现设备配置验证逻辑
                return R.ok("验证成功", true);
            } else {
                return R.fail("设备不存在");
            }
        } catch (Exception e) {
            log.error("验证设备配置失败: ", e);
            return R.fail("验证设备配置失败: " + e.getMessage(), false);
        }
    }

    /**
     * 克隆设备
     */
    @PostMapping("/{sourceDeviceId}/clone")
    public R<DeviceInfo> cloneDevice(@PathVariable String sourceDeviceId, @RequestBody Map<String, Object> data) {
        try {
            DeviceInfo sourceDevice = deviceInfoService.getById(sourceDeviceId);
            if (sourceDevice == null) {
                return R.fail("源设备不存在");
            }
            
            // 克隆设备信息，但需要清除ID等唯一标识
            DeviceInfo clonedDevice = new DeviceInfo();
            // 复制属性
            clonedDevice.setName(sourceDevice.getName() + " (克隆)");
            clonedDevice.setSerialNumber(sourceDevice.getSerialNumber() + "-cloned");
            clonedDevice.setVerifyCode(sourceDevice.getVerifyCode());
            clonedDevice.setDeviceKind(sourceDevice.getDeviceKind());
            clonedDevice.setDeviceState(sourceDevice.getDeviceState());
            clonedDevice.setProgramKind(sourceDevice.getProgramKind());
            clonedDevice.setProgramVer(sourceDevice.getProgramVer());
            clonedDevice.setVolume(sourceDevice.getVolume());
            clonedDevice.setBrightness(sourceDevice.getBrightness());
            clonedDevice.setIsUpdatable(sourceDevice.getIsUpdatable());
            clonedDevice.setVersionType(sourceDevice.getVersionType());
            clonedDevice.setProgramUpdateTime(sourceDevice.getProgramUpdateTime());
            clonedDevice.setDetailInfo(sourceDevice.getDetailInfo());
            clonedDevice.setOtaUpdateUrl(sourceDevice.getOtaUpdateUrl());
            clonedDevice.setAesKey(sourceDevice.getAesKey());
            clonedDevice.setAesNonce(sourceDevice.getAesNonce());
            clonedDevice.setRemark(sourceDevice.getRemark());
            clonedDevice.setAgentId(sourceDevice.getAgentId());

            // 如果data中有覆盖字段，则使用data中的值
            if (data.containsKey("name")) {
                clonedDevice.setName((String) data.get("name"));
            }
            if (data.containsKey("serialNumber")) {
                clonedDevice.setSerialNumber((String) data.get("serialNumber"));
            }
            if (data.containsKey("remark")) {
                clonedDevice.setRemark((String) data.get("remark"));
            }

            boolean result = deviceInfoService.save(clonedDevice);
            if (result) {
                return R.ok("克隆成功", clonedDevice);
            } else {
                return R.fail("克隆设备失败");
            }
        } catch (Exception e) {
            log.error("克隆设备失败: ", e);
            return R.fail("克隆设备失败: " + e.getMessage());
        }
    }

    /**
     * 通过验证码添加设备（从OTA缓存中获取设备信息）
     * @param verifyCode 激活验证码（6位数字）
     */
    @PostMapping("/add-by-verify-code")
    public R<DeviceInfo> addDeviceByVerifyCode(@RequestBody Map<String, String> requestData) {
        try {
            String verifyCode = requestData.get("verifyCode");

            if (verifyCode == null || verifyCode.trim().isEmpty()) {
                return R.fail("验证码不能为空");
            }

            // 从Redis中获取通过OTA接口缓存的设备信息
            String activationKey = "activation:code:" + verifyCode;
            Map<Object, Object> activationData = redisTemplate.opsForHash().entries(activationKey);

            if (activationData.isEmpty()) {
                return R.fail("验证码无效或已过期，请检查激活码是否正确");
            }

            // 从缓存中获取设备信息
            String macAddress = (String) activationData.get("device_mac");
            String uuid = (String) activationData.get("device_uuid");
            String chipModel = (String) activationData.get("device_chip_model");
            String appVersion = (String) activationData.get("device_app_version");
            String detectedTimeStr = (String) activationData.get("device_detected_time");

            // 检查设备是否已存在（通过MAC地址）
            if (macAddress != null && !macAddress.isEmpty()) {
                DeviceInfo existingByMac = deviceInfoService.queryDeviceBySerialNumber(macAddress);
                if (existingByMac != null) {
                    return R.fail("设备（MAC地址：" + macAddress + "）已存在，无法重复添加");
                }
            }

            // 检查设备是否已存在（通过UUID）
            if (uuid != null && !uuid.isEmpty()) {
                DeviceInfo existingByUuid = deviceInfoService.queryDeviceByUuid(uuid);
                if (existingByUuid != null) {
                    return R.fail("设备（UUID：" + uuid + "）已存在，无法重复添加");
                }
            }

            // 创建新设备，使用从OTA接口获取的信息
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setVerifyCode(verifyCode); // 设置验证码
            deviceInfo.setDeviceState(DeviceState.ACTIVE);

            // 设置从OTA缓存中获取的设备信息
            if (macAddress != null && !macAddress.isEmpty()) {
                deviceInfo.setSerialNumber(macAddress);
            }

            if (uuid != null && !uuid.isEmpty()) {
                deviceInfo.setUuid(uuid);
            }

            // 自动生成设备名称
            if (macAddress != null && !macAddress.isEmpty()) {
                // 使用MAC地址后6位作为设备名称
                String macSuffix = macAddress.substring(Math.max(0, macAddress.length() - 6));
                deviceInfo.setName("设备-" + macSuffix);
            } else {
                // 如果没有MAC地址，使用时间戳
                deviceInfo.setName("新设备-" + System.currentTimeMillis());
            }

            // 设置设备类型
            if (chipModel != null && !chipModel.isEmpty()) {
                deviceInfo.setDeviceKind(chipModel);
            } else {
                deviceInfo.setDeviceKind("esp32"); // 默认设备类型
            }

            // 设置程序版本
            if (appVersion != null && !appVersion.isEmpty()) {
                deviceInfo.setProgramVer(appVersion);
            }


            deviceInfo.setLastSeenTime(LocalDateTime.now());

            boolean result = deviceInfoService.save(deviceInfo);
            if (result) {
                // 添加成功后，清理Redis中的激活码缓存
                redisTemplate.delete(activationKey);

                // 如果有MAC地址，清理MAC到激活码的映射
                if (macAddress != null && !macAddress.isEmpty()) {
                    String macToCodeKey = "activation:mac_to_code:" + macAddress;
                    redisTemplate.delete(macToCodeKey);
                }

                log.info("通过验证码添加设备成功: 验证码={}, MAC={}, UUID={}, 设备名称={}",
                        verifyCode, macAddress, uuid, deviceInfo.getName());
                return R.ok("设备添加成功", deviceInfo);
            } else {
                return R.fail("设备添加失败");
            }
        } catch (Exception e) {
            log.error("通过验证码添加设备失败: ", e);
            return R.fail("通过验证码添加设备失败: " + e.getMessage());
        }
    }


}
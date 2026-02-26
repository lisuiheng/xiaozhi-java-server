package com.github.lisuiheng.astra.server.ai.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.lisuiheng.astra.server.ai.mapper.AgentDeviceBindingMapper;
import com.github.lisuiheng.astra.server.ai.model.entity.AgentDeviceBinding;
import com.github.lisuiheng.astra.server.server.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceBindingService extends ServiceImpl<AgentDeviceBindingMapper, AgentDeviceBinding> {
    
    private final AgentDeviceBindingMapper bindingMapper;
    private final DeviceService deviceService;
    
    /**
     * 创建设备绑定
     */
    @Transactional(rollbackFor = Exception.class)
    public AgentDeviceBinding createBinding(String agentId, String deviceId, String bindingType, LocalDateTime expireTime) {
        log.info("创建设备绑定: agentId={}, deviceId={}, bindingType={}", agentId, deviceId, bindingType);
        
        // 先停用设备的所有现有绑定
        deactivateAllBindingsForDevice(deviceId);
        
        // 创建新的绑定记录
        AgentDeviceBinding binding = new AgentDeviceBinding();
        binding.setAgentId(agentId);
        binding.setDeviceId(deviceId);
        binding.setBindingType(bindingType != null ? bindingType : "default");
        binding.setBindingTime(LocalDateTime.now());
        binding.setExpireTime(expireTime);
        binding.setIsActive(true);
        
        if (!save(binding)) {
            throw new RuntimeException("创建设备绑定失败");
        }
        
        // 更新设备的绑定智能体ID
        deviceService.updateDeviceAgentId(deviceId, agentId);
        
        log.info("设备绑定创建成功, ID: {}", binding.getId());
        return binding;
    }
    
    /**
     * 解绑设备
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean unbindDevice(String deviceId) {
        log.info("解绑设备: {}", deviceId);
        
        // 更新设备的绑定智能体ID为null
        deviceService.updateDeviceAgentId(deviceId, null);
        
        return deactivateAllBindingsForDevice(deviceId);
    }
    
    /**
     * 获取设备当前激活的绑定
     */
    public AgentDeviceBinding getActiveBindingByDevice(String deviceId) {
        return bindingMapper.selectActiveBindingByDevice(deviceId);
    }
    
    /**
     * 获取智能体的所有绑定
     */
    public List<AgentDeviceBinding> getBindingsByAgent(String agentId) {
        return bindingMapper.selectBindingsByAgent(agentId);
    }
    
    /**
     * 停用设备的所有绑定
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deactivateAllBindingsForDevice(String deviceId) {
        log.info("停用设备的所有绑定: {}", deviceId);
        
        LambdaUpdateWrapper<AgentDeviceBinding> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AgentDeviceBinding::getDeviceId, deviceId)
               .eq(AgentDeviceBinding::getIsActive, true)
               .set(AgentDeviceBinding::getIsActive, false)
               .set(AgentDeviceBinding::getUpdatedTime, LocalDateTime.now());
        
        return update(wrapper);
    }
    
    /**
     * 获取过期的绑定
     */
    public List<AgentDeviceBinding> getExpiredBindings() {
        return bindingMapper.selectExpiredBindings(LocalDateTime.now());
    }
    
    /**
     * 根据绑定类型查询
     */
    public List<AgentDeviceBinding> getBindingsByType(String bindingType) {
        return bindingMapper.selectByBindingType(bindingType);
    }
}
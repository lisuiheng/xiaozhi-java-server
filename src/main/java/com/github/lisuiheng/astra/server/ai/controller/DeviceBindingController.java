package com.github.lisuiheng.astra.server.ai.controller;

import com.github.lisuiheng.astra.server.ai.model.dto.BindDeviceRequest;
import com.github.lisuiheng.astra.server.ai.model.entity.AgentDeviceBinding;
import com.github.lisuiheng.astra.server.ai.service.DeviceBindingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/device-bindings")
@RequiredArgsConstructor
public class DeviceBindingController {
    
    private final DeviceBindingService deviceBindingService;
    
    /**
     * 创建设备绑定
     */
    @PostMapping
    public AgentDeviceBinding createBinding(@RequestBody BindDeviceRequest request) {
        return deviceBindingService.createBinding(
            request.getAgentId(), 
            request.getDeviceId(), 
            request.getBindingType(), 
            request.getExpireTime()
        );
    }
    
    /**
     * 解绑设备
     */
    @DeleteMapping("/device/{deviceId}")
    public boolean unbindDevice(@PathVariable String deviceId) {
        return deviceBindingService.unbindDevice(deviceId);
    }
    
    /**
     * 获取设备当前激活的绑定
     */
    @GetMapping("/device/{deviceId}/active")
    public AgentDeviceBinding getActiveBindingByDevice(@PathVariable String deviceId) {
        return deviceBindingService.getActiveBindingByDevice(deviceId);
    }
    
    /**
     * 获取智能体的所有绑定
     */
    @GetMapping("/agent/{agentId}")
    public List<AgentDeviceBinding> getBindingsByAgent(@PathVariable String agentId) {
        return deviceBindingService.getBindingsByAgent(agentId);
    }
    
    /**
     * 获取过期的绑定
     */
    @GetMapping("/expired")
    public List<AgentDeviceBinding> getExpiredBindings() {
        return deviceBindingService.getExpiredBindings();
    }
    
    /**
     * 根据绑定类型查询
     */
    @GetMapping("/type/{bindingType}")
    public List<AgentDeviceBinding> getBindingsByType(@PathVariable String bindingType) {
        return deviceBindingService.getBindingsByType(bindingType);
    }
}
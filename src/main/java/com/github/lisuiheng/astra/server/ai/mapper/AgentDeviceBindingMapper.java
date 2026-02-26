package com.github.lisuiheng.astra.server.ai.mapper;

import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import com.github.lisuiheng.astra.server.ai.model.entity.AgentDeviceBinding;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 智能体设备绑定 Mapper 接口（无XML版本）
 */
@Mapper
public interface AgentDeviceBindingMapper extends BaseMapperPlus<AgentDeviceBinding, AgentDeviceBinding> {

    /**
     * 查询设备当前激活的绑定
     */
    @Select("SELECT * FROM agent_device_binding WHERE device_id = #{deviceId} AND is_active = true ORDER BY priority DESC, binding_time DESC LIMIT 1")
    AgentDeviceBinding selectActiveBindingByDevice(@Param("deviceId") String deviceId);

    /**
     * 查询智能体的所有绑定
     */
    @Select("SELECT * FROM agent_device_binding WHERE agent_id = #{agentId} ORDER BY binding_time DESC")
    List<AgentDeviceBinding> selectBindingsByAgent(@Param("agentId") String agentId);

    /**
     * 查询过期的绑定
     */
    @Select("SELECT * FROM agent_device_binding WHERE expire_time < #{now} AND is_active = true")
    List<AgentDeviceBinding> selectExpiredBindings(@Param("now") LocalDateTime now);

    /**
     * 停用设备的所有绑定
     */
    @Update("UPDATE agent_device_binding SET is_active = false, updated_time = NOW() WHERE device_id = #{deviceId} AND is_active = true")
    int deactivateAllBindingsForDevice(@Param("deviceId") String deviceId);

    /**
     * 根据绑定类型查询
     */
    @Select("SELECT * FROM agent_device_binding WHERE binding_type = #{bindingType} AND is_active = true")
    List<AgentDeviceBinding> selectByBindingType(@Param("bindingType") String bindingType);
}
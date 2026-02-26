package com.github.lisuiheng.astra.server.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.lisuiheng.astra.server.ai.mapper.AgentMapper;
import com.github.lisuiheng.astra.server.ai.model.dto.AgentDTO;
import com.github.lisuiheng.astra.server.ai.model.entity.Agent;
import com.github.lisuiheng.astra.server.ai.model.entity.AgentDeviceBinding;
import com.github.lisuiheng.astra.common.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 智能体 Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentService extends ServiceImpl<AgentMapper, Agent> {

    private final AgentMapper agentMapper;
    private final DeviceBindingService deviceBindingService;

    // ========== 基础CRUD操作 ==========

    /**
     * 创建智能体
     */
    @Transactional(rollbackFor = Exception.class)
    public Agent createAgent(Agent agentData) {
        log.info("创建智能体: {}", agentData.getAgentName());

        // 检查名称是否重复
        Agent existAgent = agentMapper.selectByAgentName(agentData.getAgentName());
        if (existAgent != null) {
            throw new RuntimeException("智能体名称已存在");
        }

        // 设置ID和默认状态
        agentData.setId(java.util.UUID.randomUUID().toString());
        agentData.setStatus(1); // 默认启用

        // 保存到数据库
        if (!save(agentData)) {
            throw new RuntimeException("创建智能体失败");
        }

        log.info("智能体创建成功, ID: {}", agentData.getId());
        return agentData;
    }

    /**
     * 更新智能体
     */
    @Transactional(rollbackFor = Exception.class)
    public Agent updateAgent(String agentId, Agent agentData) {
        log.info("更新智能体: {}", agentId);

        // 检查智能体是否存在
        Agent agent = getById(agentId);
        if (agent == null || agent.getDeleted() == 1) {
            throw new RuntimeException("智能体不存在");
        }

        // 检查名称是否重复
        if (agentData.getAgentName() != null && !agentData.getAgentName().equals(agent.getAgentName())) {
            Agent existAgent = agentMapper.selectByAgentName(agentData.getAgentName());
            if (existAgent != null && !existAgent.getId().equals(agentId)) {
                throw new RuntimeException("智能体名称已存在");
            }
        }

        // 更新字段，保留ID和创建时间
        agentData.setId(agent.getId());
        agentData.setCreateTime(agent.getCreateTime());

        // 更新数据库
        if (!updateById(agentData)) {
            throw new RuntimeException("更新智能体失败");
        }

        log.info("智能体更新成功, ID: {}", agentId);
        return agentData;
    }

    /**
     * 删除智能体（逻辑删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAgent(String agentId) {
        log.info("删除智能体: {}", agentId);

        Agent agent = getById(agentId);
        if (agent == null) {
            throw new RuntimeException("智能体不存在");
        }

        // 逻辑删除
        agent.setDeleted(1);
        agent.setUpdateTime(LocalDateTime.now());

        return updateById(agent);
    }

    /**
     * 批量删除智能体
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteAgents(List<String> agentIds) {
        log.info("批量删除智能体: {}", agentIds);

        if (agentIds == null || agentIds.isEmpty()) {
            return true;
        }

        // 使用UpdateWrapper进行批量逻辑删除
        LambdaUpdateWrapper<Agent> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Agent::getId, agentIds)
               .set(Agent::getDeleted, 1)
               .set(Agent::getUpdateTime, LocalDateTime.now());

        return update(wrapper);
    }

    // ========== 查询操作 ==========

    /**
     * 根据ID获取智能体详情
     */
    public Map<String, Object> getAgentDetail(String agentId) {
        log.debug("获取智能体详情: {}", agentId);

        Agent agent = getById(agentId);
        if (agent == null || agent.getDeleted() == 1) {
            throw new RuntimeException("智能体不存在");
        }

        // 转换为Map
        Map<String, Object> result = new HashMap<>();
        result.put("id", agent.getId());
        result.put("agentName", agent.getAgentName());
        result.put("nickname", agent.getNickname());
        result.put("description", agent.getDescription());
        result.put("llmType", agent.getLlmType());
        result.put("modelName", agent.getModelName());
        result.put("apiBaseUrl", agent.getApiBaseUrl());
        result.put("temperature", agent.getTemperature());
        result.put("maxTokens", agent.getMaxTokens());
        result.put("systemPrompt", agent.getSystemPrompt());
        result.put("welcomeMessage", agent.getWelcomeMessage());
        result.put("avatarUrl", agent.getAvatarUrl());
        result.put("category", agent.getCategory());
        result.put("status", agent.getStatus());
        result.put("priority", agent.getPriority());
        result.put("enableRag", agent.getEnableRag());
        result.put("ragThreshold", agent.getRagThreshold());
        result.put("ragTopK", agent.getRagTopK());
        result.put("enableMemory", agent.getEnableMemory());
        result.put("memoryType", agent.getMemoryType());
        result.put("memoryConfig", agent.getMemoryConfig());
        result.put("kbConfigs", agent.getKbConfigs());
        result.put("totalCalls", agent.getTotalCalls());
        result.put("totalTokens", agent.getTotalTokens());
        result.put("createdTime", agent.getCreateTime());
        result.put("updatedTime", agent.getUpdateTime());

        return result;
    }

    /**
     * 分页查询智能体
     */
    public IPage<Agent> getAgentPage(Integer pageNum, Integer pageSize, Map<String, Object> params) {
        log.debug("分页查询智能体, pageNum: {}, pageSize: {}", pageNum, pageSize);

        Page<Agent> page = new Page<>(pageNum, pageSize);
        return agentMapper.selectAgentPage(page, params);
    }

    /**
     * 分页查询智能体（返回DTO，脱敏）
     */
    public IPage<AgentDTO> getAgentPageDTO(Integer pageNum, Integer pageSize, Map<String, Object> params) {
        log.debug("分页查询智能体（DTO）, pageNum: {}, pageSize: {}", pageNum, pageSize);

        Page<Agent> page = new Page<>(pageNum, pageSize);
        IPage<Agent> agentPage = agentMapper.selectAgentPage(page, params);
        
        // 将Agent实体转换为AgentDTO
        List<AgentDTO> agentDTOList = agentPage.getRecords().stream()
            .map(AgentDTO::fromEntity)
            .collect(Collectors.toList());
        
        // 创建一个新的IPage<AgentDTO>并设置相关属性
        Page<AgentDTO> resultPage = new Page<>();
        resultPage.setRecords(agentDTOList);
        resultPage.setCurrent(agentPage.getCurrent());
        resultPage.setSize(agentPage.getSize());
        resultPage.setTotal(agentPage.getTotal());
        
        return resultPage;
    }

    /**
     * 根据分类查询智能体列表
     */
    public List<Map<String, Object>> getAgentsByCategory(String category) {
        log.debug("根据分类查询智能体: {}", category);

        List<Agent> agents = agentMapper.selectByCategory(category);

        return agents.stream()
            .map(agent -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", agent.getId());
                map.put("agentName", agent.getAgentName());
                map.put("nickname", agent.getNickname());
                map.put("description", agent.getDescription());
                map.put("avatarUrl", agent.getAvatarUrl());
                map.put("category", agent.getCategory());
                map.put("status", agent.getStatus());
                return map;
            })
            .collect(Collectors.toList());
    }

    /**
     * 搜索智能体
     */
    public List<Map<String, Object>> searchAgents(String keyword, String category) {
        List<Agent> agents = agentMapper.searchAgents(keyword, category);

        return agents.stream()
            .map(agent -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", agent.getId());
                map.put("agentName", agent.getAgentName());
                map.put("nickname", agent.getNickname());
                map.put("description", agent.getDescription());
                map.put("avatarUrl", agent.getAvatarUrl());
                map.put("category", agent.getCategory());
                map.put("enableRag", agent.getEnableRag());
                map.put("enableMemory", agent.getEnableMemory());
                map.put("totalCalls", agent.getTotalCalls());
                return map;
            })
            .collect(Collectors.toList());
    }

    // ========== 状态管理 ==========

    /**
     * 启用/禁用智能体
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleAgentStatus(String agentId, Integer status) {
        log.info("更新智能体状态, agentId: {}, status: {}", agentId, status);

        if (!Arrays.asList(0, 1).contains(status)) {
            throw new RuntimeException("状态值不合法");
        }

        LambdaUpdateWrapper<Agent> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Agent::getId, agentId)
               .eq(Agent::getDeleted, 0)
               .set(Agent::getStatus, status)
               .set(Agent::getUpdateTime, LocalDateTime.now());

        return update(wrapper);
    }

    /**
     * 批量更新智能体状态
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateAgentStatus(List<String> agentIds, Integer status) {
        log.info("批量更新智能体状态, agentIds: {}, status: {}", agentIds, status);

        if (agentIds == null || agentIds.isEmpty()) {
            return true;
        }

        if (!Arrays.asList(0, 1).contains(status)) {
            throw new RuntimeException("状态值不合法");
        }

        int rows = agentMapper.batchUpdateStatus(agentIds, status);
        return rows > 0;
    }

    // ========== 克隆和模板操作 ==========

    /**
     * 克隆智能体
     */
    @Transactional(rollbackFor = Exception.class)
    public Agent cloneAgent(String sourceAgentId, Map<String, Object> agentData) {
        log.info("克隆智能体, sourceAgentId: {}", sourceAgentId);

        Agent sourceAgent = getById(sourceAgentId);
        if (sourceAgent == null || sourceAgent.getDeleted() == 1) {
            throw new RuntimeException("源智能体不存在");
        }

        // 检查目标名称是否重复
        String agentName = (String) agentData.get("agentName");
        Agent existAgent = agentMapper.selectByAgentName(agentName);
        if (existAgent != null) {
            throw new RuntimeException("智能体名称已存在");
        }

        // 克隆智能体
        Agent newAgent = new Agent();
        BeanUtils.copyProperties(sourceAgent, newAgent,
            "id", "agentName", "nickname", "description",
            "totalTokens", "totalCalls", "createdTime", "updatedTime");

        // 应用覆盖的属性
        newAgent.setId(java.util.UUID.randomUUID().toString());
        newAgent.setAgentName(agentName);
        newAgent.setNickname((String) agentData.get("nickname"));
        newAgent.setDescription((String) agentData.get("description"));
        newAgent.setTotalTokens(0L);
        newAgent.setTotalCalls(0L);

        // 保存到数据库
        if (!save(newAgent)) {
            throw new RuntimeException("克隆智能体失败");
        }

        log.info("智能体克隆成功, newId: {}", newAgent.getId());
        return newAgent;
    }

    /**
     * 根据模板创建智能体
     */
    @Transactional(rollbackFor = Exception.class)
    public Agent createAgentFromTemplate(String templateId, Map<String, Object> agentData) {
        log.info("根据模板创建智能体, templateId: {}", templateId);

        // 查询模板对应的智能体
        List<Agent> templateAgents = agentMapper.selectByTemplateId(templateId);
        if (templateAgents.isEmpty()) {
            throw new RuntimeException("模板不存在或没有对应智能体");
        }

        Agent templateAgent = templateAgents.get(0);

        // 创建新智能体
        Agent newAgent = new Agent();
        BeanUtils.copyProperties(templateAgent, newAgent,
            "id", "agentName", "nickname", "description",
            "totalTokens", "totalCalls", "createdTime", "updatedTime");

        // 应用自定义属性
        newAgent.setId(java.util.UUID.randomUUID().toString());
        newAgent.setAgentName((String) agentData.get("agentName"));
        newAgent.setNickname((String) agentData.get("nickname"));
        newAgent.setDescription((String) agentData.get("description"));
        newAgent.setAgentTemplateId(templateId);
        newAgent.setTotalTokens(0L);
        newAgent.setTotalCalls(0L);

        if (!save(newAgent)) {
            throw new RuntimeException("创建智能体失败");
        }

        return newAgent;
    }

    // ========== 统计和监控 ==========

    /**
     * 更新token使用量
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTokenUsage(String agentId, Long tokens) {
        if (tokens == null || tokens <= 0) {
            return;
        }

        agentMapper.updateTokenUsage(agentId, tokens);
    }

    /**
     * 获取智能体使用统计
     */
    public Map<String, Object> getAgentUsageStats(String agentId) {
        Agent agent = getById(agentId);
        if (agent == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCalls", agent.getTotalCalls());
        stats.put("totalTokens", agent.getTotalTokens());
        stats.put("averageTokens", agent.getTotalCalls() > 0 ?
            agent.getTotalTokens() / agent.getTotalCalls() : 0);
        stats.put("createdTime", agent.getCreateTime());
        stats.put("lastUpdated", agent.getUpdateTime());

        return stats;
    }

    /**
     * 获取总体使用统计
     */
    public Map<String, Object> getTotalUsageStats() {
        return agentMapper.getTotalUsageStats();
    }

    /**
     * 获取各分类统计
     */
    public List<Map<String, Object>> getCategoryStats() {
        return agentMapper.countByCategory();
    }

    /**
     * 获取热门智能体
     */
    public List<Map<String, Object>> getPopularAgents(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        List<Agent> agents = agentMapper.selectPopularAgents(limit);

        return agents.stream()
            .map(agent -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", agent.getId());
                map.put("agentName", agent.getAgentName());
                map.put("nickname", agent.getNickname());
                map.put("totalCalls", agent.getTotalCalls());
                map.put("totalTokens", agent.getTotalTokens());
                return map;
            })
            .collect(Collectors.toList());
    }

    // ========== 验证和测试 ==========

    /**
     * 验证智能体配置
     */
    public boolean validateAgentConfig(String agentId) {
        Agent agent = getById(agentId);
        if (agent == null) {
            return false;
        }

        // 检查必要配置
        if (StringUtils.isBlank(agent.getLlmType())) {
            return false;
        }

        if (StringUtils.isBlank(agent.getModelName())) {
            return false;
        }

        // 检查API配置
        if (Boolean.TRUE.equals(agent.getEnableMemory())) {
            String memoryConfig = agent.getMemoryConfig();
            if (memoryConfig == null || memoryConfig.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * 测试智能体连接
     */
    public boolean testAgentConnection(String agentId) {
        // 这里可以添加实际的大模型连接测试逻辑
        log.debug("测试智能体连接: {}", agentId);
        return true;
    }

    /**
     * 忽略null值的属性拷贝
     */
    private void copyPropertiesIgnoreNull(Map<String, Object> source, Agent target) {
        if (source == null || target == null) {
            return;
        }

        source.forEach((key, value) -> {
            if (value != null) {
                try {
                    // 特殊处理kbConfigs字段
                    if ("kbConfigs".equals(key)) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> kbConfigs = (List<Map<String, Object>>) value;
                        target.setKbConfigs(kbConfigs);
                    } else {
                        // 使用反射设置其他属性
                        java.lang.reflect.Field field = target.getClass().getDeclaredField(key);
                        field.setAccessible(true);
                        
                        // 类型转换处理
                        Object convertedValue = convertValueToFieldType(value, field.getType());
                        field.set(target, convertedValue);
                    }
                } catch (Exception e) {
                    log.warn("设置属性失败: {} - {}", key, e.getMessage());
                }
            }
        });
    }
    
    /**
     * 将值转换为字段类型
     */
    private Object convertValueToFieldType(Object value, Class<?> targetType) {
        if (value == null || targetType == null || targetType.isAssignableFrom(value.getClass())) {
            return value;
        }
        
        // 处理数字类型转换
        if (targetType == Long.class || targetType == long.class) {
            if (value instanceof Integer) {
                return ((Integer) value).longValue();
            } else if (value instanceof String) {
                return Long.valueOf((String) value);
            }
        } else if (targetType == Integer.class || targetType == int.class) {
            if (value instanceof Long) {
                return ((Long) value).intValue();
            } else if (value instanceof String) {
                return Integer.valueOf((String) value);
            }
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            if (value instanceof String) {
                return Boolean.valueOf((String) value);
            }
        } else if (targetType == java.math.BigDecimal.class) {
            if (value instanceof Number) {
                return new java.math.BigDecimal(value.toString());
            } else if (value instanceof String) {
                return new java.math.BigDecimal((String) value);
            }
        }
        
        return value;
    }

    // ========== 设备绑定操作 ==========

    /**
     * 绑定设备到智能体
     */
    @Transactional(rollbackFor = Exception.class)
    public AgentDeviceBinding bindDeviceToAgent(String agentId, String deviceId, String bindingType, LocalDateTime expireTime) {
        log.info("绑定设备到智能体: agentId={}, deviceId={}", agentId, deviceId);
        
        // 检查智能体是否存在
        Agent agent = getById(agentId);
        if (agent == null || agent.getDeleted() == 1) {
            throw new RuntimeException("智能体不存在");
        }
        
        // 创建设备绑定
        return deviceBindingService.createBinding(agentId, deviceId, bindingType, expireTime);
    }

    /**
     * 解绑设备
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean unbindDeviceFromAgent(String deviceId) {
        log.info("解绑设备: {}", deviceId);
        return deviceBindingService.unbindDevice(deviceId);
    }

    /**
     * 获取设备当前绑定的智能体
     */
    public AgentDeviceBinding getDeviceCurrentBinding(String deviceId) {
        return deviceBindingService.getActiveBindingByDevice(deviceId);
    }

    /**
     * 获取智能体绑定的设备列表
     */
    public List<AgentDeviceBinding> getAgentDeviceBindings(String agentId) {
        return deviceBindingService.getBindingsByAgent(agentId);
    }
}
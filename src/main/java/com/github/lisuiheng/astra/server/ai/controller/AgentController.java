package com.github.lisuiheng.astra.server.ai.controller;

import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.domain.R;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.lisuiheng.astra.server.ai.model.entity.Agent;
import com.github.lisuiheng.astra.server.ai.model.dto.AgentDTO;
import com.github.lisuiheng.astra.server.ai.service.AgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 智能体 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    /**
     * 创建智能体
     */
    @PostMapping
    public R<Agent> createAgent(@RequestBody Agent agentData) {
        try {
            Agent agent = agentService.createAgent(agentData);
            return R.ok("创建成功", agent);
        } catch (Exception e) {
            log.error("创建智能体失败: ", e);
            return R.fail("创建智能体失败: " + e.getMessage());
        }
    }

    /**
     * 更新智能体
     */
    @PutMapping("/{agentId}")
    public R<Agent> updateAgent(@PathVariable String agentId, @RequestBody Agent agentData) {
        try {
            Agent agent = agentService.updateAgent(agentId, agentData);
            return R.ok("更新成功", agent);
        } catch (Exception e) {
            log.error("更新智能体失败: ", e);
            return R.fail("更新智能体失败: " + e.getMessage());
        }
    }

    /**
     * 删除智能体
     */
    @DeleteMapping("/{agentId}")
    public R<Boolean> deleteAgent(@PathVariable String agentId) {
        try {
            boolean result = agentService.deleteAgent(agentId);
            return result ? R.ok("删除成功", true) : R.fail("删除失败", false);
        } catch (Exception e) {
            log.error("删除智能体失败: ", e);
            return R.fail("删除智能体失败: " + e.getMessage(), false);
        }
    }

    /**
     * 批量删除智能体
     */
    @DeleteMapping("/batch")
    public R<Boolean> batchDeleteAgents(@RequestBody List<String> agentIds) {
        try {
            boolean result = agentService.batchDeleteAgents(agentIds);
            return result ? R.ok("批量删除成功", true) : R.fail("批量删除失败", false);
        } catch (Exception e) {
            log.error("批量删除智能体失败: ", e);
            return R.fail("批量删除智能体失败: " + e.getMessage(), false);
        }
    }

    /**
     * 获取智能体详情
     */
    @GetMapping("/{agentId}")
    public R<Map<String, Object>> getAgentDetail(@PathVariable String agentId) {
        try {
            Map<String, Object> agentDetail = agentService.getAgentDetail(agentId);
            return R.ok("查询成功", agentDetail);
        } catch (Exception e) {
            log.error("获取智能体详情失败: ", e);
            return R.fail("获取智能体详情失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询智能体
     */
    @GetMapping
    public TableDataInfo<AgentDTO> getAgentPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String agentName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("agentName", agentName);
        params.put("category", category);
        params.put("status", status);
        
        IPage<AgentDTO> page = agentService.getAgentPageDTO(pageNum, pageSize, params);
        return TableDataInfo.build(page);
    }

    /**
     * 根据分类查询智能体列表
     */
    @GetMapping("/category/{category}")
    public R<List<Map<String, Object>>> getAgentsByCategory(@PathVariable String category) {
        try {
            List<Map<String, Object>> agents = agentService.getAgentsByCategory(category);
            return R.ok("查询成功", agents);
        } catch (Exception e) {
            log.error("根据分类查询智能体失败: ", e);
            return R.fail("根据分类查询智能体失败: " + e.getMessage());
        }
    }

    /**
     * 搜索智能体
     */
    @GetMapping("/search")
    public R<List<Map<String, Object>>> searchAgents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        try {
            List<Map<String, Object>> agents = agentService.searchAgents(keyword, category);
            return R.ok("搜索成功", agents);
        } catch (Exception e) {
            log.error("搜索智能体失败: ", e);
            return R.fail("搜索智能体失败: " + e.getMessage());
        }
    }

    /**
     * 启用/禁用智能体
     */
    @PutMapping("/{agentId}/status")
    public R<Boolean> toggleAgentStatus(@PathVariable String agentId, @RequestParam Integer status) {
        try {
            boolean result = agentService.toggleAgentStatus(agentId, status);
            return result ? R.ok("状态更新成功", true) : R.fail("状态更新失败", false);
        } catch (Exception e) {
            log.error("启用/禁用智能体失败: ", e);
            return R.fail("启用/禁用智能体失败: " + e.getMessage(), false);
        }
    }

    /**
     * 批量更新智能体状态
     */
    @PutMapping("/batch/status")
    public R<Boolean> batchUpdateAgentStatus(@RequestBody List<String> agentIds, @RequestParam Integer status) {
        try {
            boolean result = agentService.batchUpdateAgentStatus(agentIds, status);
            return result ? R.ok("批量状态更新成功", true) : R.fail("批量状态更新失败", false);
        } catch (Exception e) {
            log.error("批量更新智能体状态失败: ", e);
            return R.fail("批量更新智能体状态失败: " + e.getMessage(), false);
        }
    }

    /**
     * 克隆智能体
     */
    @PostMapping("/{sourceAgentId}/clone")
    public R<Agent> cloneAgent(@PathVariable String sourceAgentId, @RequestBody Map<String, Object> agentData) {
        try {
            Agent agent = agentService.cloneAgent(sourceAgentId, agentData);
            return R.ok("克隆成功", agent);
        } catch (Exception e) {
            log.error("克隆智能体失败: ", e);
            return R.fail("克隆智能体失败: " + e.getMessage());
        }
    }

    /**
     * 根据模板创建智能体
     */
    @PostMapping("/template/{templateId}")
    public R<Agent> createAgentFromTemplate(@PathVariable String templateId, @RequestBody Map<String, Object> agentData) {
        try {
            Agent agent = agentService.createAgentFromTemplate(templateId, agentData);
            return R.ok("创建成功", agent);
        } catch (Exception e) {
            log.error("根据模板创建智能体失败: ", e);
            return R.fail("根据模板创建智能体失败: " + e.getMessage());
        }
    }

    /**
     * 更新token使用量
     */
    @PutMapping("/{agentId}/usage")
    public R<Void> updateTokenUsage(@PathVariable String agentId, @RequestParam Long tokens) {
        try {
            agentService.updateTokenUsage(agentId, tokens);
            return R.ok("Token使用量更新成功");
        } catch (Exception e) {
            log.error("更新token使用量失败: ", e);
            return R.fail("更新token使用量失败: " + e.getMessage());
        }
    }

    /**
     * 获取智能体使用统计
     */
    @GetMapping("/{agentId}/stats")
    public R<Map<String, Object>> getAgentUsageStats(@PathVariable String agentId) {
        try {
            Map<String, Object> stats = agentService.getAgentUsageStats(agentId);
            return R.ok("查询成功", stats);
        } catch (Exception e) {
            log.error("获取智能体使用统计失败: ", e);
            return R.fail("获取智能体使用统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取总体使用统计
     */
    @GetMapping("/stats/total")
    public R<Map<String, Object>> getTotalUsageStats() {
        try {
            Map<String, Object> stats = agentService.getTotalUsageStats();
            return R.ok("查询成功", stats);
        } catch (Exception e) {
            log.error("获取总体使用统计失败: ", e);
            return R.fail("获取总体使用统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取各分类统计
     */
    @GetMapping("/stats/category")
    public R<List<Map<String, Object>>> getCategoryStats() {
        try {
            List<Map<String, Object>> stats = agentService.getCategoryStats();
            return R.ok("查询成功", stats);
        } catch (Exception e) {
            log.error("获取分类统计失败: ", e);
            return R.fail("获取分类统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门智能体
     */
    @GetMapping("/popular")
    public R<List<Map<String, Object>>> getPopularAgents(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Map<String, Object>> agents = agentService.getPopularAgents(limit);
            return R.ok("查询成功", agents);
        } catch (Exception e) {
            log.error("获取热门智能体失败: ", e);
            return R.fail("获取热门智能体失败: " + e.getMessage());
        }
    }

    /**
     * 验证智能体配置
     */
    @GetMapping("/{agentId}/validate")
    public R<Boolean> validateAgentConfig(@PathVariable String agentId) {
        try {
            boolean result = agentService.validateAgentConfig(agentId);
            return R.ok("验证成功", result);
        } catch (Exception e) {
            log.error("验证智能体配置失败: ", e);
            return R.fail("验证智能体配置失败: " + e.getMessage(), false);
        }
    }



    /**
     * 绑定设备到智能体
     */
    @PostMapping("/{agentId}/bind-device")
    public R<com.github.lisuiheng.astra.server.ai.model.entity.AgentDeviceBinding> bindDeviceToAgent(
            @PathVariable String agentId,
            @RequestBody com.github.lisuiheng.astra.server.ai.model.dto.BindDeviceRequest request) {
        
        try {
            // 确保agentId与路径参数一致
            request.setAgentId(agentId);
            
            com.github.lisuiheng.astra.server.ai.model.entity.AgentDeviceBinding binding = agentService.bindDeviceToAgent(
                request.getAgentId(), 
                request.getDeviceId(), 
                request.getBindingType(), 
                request.getExpireTime()
            );
            return R.ok("绑定成功", binding);
        } catch (Exception e) {
            log.error("绑定设备到智能体失败: ", e);
            return R.fail("绑定设备到智能体失败: " + e.getMessage());
        }
    }

    /**
     * 解绑设备
     */
    @DeleteMapping("/unbind-device")
    public R<Boolean> unbindDeviceFromAgent(@RequestParam String deviceId) {
        try {
            boolean result = agentService.unbindDeviceFromAgent(deviceId);
            return result ? R.ok("解绑成功", true) : R.fail("解绑失败", false);
        } catch (Exception e) {
            log.error("解绑设备失败: ", e);
            return R.fail("解绑设备失败: " + e.getMessage(), false);
        }
    }

    /**
     * 获取设备当前绑定的智能体
     */
    @GetMapping("/device/{deviceId}/binding")
    public R<com.github.lisuiheng.astra.server.ai.model.entity.AgentDeviceBinding> getDeviceCurrentBinding(@PathVariable String deviceId) {
        try {
            com.github.lisuiheng.astra.server.ai.model.entity.AgentDeviceBinding binding = agentService.getDeviceCurrentBinding(deviceId);
            return R.ok("查询成功", binding);
        } catch (Exception e) {
            log.error("获取设备当前绑定的智能体失败: ", e);
            return R.fail("获取设备当前绑定的智能体失败: " + e.getMessage());
        }
    }

    /**
     * 获取智能体绑定的设备列表
     */
    @GetMapping("/{agentId}/bindings")
    public R<java.util.List<com.github.lisuiheng.astra.server.ai.model.entity.AgentDeviceBinding>> getAgentDeviceBindings(@PathVariable String agentId) {
        try {
            java.util.List<com.github.lisuiheng.astra.server.ai.model.entity.AgentDeviceBinding> bindings = agentService.getAgentDeviceBindings(agentId);
            return R.ok("查询成功", bindings);
        } catch (Exception e) {
            log.error("获取智能体绑定的设备列表失败: ", e);
            return R.fail("获取智能体绑定的设备列表失败: " + e.getMessage());
        }
    }
}
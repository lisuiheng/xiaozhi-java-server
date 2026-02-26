package com.github.lisuiheng.astra.server.ai.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.lisuiheng.astra.server.ai.model.entity.Agent;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 智能体 Mapper 接口（无XML版本）
 */
@Mapper
public interface AgentMapper extends BaseMapperPlus<Agent, Agent> {

    // ========== 自定义SQL方法 ==========

    /**
     * 根据名称查询智能体（使用注解SQL）
     */
    @Select("SELECT * FROM server_agent WHERE agent_name = #{agentName} AND deleted = 0")
    Agent selectByAgentName(@Param("agentName") String agentName);

    /**
     * 根据分类查询智能体列表
     */
    @Select("SELECT * FROM server_agent WHERE category = #{category} AND status = 1 AND deleted = 0 ORDER BY priority DESC, create_time DESC")
    List<Agent> selectByCategory(@Param("category") String category);

    /**
     * 根据状态查询智能体列表
     */
    @Select("SELECT * FROM server_agent WHERE status = #{status} AND deleted = 0 ORDER BY create_time DESC")
    List<Agent> selectByStatus(@Param("status") Integer status);

    /**
     * 根据模板ID查询智能体
     */
    @Select("SELECT * FROM server_agent WHERE agent_template_id = #{templateId} AND deleted = 0")
    List<Agent> selectByTemplateId(@Param("templateId") String templateId);

    /**
     * 查询启用的智能体列表
     */
    @Select("SELECT * FROM server_agent WHERE status = 1 AND deleted = 0 ORDER BY priority DESC, create_time DESC")
    List<Agent> selectEnabledAgents();

    /**
     * 根据模型类型查询智能体
     */
    @Select("SELECT * FROM server_agent WHERE llm_type = #{llmType} AND status = 1 AND deleted = 0")
    List<Agent> selectByLlmType(@Param("llmType") String llmType);

    /**
     * 更新智能体token使用量
     */
    @Update("UPDATE server_agent SET total_tokens = total_tokens + #{tokens}, total_calls = total_calls + 1, update_time = NOW() WHERE id = #{agentId} AND deleted = 0")
    int updateTokenUsage(@Param("agentId") String agentId, @Param("tokens") Long tokens);

    /**
     * 批量更新智能体状态
     */
    @Update("<script>" +
            "UPDATE server_agent SET status = #{status}, update_time = NOW() " +
            "WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " AND deleted = 0" +
            "</script>")
    int batchUpdateStatus(@Param("ids") List<String> ids, @Param("status") Integer status);

    /**
     * 统计各分类智能体数量
     */
    @Select("SELECT category, COUNT(*) as count FROM server_agent WHERE deleted = 0 AND status = 1 GROUP BY category")
    List<Map<String, Object>> countByCategory();

    /**
     * 查询热门智能体（按调用次数排序）
     */
    @Select("SELECT * FROM server_agent WHERE deleted = 0 AND status = 1 ORDER BY total_calls DESC LIMIT #{limit}")
    List<Agent> selectPopularAgents(@Param("limit") Integer limit);

    /**
     * 根据条件分页查询智能体（使用Provider）
     */
    @SelectProvider(type = AgentSqlProvider.class, method = "selectAgentPage")
    IPage<Agent> selectAgentPage(Page<Agent> page, @Param("params") Map<String, Object> params);

    /**
     * 根据条件查询智能体（使用Provider）
     */
    @SelectProvider(type = AgentSqlProvider.class, method = "selectByCondition")
    List<Agent> selectByCondition(@Param("condition") Map<String, Object> condition);

    /**
     * 搜索智能体（模糊查询）
     */
    @Select("<script>" +
            "SELECT * FROM server_agent WHERE deleted = 0 AND status = 1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "   AND (agent_name LIKE CONCAT('%', #{keyword}, '%') " +
            "        OR nickname LIKE CONCAT('%', #{keyword}, '%') " +
            "        OR description LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "<if test='category != null and category != \"\"'>" +
            "   AND category = #{category}" +
            "</if>" +
            "ORDER BY priority DESC, create_time DESC" +
            "</script>")
    List<Agent> searchAgents(@Param("keyword") String keyword, @Param("category") String category);

    /**
     * 获取智能体使用统计
     */
    @Select("SELECT " +
            "SUM(total_calls) as total_calls, " +
            "SUM(total_tokens) as total_tokens, " +
            "AVG(total_tokens * 1.0 / NULLIF(total_calls, 0)) as avg_tokens_per_call " +
            "FROM server_agent WHERE deleted = 0 AND status = 1")
    Map<String, Object> getTotalUsageStats();
}
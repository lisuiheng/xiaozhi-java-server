package com.github.lisuiheng.astra.server.ai.mapper;

import java.util.Map;

/**
 * SQL Provider类（用于动态SQL）
 */
public class AgentSqlProvider {

    /**
     * 分页查询智能体的动态SQL
     */
    public String selectAgentPage(Map<String, Object> params) {
        Map<String, Object> queryParams = (Map<String, Object>) params.get("params");
        StringBuilder sql = new StringBuilder("SELECT * FROM server_agent WHERE deleted = 0");

        if (queryParams != null) {
            if (queryParams.get("agentName") != null && !"".equals(queryParams.get("agentName"))) {
                sql.append(" AND agent_name LIKE CONCAT('%', #{params.agentName}, '%')");
            }
            if (queryParams.get("category") != null && !"".equals(queryParams.get("category"))) {
                sql.append(" AND category = #{params.category}");
            }
            if (queryParams.get("status") != null) {
                sql.append(" AND status = #{params.status}");
            }
            if (queryParams.get("llmType") != null && !"".equals(queryParams.get("llmType"))) {
                sql.append(" AND llm_type = #{params.llmType}");
            }
            if (queryParams.get("enableRag") != null) {
                sql.append(" AND enable_rag = #{params.enableRag}");
            }
            if (queryParams.get("enableMemory") != null) {
                sql.append(" AND enable_memory = #{params.enableMemory}");
            }
            if (queryParams.get("startTime") != null) {
                sql.append(" AND create_time >= #{params.startTime}");
            }
            if (queryParams.get("endTime") != null) {
                sql.append(" AND create_time <= #{params.endTime}");
            }
        }

        sql.append(" ORDER BY priority DESC, create_time DESC");
        return sql.toString();
    }

    /**
     * 根据条件查询智能体的动态SQL
     */
    public String selectByCondition(Map<String, Object> params) {
        Map<String, Object> condition = (Map<String, Object>) params.get("condition");
        StringBuilder sql = new StringBuilder("SELECT * FROM server_agent WHERE deleted = 0");

        if (condition != null) {
            if (condition.get("agentName") != null && !"".equals(condition.get("agentName"))) {
                sql.append(" AND agent_name LIKE CONCAT('%', #{condition.agentName}, '%')");
            }
            if (condition.get("nickname") != null && !"".equals(condition.get("nickname"))) {
                sql.append(" AND nickname LIKE CONCAT('%', #{condition.nickname}, '%')");
            }
            if (condition.get("category") != null && !"".equals(condition.get("category"))) {
                sql.append(" AND category = #{condition.category}");
            }
            if (condition.get("enableRag") != null) {
                sql.append(" AND enable_rag = #{condition.enableRag}");
            }
            if (condition.get("enableMemory") != null) {
                sql.append(" AND enable_memory = #{condition.enableMemory}");
            }
            if (condition.get("minCalls") != null) {
                sql.append(" AND total_calls >= #{condition.minCalls}");
            }
            if (condition.get("maxCalls") != null) {
                sql.append(" AND total_calls <= #{condition.maxCalls}");
            }
            if (condition.get("orderBy") != null && !"".equals(condition.get("orderBy"))) {
                sql.append(" ORDER BY ").append(condition.get("orderBy"));
                if (condition.get("orderType") != null && !"".equals(condition.get("orderType"))) {
                    sql.append(" ").append(condition.get("orderType"));
                }
            }
        }

        return sql.toString();
    }
}
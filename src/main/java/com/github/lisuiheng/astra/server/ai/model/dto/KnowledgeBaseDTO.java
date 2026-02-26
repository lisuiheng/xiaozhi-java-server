package com.github.lisuiheng.astra.server.ai.model.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class KnowledgeBaseDTO extends com.github.lisuiheng.astra.common.core.domain.BaseEntity {

    private String id;

    private String kbName; // 知识库名称

    private String description; // 描述

    private String vectorStoreType = "milvus"; // 向量存储类型

    private String embeddingModel; // 嵌入模型

    private Integer docCount = 0; // 文档数量

    private Integer status = 1; // 状态：0-停用，1-启用，2-处理中

    private Boolean isPublic = false; // 是否公开

    // 创建一个从KnowledgeBase实体转换为DTO的静态方法
    public static KnowledgeBaseDTO fromEntity(com.github.lisuiheng.astra.server.ai.model.entity.KnowledgeBase knowledgeBase) {
        if (knowledgeBase == null) {
            return null;
        }

        KnowledgeBaseDTO dto = new KnowledgeBaseDTO();
        dto.setId(knowledgeBase.getId());
        dto.setKbName(knowledgeBase.getKbName());
        dto.setDescription(knowledgeBase.getDescription());
        dto.setVectorStoreType(knowledgeBase.getVectorStoreType());
        dto.setEmbeddingModel(knowledgeBase.getEmbeddingModel());
        dto.setDocCount(knowledgeBase.getDocCount());
        dto.setStatus(knowledgeBase.getStatus());
        dto.setIsPublic(knowledgeBase.getIsPublic());
        dto.setCreateTime(knowledgeBase.getCreateTime());
        dto.setUpdateTime(knowledgeBase.getUpdateTime());
        dto.setCreateBy(knowledgeBase.getCreateBy());
        dto.setUpdateBy(knowledgeBase.getUpdateBy());
        return dto;
    }
}
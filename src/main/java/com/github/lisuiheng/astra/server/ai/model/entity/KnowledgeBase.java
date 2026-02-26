package com.github.lisuiheng.astra.server.ai.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.github.lisuiheng.astra.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "server_knowledge_base", autoResultMap = true)
public class KnowledgeBase extends BaseEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("kb_name")
    private String kbName; // 知识库名称

    @TableField("description")
    private String description; // 描述

    @TableField("vector_store_type")
    private String vectorStoreType = "milvus"; // 向量存储类型

    @TableField("embedding_model")
    private String embeddingModel; // 嵌入模型

    @TableField("doc_count")
    private Integer docCount = 0; // 文档数量

    @TableField("status")
    private Integer status = 1; // 状态：0-停用，1-启用，2-处理中

    @TableField("is_public")
    private Boolean isPublic = false; // 是否公开

}
package com.github.lisuiheng.astra.server.ai.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.github.lisuiheng.astra.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "server_knowledge_document", autoResultMap = true)
public class KnowledgeDocument extends BaseEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("kb_id")
    private String kbId; // 知识库ID

    @TableField("doc_name")
    private String docName; // 文档名称

    @TableField("file_name")
    private String fileName; // 文件名

    @TableField("file_size")
    private Long fileSize; // 文件大小

    @TableField("file_type")
    private String fileType; // 文件类型

    @TableField("content")
    private String content; // 文档内容

    @TableField("content_summary")
    private String contentSummary; // 内容摘要

    @TableField("status")
    private Integer status = 1; // 状态：0-删除，1-正常，2-处理中，3-失败

    @TableField("processed_at")
    private Date processedAt; // 处理时间

    @TableField("vector_id")
    private String vectorId; // 向量数据库中的ID

    @TableField("embedding_status")
    private Integer embeddingStatus = 0; // 向量状态：0-未嵌入，1-已嵌入，2-嵌入失败
}
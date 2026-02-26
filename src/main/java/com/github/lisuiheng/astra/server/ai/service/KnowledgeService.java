package com.github.lisuiheng.astra.server.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.lisuiheng.astra.server.ai.mapper.KnowledgeBaseMapper;
import com.github.lisuiheng.astra.server.ai.mapper.KnowledgeDocumentMapper;
import com.github.lisuiheng.astra.server.ai.model.dto.*;
import com.github.lisuiheng.astra.server.ai.model.entity.KnowledgeBase;
import com.github.lisuiheng.astra.server.ai.model.entity.KnowledgeDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KnowledgeService extends ServiceImpl<KnowledgeBaseMapper, KnowledgeBase> {

    @Autowired
    private KnowledgeDocumentMapper documentMapper;

    @Autowired
    private VectorStore vectorStore;

    // ========== 知识库管理 ==========

    /**
     * 创建知识库
     */
    @Transactional(rollbackFor = Exception.class)
    public KnowledgeBase createKnowledgeBase(CreateKnowledgeBaseRequest request) {
        log.info("创建知识库: {}", request.getKbName());

        // 检查名称是否重复
        LambdaQueryWrapper<KnowledgeBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeBase::getKbName, request.getKbName());
        if (this.count(wrapper) > 0) {
            throw new RuntimeException("知识库名称已存在");
        }

        // 创建知识库
        KnowledgeBase kb = new KnowledgeBase()
                .setId(UUID.randomUUID().toString())
                .setKbName(request.getKbName())
                .setDescription(request.getDescription())
                .setVectorStoreType("qdrant")
                .setEmbeddingModel(request.getEmbeddingModel())
                .setIsPublic(request.getIsPublic())
                .setStatus(1);

        this.save(kb);
        return kb;
    }

    /**
     * 删除知识库（包含所有文档）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteKnowledgeBase(String kbId) {
        log.info("删除知识库: {}", kbId);

        KnowledgeBase kb = this.getById(kbId);
        if (kb == null) {
            throw new RuntimeException("知识库不存在");
        }

        // 1. 查询知识库下的所有文档
        LambdaQueryWrapper<KnowledgeDocument> docWrapper = new LambdaQueryWrapper<>();
        docWrapper.eq(KnowledgeDocument::getKbId, kbId)
                 .eq(KnowledgeDocument::getStatus, 1);
        List<KnowledgeDocument> documents = documentMapper.selectList(docWrapper);

        // 2. 从向量数据库中删除所有文档
        if (!documents.isEmpty()) {
            List<String> vectorIds = documents.stream()
                    .filter(doc -> doc.getVectorId() != null)
                    .map(KnowledgeDocument::getVectorId)
                    .collect(Collectors.toList());

            if (!vectorIds.isEmpty()) {
                try {
                    vectorStore.delete(vectorIds);
                    log.info("从向量数据库中删除 {} 个文档", vectorIds.size());
                } catch (Exception e) {
                    log.error("从向量数据库删除文档失败", e);
                    // 继续执行，不要因为向量数据库失败而回滚
                }
            }
        }

        // 3. 删除所有文档记录（逻辑删除）
        documents.forEach(doc -> {
            doc.setStatus(0);
            documentMapper.updateById(doc);
        });

        // 4. 删除知识库记录（逻辑删除）
        kb.setStatus(0);
        this.updateById(kb);

        log.info("知识库删除完成");
    }

    // ========== 文档管理 ==========

    /**
     * 添加文档（直接存储整个文档内容）
     */
    @Transactional(rollbackFor = Exception.class)
    public KnowledgeDocument addDocument(AddDocumentRequest request) {
        log.info("添加文档到知识库: {}, 文档名: {}", request.getKbId(), request.getDocName());

        // 1. 验证知识库
        KnowledgeBase kb = this.getById(request.getKbId());
        if (kb == null) {
            throw new RuntimeException("知识库不存在");
        }

        if (kb.getStatus() != 1) {
            throw new RuntimeException("知识库已停用");
        }

        // 2. 创建文档记录
        String docId = UUID.randomUUID().toString();
        String vectorId = UUID.randomUUID().toString();

        KnowledgeDocument document = new KnowledgeDocument()
                .setId(docId)
                .setKbId(request.getKbId())
                .setDocName(request.getDocName())
                .setFileName(request.getFileName())
                .setFileSize(request.getFileSize())
                .setFileType(request.getFileType())
                .setContent(request.getContent())
                .setVectorId(vectorId)
                .setEmbeddingStatus(0)
                .setStatus(1);

        // 3. 生成内容摘要
        String summary = generateSummary(request.getContent());
        document.setContentSummary(summary);

        documentMapper.insert(document);

        // 4. 异步保存到向量数据库
        try {
            saveToVectorStore(document, kb.getEmbeddingModel());
            document.setEmbeddingStatus(1);
            document.setProcessedAt(new Date());
        } catch (Exception e) {
            log.error("保存到向量数据库失败", e);
            document.setEmbeddingStatus(2);
            throw new RuntimeException("文档向量化失败: " + e.getMessage());
        } finally {
            documentMapper.updateById(document);
        }

        // 5. 更新知识库统计
        kb.setDocCount(kb.getDocCount() + 1);
        this.updateById(kb);

        log.info("文档添加成功，ID: {}", docId);
        return document;
    }

    /**
     * 保存文档到向量数据库
     */
    private void saveToVectorStore(KnowledgeDocument document, String embeddingModel) {
        // 构建元数据，确保不包含null值
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("kb_id", document.getKbId());
        metadata.put("doc_id", document.getId());
        metadata.put("doc_name", document.getDocName());
        metadata.put("file_type", document.getFileType());
        metadata.put("created_at", new Date().getTime());

        // 过滤掉null值
        metadata.entrySet().removeIf(entry -> entry.getValue() == null);

        // 创建 Document 对象
        Document vectorDoc = Document.builder()
                .id(document.getVectorId())
                .text(document.getContent())
                .metadata(metadata)
                .build();

        // 保存到向量数据库
        vectorStore.add(Collections.singletonList(vectorDoc));
        log.debug("文档已保存到向量数据库，向量ID: {}", document.getVectorId());
    }

    /**
     * 更新文档
     */
    @Transactional(rollbackFor = Exception.class)
    public KnowledgeDocument updateDocument(String documentId, UpdateDocumentRequest request) {
        log.info("更新文档: {}", documentId);

        KnowledgeDocument document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new RuntimeException("文档不存在");
        }

        boolean contentChanged = false;

        // 更新基本字段
        if (StringUtils.hasText(request.getDocName())) {
            document.setDocName(request.getDocName());
        }

        // 如果内容有更新，需要重新生成向量
        if (request.getContent() != null &&
            !request.getContent().equals(document.getContent())) {
            document.setContent(request.getContent());
            contentChanged = true;

            // 更新摘要
            String summary = generateSummary(request.getContent());
            document.setContentSummary(summary);
        }

        // 如果内容改变，需要更新向量数据库
        if (contentChanged && document.getVectorId() != null) {
            try {
                // 先删除旧的向量
                vectorStore.delete(Collections.singletonList(document.getVectorId()));

                // 生成新的向量
                KnowledgeBase kb = this.getById(document.getKbId());
                if (kb != null) {
                    saveToVectorStore(document, kb.getEmbeddingModel());
                    document.setEmbeddingStatus(1);
                } else {
                    document.setEmbeddingStatus(2);
                    log.warn("知识库不存在，无法更新向量");
                }
            } catch (Exception e) {
                log.error("更新向量数据库失败", e);
                document.setEmbeddingStatus(2);
                throw new RuntimeException("文档向量更新失败: " + e.getMessage());
            }
        }

        documentMapper.updateById(document);
        return document;
    }

    /**
     * 删除文档（同步删除向量数据库中的记录）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDocument(String documentId) {
        log.info("删除文档: {}", documentId);

        KnowledgeDocument document = documentMapper.selectById(documentId);
        if (document == null) {
            return;
        }

        // 1. 从向量数据库中删除
        if (document.getVectorId() != null) {
            try {
                vectorStore.delete(Collections.singletonList(document.getVectorId()));
                log.debug("已从向量数据库删除向量: {}", document.getVectorId());
            } catch (Exception e) {
                log.error("从向量数据库删除文档失败", e);
                // 继续执行，不要因为向量数据库失败而回滚事务
            }
        }

        // 2. 逻辑删除文档记录
        document.setStatus(0);
        documentMapper.updateById(document);

        // 3. 更新知识库统计
        KnowledgeBase kb = this.getById(document.getKbId());
        if (kb != null && kb.getDocCount() > 0) {
            kb.setDocCount(kb.getDocCount() - 1);
            this.updateById(kb);
        }

        log.info("文档删除完成");
    }

    /**
     * 批量删除文档
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteDocuments(List<String> documentIds) {
        if (documentIds == null || documentIds.isEmpty()) {
            return;
        }

        log.info("批量删除文档，数量: {}", documentIds.size());

        for (String documentId : documentIds) {
            try {
                deleteDocument(documentId);
            } catch (Exception e) {
                log.error("删除文档失败: {}", documentId, e);
                // 继续删除其他文档
            }
        }
    }

    /**
     * 重新生成向量（修复失败的嵌入）
     */
    @Transactional(rollbackFor = Exception.class)
    public void regenerateEmbedding(String documentId) {
        log.info("重新生成向量: {}", documentId);

        KnowledgeDocument document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new RuntimeException("文档不存在");
        }

        if (document.getEmbeddingStatus() == 1 && document.getVectorId() != null) {
            // 先删除旧的向量
            try {
                vectorStore.delete(Collections.singletonList(document.getVectorId()));
            } catch (Exception e) {
                log.warn("删除旧向量失败，继续生成新向量", e);
            }
        }

        // 生成新的向量ID
        String newVectorId = UUID.randomUUID().toString();
        document.setVectorId(newVectorId);
        document.setEmbeddingStatus(0);

        // 保存到向量数据库
        KnowledgeBase kb = this.getById(document.getKbId());
        if (kb != null) {
            try {
                saveToVectorStore(document, kb.getEmbeddingModel());
                document.setEmbeddingStatus(1);
                document.setProcessedAt(new Date());
                log.info("向量重新生成成功");
            } catch (Exception e) {
                log.error("向量重新生成失败", e);
                document.setEmbeddingStatus(2);
                throw new RuntimeException("向量重新生成失败: " + e.getMessage());
            }
        } else {
            document.setEmbeddingStatus(2);
            throw new RuntimeException("知识库不存在");
        }

        documentMapper.updateById(document);
    }

    // ========== 检索功能 ==========

    /**
     * 语义搜索
     */
    public SearchResultDto semanticSearch(SearchRequestDto request) {
        log.info("语义搜索: kbId={}, query={}", request.getKbId(), request.getQuery());

        SearchResultDto result = new SearchResultDto();
        result.setQuery(request.getQuery());
        result.setKbId(request.getKbId());

        try {
            // 构建搜索请求
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(request.getQuery())
                    .topK(request.getTopK() != null ? request.getTopK() : 5)
                    .similarityThreshold(request.getSimilarityThreshold() != null ?
                        request.getSimilarityThreshold().doubleValue() : 0.5)
                    .build();

            // 执行搜索
            List<Document> documents = vectorStore.similaritySearch(searchRequest);

            // 过滤只返回当前知识库的文档
            List<Document> filteredDocs = documents.stream()
                    .filter(doc -> {
                        Map<String, Object> metadata = doc.getMetadata();
                        return metadata != null &&
                               request.getKbId().equals(metadata.get("kb_id"));
                    })
                    .collect(Collectors.toList());

            // 转换为结果
            List<DocumentResultDto> documentResults = filteredDocs.stream()
                    .map(doc -> {
                        DocumentResultDto docResult = new DocumentResultDto();
                        docResult.setId(doc.getId());
                        docResult.setContent(doc.getText());

                        // 提取分数
                        if (doc.getMetadata().containsKey("distance")) {
                            Object distance = doc.getMetadata().get("distance");
                            if (distance instanceof Number) {
                                double score = 1.0 / (1.0 + ((Number) distance).doubleValue());
                                docResult.setScore(score);
                            }
                        }

                        docResult.setMetadata(doc.getMetadata());
                        return docResult;
                    })
                    .collect(Collectors.toList());

            result.setSuccess(true);
            result.setDocuments(documentResults);
            result.setTotal(documentResults.size());

        } catch (Exception e) {
            log.error("语义搜索失败", e);
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }

        return result;
    }

    /**
     * 分页查询文档
     */
    public PageResultDto<KnowledgeDocument> getDocumentsByKbId(String kbId, int page, int size, String keyword) {
        LambdaQueryWrapper<KnowledgeDocument> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeDocument::getKbId, kbId)
               .eq(KnowledgeDocument::getStatus, 1)
               .orderByDesc(KnowledgeDocument::getCreateTime);

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(KnowledgeDocument::getDocName, keyword)
                             .or()
                             .like(KnowledgeDocument::getContent, keyword));
        }

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<KnowledgeDocument> mpPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<KnowledgeDocument> resultPage =
                documentMapper.selectPage(mpPage, wrapper);

        PageResultDto<KnowledgeDocument> pageResult = new PageResultDto<>();
        pageResult.setCurrent(page);
        pageResult.setSize(size);
        pageResult.setTotal(resultPage.getTotal());
        pageResult.setRecords(resultPage.getRecords());

        return pageResult;
    }

    /**
     * 获取文档详情
     */
    public KnowledgeDocument getDocumentDetail(String documentId) {
        return documentMapper.selectById(documentId);
    }

    // ========== 辅助方法 ==========

    /**
     * 生成内容摘要
     */
    private String generateSummary(String content) {
        if (content == null || content.length() <= 200) {
            return content;
        }

        // 简单实现：取前200字符作为摘要
        return content.substring(0, 200) + "...";
    }

    /**
     * 获取知识库统计
     */
    public KnowledgeBaseStatsDto getKnowledgeBaseStats(String kbId) {
        KnowledgeBase kb = this.getById(kbId);
        if (kb == null) {
            throw new RuntimeException("知识库不存在");
        }

        KnowledgeBaseStatsDto stats = new KnowledgeBaseStatsDto();
        stats.setKbId(kbId);
        stats.setKbName(kb.getKbName());
        stats.setTotalDocuments(kb.getDocCount());
        stats.setStatus(kb.getStatus());
        stats.setIsPublic(kb.getIsPublic());
        stats.setCreatedTime(kb.getCreateTime());

        // 计算各种状态的文档数量
        LambdaQueryWrapper<KnowledgeDocument> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeDocument::getKbId, kbId);

        // 正常文档数
        wrapper.eq(KnowledgeDocument::getStatus, 1);
        long normalCount = documentMapper.selectCount(wrapper);
        stats.setNormalDocuments(normalCount);

        // 嵌入成功的文档数
        wrapper.eq(KnowledgeDocument::getEmbeddingStatus, 1);
        long embeddedCount = documentMapper.selectCount(wrapper);
        stats.setEmbeddedDocuments(embeddedCount);

        // 嵌入失败的文档数
        wrapper.eq(KnowledgeDocument::getEmbeddingStatus, 2);
        long failedCount = documentMapper.selectCount(wrapper);
        stats.setFailedDocuments(failedCount);

        // 计算总分块数 (这里暂时设置为0，实际需要根据向量数据库中的分块数计算)
        stats.setTotalChunks(0L);

        return stats;
    }

    /**
     * 同步所有文档到向量数据库（用于修复数据不一致）
     */
    @Transactional(rollbackFor = Exception.class)
    public SyncResultDto syncAllDocumentsToVectorStore(String kbId) {
        log.info("开始同步知识库 {} 的所有文档到向量数据库", kbId);

        SyncResultDto result = new SyncResultDto();
        result.setKbId(kbId);
        result.setStartTime(new Date());

        // 查询所有需要同步的文档
        LambdaQueryWrapper<KnowledgeDocument> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeDocument::getKbId, kbId)
               .eq(KnowledgeDocument::getStatus, 1);

        List<KnowledgeDocument> documents = documentMapper.selectList(wrapper);
        result.setTotalDocuments(documents.size());

        KnowledgeBase kb = this.getById(kbId);
        if (kb == null) {
            result.setSuccess(false);
            result.setErrorMessage("知识库不存在");
            return result;
        }

        int successCount = 0;
        int failCount = 0;

        for (KnowledgeDocument doc : documents) {
            try {
                // 如果已经有向量ID，先删除旧的
                if (doc.getVectorId() != null) {
                    try {
                        vectorStore.delete(Collections.singletonList(doc.getVectorId()));
                    } catch (Exception e) {
                        log.warn("删除旧向量失败: {}", doc.getVectorId(), e);
                    }
                }

                // 生成新的向量ID
                String newVectorId = UUID.randomUUID().toString();
                doc.setVectorId(newVectorId);

                // 保存到向量数据库
                saveToVectorStore(doc, kb.getEmbeddingModel());
                doc.setEmbeddingStatus(1);
                doc.setProcessedAt(new Date());
                documentMapper.updateById(doc);

                successCount++;
            } catch (Exception e) {
                log.error("同步文档失败: {}", doc.getId(), e);
                doc.setEmbeddingStatus(2);
                documentMapper.updateById(doc);
                failCount++;
            }
        }

        result.setSuccessCount(successCount);
        result.setFailCount(failCount);
        result.setEndTime(new Date());
        result.setSuccess(failCount == 0);

        log.info("同步完成，成功: {}, 失败: {}", successCount, failCount);
        return result;
    }

    /**
     * 分页查询知识库列表
     */
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<KnowledgeBase> getKnowledgeBasePage(int page, int size, String name, Integer status) {
        LambdaQueryWrapper<KnowledgeBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, KnowledgeBase::getStatus, status)
               .like(StringUtils.hasText(name), KnowledgeBase::getKbName, name)
               .orderByDesc(KnowledgeBase::getCreateTime);

        return this.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size), wrapper);
    }

    /**
     * 将KnowledgeBase实体转换为KnowledgeBaseListVO
     */
    public KnowledgeBaseListVO convertToVO(com.github.lisuiheng.astra.server.ai.model.entity.KnowledgeBase kb, KnowledgeBaseStatsDto stats) {
        KnowledgeBaseListVO vo = new KnowledgeBaseListVO();
        vo.setId(kb.getId())
          .setName(kb.getKbName())
          .setDescription(kb.getDescription())
          .setStatus(kb.getStatus())
          .setCreatedAt(kb.getCreateTime())
          .setUpdatedAt(kb.getUpdateTime());

        if (stats != null) {
            vo.setTotalDocuments((long) stats.getTotalDocuments())
              .setTotalChunks(stats.getTotalChunks());
        } else {
            vo.setTotalDocuments(0L)
              .setTotalChunks(0L);
        }

        return vo;
    }
}
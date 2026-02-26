package com.github.lisuiheng.astra.server.ai.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.lisuiheng.AstraServerApplication;
import com.github.lisuiheng.astra.server.ai.mapper.KnowledgeDocumentMapper;
import com.github.lisuiheng.astra.server.ai.model.dto.*;
import com.github.lisuiheng.astra.server.ai.model.entity.KnowledgeBase;
import com.github.lisuiheng.astra.server.ai.model.entity.KnowledgeDocument;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(classes = AstraServerApplication.class)
class KnowledgeServiceTest {
    @Autowired
    private KnowledgeService knowledgeService;
    @Autowired
    private KnowledgeDocumentMapper knowledgeDocumentMapper;


    @Test
    @DisplayName("测试创建知识库")
    void testCreateKnowledgeBase() {
        // 准备测试数据
        CreateKnowledgeBaseRequest request = new CreateKnowledgeBaseRequest()
                .setKbName("测试知识库")
                .setDescription("这是一个测试知识库")
                .setEmbeddingModel("text-embedding-v4")
                .setIsPublic(true);

        // 执行测试
        KnowledgeBase kb = knowledgeService.createKnowledgeBase(request);

        // 验证结果
        assertThat(kb).isNotNull();
        assertThat(kb.getId()).isNotEmpty();
        assertThat(kb.getKbName()).isEqualTo("测试知识库");
        assertThat(kb.getDescription()).isEqualTo("这是一个测试知识库");
        assertThat(kb.getEmbeddingModel()).isEqualTo("text-embedding-v4");
        assertThat(kb.getIsPublic()).isTrue();
        assertThat(kb.getStatus()).isEqualTo(1);
        assertThat(kb.getDocCount()).isEqualTo(0);

        // 保存测试ID供后续测试使用
        String testKbId = kb.getId();

        // 验证数据库
        KnowledgeBase savedKb = knowledgeService.getById(testKbId);
        assertThat(savedKb).isNotNull();
        assertThat(savedKb.getKbName()).isEqualTo("测试知识库");

        System.out.println("✅ 创建知识库测试通过，知识库ID: " + testKbId);
    }


    @Test
    @DisplayName("测试添加文档")
    void testAddDocument() {
        // 首先创建一个知识库
        CreateKnowledgeBaseRequest kbRequest = new CreateKnowledgeBaseRequest()
                .setKbName("文档测试知识库");
        KnowledgeBase kb = knowledgeService.createKnowledgeBase(kbRequest);
        String testKbId = kb.getId();

        // 准备文档数据
        AddDocumentRequest request = new AddDocumentRequest()
                .setKbId(testKbId)
                .setDocName("测试文档")
                .setFileName("test.txt")
                .setFileSize(1024L)
                .setFileType("text/plain")
                .setContent("这是测试文档的内容，包含一些重要信息，用于测试知识库功能。");


        // 执行添加文档
        KnowledgeDocument doc = knowledgeService.addDocument(request);

        // 验证结果
        assertThat(doc).isNotNull();
        assertThat(doc.getId()).isNotEmpty();
        assertThat(doc.getKbId()).isEqualTo(testKbId);
        assertThat(doc.getDocName()).isEqualTo("测试文档");
        assertThat(doc.getContent()).isEqualTo("这是测试文档的内容，包含一些重要信息，用于测试知识库功能。");
        assertThat(doc.getEmbeddingStatus()).isEqualTo(1); // 应该已嵌入
        assertThat(doc.getVectorId()).isNotEmpty();
        assertThat(doc.getContentSummary()).isNotEmpty();

        // 保存文档ID供后续测试使用
        String testDocumentId = doc.getId();

        // 验证知识库文档计数已更新
        KnowledgeBase updatedKb = knowledgeService.getById(testKbId);
        assertThat(updatedKb.getDocCount()).isEqualTo(1);


        System.out.println("✅ 添加文档测试通过，文档ID: " + testDocumentId);
    }

    @Test
    @DisplayName("测试语义搜索")
    void testSemanticSearch() {
        // 首先创建知识库并添加文档
        CreateKnowledgeBaseRequest kbRequest = new CreateKnowledgeBaseRequest()
                .setKbName("搜索测试知识库");
        KnowledgeBase kb = knowledgeService.createKnowledgeBase(kbRequest);
        String searchKbId = kb.getId();

        // 添加测试文档
        AddDocumentRequest docRequest = new AddDocumentRequest()
                .setKbId(searchKbId)
                .setDocName("搜索测试文档")
                .setContent("Spring AI 是一个强大的AI应用开发框架，支持多种AI模型和向量数据库。")
                .setFileType("text");



        KnowledgeDocument doc = knowledgeService.addDocument(docRequest);

        // 执行搜索
        SearchRequestDto searchRequest = new SearchRequestDto()
                .setKbId(searchKbId)
                .setQuery("Spring AI 框架")
                .setTopK(5)
                .setSimilarityThreshold(0.5);

        SearchResultDto result = knowledgeService.semanticSearch(searchRequest);

        // 验证搜索结果
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getTotal()).isGreaterThan(0);

        DocumentResultDto firstDoc = result.getDocuments().get(0);
        assertThat(firstDoc.getContent()).contains("Spring AI");
        assertThat(firstDoc.getScore()).isBetween(0.0, 1.0);

        System.out.println("✅ 语义搜索测试通过，找到 " + result.getTotal() + " 个相关文档");
    }

    @Test
    @DisplayName("测试删除知识库")
    void testDeleteKnowledgeBase() {
        // 创建知识库并添加文档
        CreateKnowledgeBaseRequest kbRequest = new CreateKnowledgeBaseRequest()
                .setKbName("待删除知识库");
        KnowledgeBase kb = knowledgeService.createKnowledgeBase(kbRequest);
        String testKbId = kb.getId();

        // 添加几个文档
        for (int i = 1; i <= 3; i++) {
            AddDocumentRequest docRequest = new AddDocumentRequest()
                    .setKbId(testKbId)
                    .setDocName("文档" + i)
                    .setContent("文档" + i + "的内容")
                    .setFileType("text");

            knowledgeService.addDocument(docRequest);
        }


        // 执行删除知识库
        knowledgeService.deleteKnowledgeBase(testKbId);

        // 验证知识库状态
        KnowledgeBase deletedKb = knowledgeService.getById(testKbId);
        assertThat(deletedKb.getStatus()).isEqualTo(0);

        LambdaQueryWrapper<KnowledgeDocument> queryWrapper = Wrappers.lambdaQuery(KnowledgeDocument.class).eq(KnowledgeDocument::getKbId, testKbId);

        List<KnowledgeDocument> documents = knowledgeDocumentMapper
                .selectList(queryWrapper);
        assertThat(documents.size()).isEqualTo(0);


        System.out.println("✅ 删除知识库测试通过");
    }

}
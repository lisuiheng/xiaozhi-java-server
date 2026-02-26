package com.github.lisuiheng.astra.server.ai.controller;

import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.server.ai.model.dto.*;
import com.github.lisuiheng.astra.server.ai.model.entity.KnowledgeBase;
import com.github.lisuiheng.astra.server.ai.model.entity.KnowledgeDocument;
import com.github.lisuiheng.astra.server.ai.service.KnowledgeService;
import com.github.lisuiheng.astra.common.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/api/v1/knowledge")
@Tag(name = "知识库管理")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    // ========== 知识库管理 ==========

    @PostMapping("/bases")
    @Operation(summary = "创建知识库")
    public R<KnowledgeBase> createKnowledgeBase(@RequestBody CreateKnowledgeBaseRequest request) {
        try {
            KnowledgeBase kb = knowledgeService.createKnowledgeBase(request);
            return R.ok("创建成功", kb);
        } catch (Exception e) {
            log.error("创建知识库失败", e);
            return R.fail("创建知识库失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/bases/{kbId}")
    @Operation(summary = "删除知识库")
    public R<Boolean> deleteKnowledgeBase(@PathVariable String kbId) {
        try {
            knowledgeService.deleteKnowledgeBase(kbId);
            return R.ok("删除成功", true);
        } catch (Exception e) {
            log.error("删除知识库失败", e);
            return R.fail("删除知识库失败: " + e.getMessage(), false);
        }
    }

    @GetMapping("/bases/{kbId}/stats")
    @Operation(summary = "获取知识库统计")
    public R<KnowledgeBaseStatsDto> getKnowledgeBaseStats(@PathVariable String kbId) {
        try {
            KnowledgeBaseStatsDto stats = knowledgeService.getKnowledgeBaseStats(kbId);
            return R.ok("查询成功", stats);
        } catch (Exception e) {
            log.error("获取知识库统计失败", e);
            return R.fail("获取知识库统计失败: " + e.getMessage());
        }
    }

    // ========== 知识库列表 ==========

    @GetMapping("/bases")
    @Operation(summary = "获取知识库列表")
    public TableDataInfo<KnowledgeBaseListVO> getKnowledgeBaseList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        try {
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.github.lisuiheng.astra.server.ai.model.entity.KnowledgeBase> result = 
                knowledgeService.getKnowledgeBasePage(page, size, name, status);
            
            // 为每个知识库添加统计信息
            java.util.List<com.github.lisuiheng.astra.server.ai.model.dto.KnowledgeBaseListVO> voList = 
                new java.util.ArrayList<>();
            for (com.github.lisuiheng.astra.server.ai.model.entity.KnowledgeBase kb : result.getRecords()) {
                try {
                    com.github.lisuiheng.astra.server.ai.model.dto.KnowledgeBaseStatsDto stats = 
                        knowledgeService.getKnowledgeBaseStats(kb.getId());
                    com.github.lisuiheng.astra.server.ai.model.dto.KnowledgeBaseListVO vo = 
                        knowledgeService.convertToVO(kb, stats);
                    voList.add(vo);
                } catch (Exception e) {
                    log.warn("转换知识库VO失败: {}", kb.getId(), e);
                    // 如果统计信息获取失败，仍然创建VO对象但不包含统计信息
                    com.github.lisuiheng.astra.server.ai.model.dto.KnowledgeBaseListVO vo = 
                        knowledgeService.convertToVO(kb, null);
                    voList.add(vo);
                }
            }
            
            // 构建分页结果
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<KnowledgeBaseListVO> pageResult = 
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
            pageResult.setCurrent(result.getCurrent())
                   .setSize(result.getSize())
                   .setTotal(result.getTotal())
                   .setRecords(voList);
            
            return TableDataInfo.build(pageResult);
        } catch (Exception e) {
            log.error("获取知识库列表失败", e);
            // 返回空的分页结果
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<KnowledgeBaseListVO> emptyPage = 
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
            emptyPage.setCurrent(page)
                   .setSize(size)
                   .setTotal(0)
                   .setRecords(new java.util.ArrayList<>());
            return TableDataInfo.build(emptyPage);
        }
    }

    // ========== 文档管理 ==========

    @PostMapping("/documents")
    @Operation(summary = "添加文档")
    public R<KnowledgeDocument> addDocument(@RequestBody AddDocumentRequest request) {
        try {
            KnowledgeDocument doc = knowledgeService.addDocument(request);
            return R.ok("添加成功", doc);
        } catch (Exception e) {
            log.error("添加文档失败", e);
            return R.fail("添加文档失败: " + e.getMessage());
        }
    }

    @PutMapping("/documents/{documentId}")
    @Operation(summary = "更新文档")
    public R<KnowledgeDocument> updateDocument(@PathVariable String documentId,
                                              @RequestBody UpdateDocumentRequest request) {
        try {
            KnowledgeDocument doc = knowledgeService.updateDocument(documentId, request);
            return R.ok("更新成功", doc);
        } catch (Exception e) {
            log.error("更新文档失败", e);
            return R.fail("更新文档失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/documents/{documentId}")
    @Operation(summary = "删除文档")
    public R<Boolean> deleteDocument(@PathVariable String documentId) {
        try {
            knowledgeService.deleteDocument(documentId);
            return R.ok("删除成功", true);
        } catch (Exception e) {
            log.error("删除文档失败", e);
            return R.fail("删除文档失败: " + e.getMessage(), false);
        }
    }

    @PostMapping("/documents/upload")
    @Operation(summary = "上传文件并添加文档")
    public R<KnowledgeDocument> uploadAndAddDocument(
            @RequestParam("kbId") String kbId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "docName", required = false) String docName) {
        try {
            // 提取文件内容
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);

            // 构建请求
            AddDocumentRequest request = new AddDocumentRequest()
                    .setKbId(kbId)
                    .setDocName(docName != null ? docName : file.getOriginalFilename())
                    .setFileName(file.getOriginalFilename())
                    .setFileSize(file.getSize())
                    .setFileType(getFileType(file.getOriginalFilename()))
                    .setContent(content);

            // 添加文档
            KnowledgeDocument doc = knowledgeService.addDocument(request);

            return R.ok("上传成功", doc);
        } catch (Exception e) {
            log.error("上传文件失败", e);
            return R.fail("上传文件失败: " + e.getMessage());
        }
    }

    @PostMapping("/documents/{documentId}/regenerate")
    @Operation(summary = "重新生成向量")
    public R<Boolean> regenerateEmbedding(@PathVariable String documentId) {
        try {
            knowledgeService.regenerateEmbedding(documentId);
            return R.ok("重新生成向量成功", true);
        } catch (Exception e) {
            log.error("重新生成向量失败", e);
            return R.fail("重新生成向量失败: " + e.getMessage(), false);
        }
    }

    @PostMapping("/bases/{kbId}/sync")
    @Operation(summary = "同步知识库到向量数据库")
    public R<SyncResultDto> syncKnowledgeBase(@PathVariable String kbId) {
        try {
            SyncResultDto result = knowledgeService.syncAllDocumentsToVectorStore(kbId);
            return result.isSuccess() ? R.ok("同步成功", result) : R.fail("同步失败: " + result.getErrorMessage(), result);
        } catch (Exception e) {
            log.error("同步知识库失败", e);
            return R.fail("同步知识库失败: " + e.getMessage());
        }
    }

    // ========== 检索功能 ==========

    @PostMapping("/search")
    @Operation(summary = "语义搜索")
    public R<SearchResultDto> semanticSearch(@RequestBody SearchRequestDto request) {
        try {
            SearchResultDto result = knowledgeService.semanticSearch(request);
            return result.isSuccess() ? R.ok("搜索成功", result) : R.fail("搜索失败: " + result.getErrorMessage(), result);
        } catch (Exception e) {
            log.error("搜索失败", e);
            return R.fail("搜索失败: " + e.getMessage());
        }
    }

    @GetMapping("/bases/{kbId}/documents")
    @Operation(summary = "获取文档列表")
    public TableDataInfo<KnowledgeDocument> getDocuments(
            @PathVariable String kbId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {
        try {
            PageResultDto<KnowledgeDocument> result = knowledgeService.getDocumentsByKbId(kbId, page, size, keyword);
            
            // 将PageResultDto转换为MyBatis-Plus的分页对象
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<KnowledgeDocument> pageResult = 
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
            pageResult.setCurrent(page)
                   .setSize(size)
                   .setTotal(result.getTotal())
                   .setRecords(result.getRecords());
            
            return TableDataInfo.build(pageResult);
        } catch (Exception e) {
            log.error("获取文档列表失败", e);
            // 返回空的分页结果
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<KnowledgeDocument> emptyPage = 
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
            emptyPage.setCurrent(page)
                   .setSize(size)
                   .setTotal(0)
                   .setRecords(new java.util.ArrayList<>());
            return TableDataInfo.build(emptyPage);
        }
    }

    @GetMapping("/documents/{documentId}")
    @Operation(summary = "获取文档详情")
    public R<KnowledgeDocument> getDocumentDetail(@PathVariable String documentId) {
        try {
            KnowledgeDocument doc = knowledgeService.getDocumentDetail(documentId);
            return R.ok("查询成功", doc);
        } catch (Exception e) {
            log.error("获取文档详情失败", e);
            return R.fail("获取文档详情失败: " + e.getMessage());
        }
    }

    // ========== 辅助方法 ==========

    private String getFileType(String fileName) {
        if (fileName == null) {
            return "unknown";
        }

        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(lastDot + 1).toLowerCase();
        }
        return "unknown";
    }
}
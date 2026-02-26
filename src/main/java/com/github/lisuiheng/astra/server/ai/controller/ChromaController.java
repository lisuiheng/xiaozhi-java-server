package com.github.lisuiheng.astra.server.ai.controller;

//import com.github.lisuiheng.astra.server.ai.model.dto.ChromaQueryRequest;
//import com.github.lisuiheng.astra.server.ai.model.dto.ChromaQueryResponse;
//import com.github.lisuiheng.astra.server.service.memory.ChromaService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/ai/chroma")
//@RequiredArgsConstructor
//@Tag(name = "Chroma 向量检索", description = "仅用于语义搜索，不涉及文档管理")
//public class ChromaController {
//
//    private final ChromaService chromaService;
//
//    /**
//     * 语义搜索（无过滤）
//     */
//    @Operation(summary = "语义搜索")
//    @PostMapping("/query")
//    public ChromaQueryResponse query(@RequestBody ChromaQueryRequest request) {
//        return chromaService.query(request.getQueryText(), request.getTopK());
//    }
//
//    /**
//     * 语义搜索（带 metadata 过滤）
//     */
//    @Operation(summary = "带过滤条件的语义搜索")
//    @PostMapping("/query/filtered")
//    public ChromaQueryResponse queryFiltered(@RequestBody ChromaQueryRequest request) {
//        return chromaService.queryFiltered(
//                request.getQueryText(),
//                request.getTopK(),
//                request.getWhere()
//        );
//    }
//}
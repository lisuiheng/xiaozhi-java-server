package com.github.lisuiheng.astra.server.ai.service;

//import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
//import com.github.lisuiheng.astra.server.ai.model.dto.ChromaQueryResponse;
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//import java.util.*;
//
//@Slf4j
//@Service
//public class ChromaService {
//
//    private WebClient webClient;
//    private final DashScopeEmbeddingModel embeddingModel;
//
//    @Value("${spring.ai.vectorstore.chroma.client.host:localhost}")
//    private String chromaHost;
//
//    @Value("${spring.ai.vectorstore.chroma.client.port:8000}")
//    private int chromaPort;
//
//    @Value("${spring.ai.vectorstore.chroma.collection-name:test_rag_docs}")
//    private String collectionName;
//
//    public ChromaService(DashScopeEmbeddingModel embeddingModel) {
//        this.embeddingModel = embeddingModel;
//    }
//
//    @PostConstruct
//    public void init() {
//        String baseUrl = chromaHost + ":" + chromaPort + "/api/v1";
//        this.webClient = WebClient.builder()
//                .baseUrl(baseUrl)
//                .build();
//        log.info("ChromaService initialized with base URL: {}", baseUrl);
//    }
//
//    // === CRUD: Upsert (Create or Update) ===
//
//    public void upsertDocument(String id, String content, Map<String, Object> metadata) {
//        float[] embedding = embeddingModel.embed(content);
//
//        Map<String, Object> payload = new HashMap<>();
//        payload.put("ids", Arrays.asList(id));
//        payload.put("embeddings", Arrays.asList(embedding));
//        payload.put("documents", Arrays.asList(content));
//        payload.put("metadatas", Arrays.asList(metadata != null ? metadata : Map.of()));
//
//        log.debug("Upserting document to Chroma: id={}, payload={}", id, payload);
//
//        webClient.post()
//                .uri("/collections/" + collectionName + "/upsert")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(payload)
//                .retrieve()
//                .onStatus(status -> !status.is2xxSuccessful(),
//                        clientResponse -> clientResponse.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException(
//                                        "Chroma upsert failed: " + clientResponse.statusCode() +
//                                                ", body: " + (errorBody != null ? errorBody : "null")))))
//                .toBodilessEntity()
//                .block();
//    }
//
//    public void upsertDocuments(List<DocumentInput> documents) {
//        if (documents == null || documents.isEmpty()) return;
//
//        List<String> ids = new ArrayList<>();
//        List<String> contents = new ArrayList<>();
//        List<Map<String, Object>> metadatas = new ArrayList<>();
//        List<float[]> embeddings = new ArrayList<>();
//
//        for (DocumentInput doc : documents) {
//            ids.add(doc.id());
//            contents.add(doc.content());
//            metadatas.add(doc.metadata() != null ? doc.metadata() : Map.of());
//            embeddings.add(embeddingModel.embed(doc.content()));
//        }
//
//        Map<String, Object> payload = new HashMap<>();
//        payload.put("ids", ids);
//        payload.put("embeddings", embeddings);
//        payload.put("documents", contents);
//        payload.put("metadatas", metadatas);
//
//        webClient.post()
//                .uri("/collections/" + collectionName + "/upsert")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(payload)
//                .retrieve()
//                .onStatus(status -> !status.is2xxSuccessful(),
//                        clientResponse -> clientResponse.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException(
//                                        "Chroma batch upsert failed: " + clientResponse.statusCode() +
//                                                ", body: " + (errorBody != null ? errorBody : "null")))))
//                .toBodilessEntity()
//                .block();
//    }
//
//    // === Delete ===
//
//    public void deleteDocument(String id) {
//        Map<String, Object> payload = Map.of("ids", Arrays.asList(id));
//
//        webClient.post()
//                .uri("/collections/" + collectionName + "/delete")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(payload)
//                .retrieve()
//                .onStatus(status -> !status.is2xxSuccessful(),
//                        clientResponse -> clientResponse.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException(
//                                        "Chroma delete failed: " + clientResponse.statusCode() +
//                                                ", body: " + (errorBody != null ? errorBody : "null")))))
//                .toBodilessEntity()
//                .block();
//    }
//
//    public void deleteDocuments(List<String> ids) {
//        if (ids == null || ids.isEmpty()) return;
//        Map<String, Object> payload = Map.of("ids", ids);
//
//        webClient.post()
//                .uri("/collections/" + collectionName + "/delete")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(payload)
//                .retrieve()
//                .onStatus(status -> !status.is2xxSuccessful(),
//                        clientResponse -> clientResponse.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException(
//                                        "Chroma batch delete failed: " + clientResponse.statusCode() +
//                                                ", body: " + (errorBody != null ? errorBody : "null")))))
//                .toBodilessEntity()
//                .block();
//    }
//
//    // === Query Methods ===
//
//    /**
//     * 语义搜索（无过滤）
//     */
//    public ChromaQueryResponse query(String queryText, int topK) {
//        QueryResult result = querySimilar(queryText, topK);
//        return new ChromaQueryResponse(result.documents(), result.metadatas(), result.distances());
//    }
//
//    /**
//     * 语义搜索（带 where 过滤）
//     */
//    public ChromaQueryResponse queryFiltered(String queryText, int topK, Map<String, Object> where) {
//        QueryResult result = querySimilar(queryText, topK, where);
//        return new ChromaQueryResponse(result.documents(), result.metadatas(), result.distances());
//    }
//
//    // Internal method used by both query methods
//    private QueryResult querySimilar(String queryText, int topK) {
//        return querySimilar(queryText, topK, null);
//    }
//
//    private QueryResult querySimilar(String queryText, int topK, Map<String, Object> where) {
//        float[] queryEmbedding = embeddingModel.embed(queryText);
//
//        Map<String, Object> payload = new HashMap<>();
//        payload.put("query_embeddings", Arrays.asList(queryEmbedding));
//        payload.put("n_results", topK);
//        if (where != null && !where.isEmpty()) {
//            payload.put("where", where);
//        }
//
//        log.debug("Querying Chroma with topK={}, where={}, payload={}", topK, where, payload);
//
//        Map<String, Object> response = webClient.post()
//                .uri("/collections/" + collectionName + "/query")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(payload)
//                .retrieve()
//                .onStatus(status -> !status.is2xxSuccessful(),
//                        clientResponse -> clientResponse.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException(
//                                        "Chroma query failed: " + clientResponse.statusCode() +
//                                                ", body: " + (errorBody != null ? errorBody : "null")))))
//                .bodyToMono(Map.class)
//                .block();
//
//        if (response == null) {
//            return new QueryResult(List.of(), List.of(), List.of());
//        }
//
//        @SuppressWarnings("unchecked")
//        List<List<String>> documentsList = (List<List<String>>) response.get("documents");
//        @SuppressWarnings("unchecked")
//        List<List<Map<String, Object>>> metadatasList = (List<List<Map<String, Object>>>) response.get("metadatas");
//        @SuppressWarnings("unchecked")
//        List<List<Double>> distancesList = (List<List<Double>>) response.get("distances");
//
//        List<String> docs = documentsList != null && !documentsList.isEmpty() ? documentsList.get(0) : List.of();
//        List<Map<String, Object>> meta = metadatasList != null && !metadatasList.isEmpty() ? metadatasList.get(0) : List.of();
//        List<Double> dists = distancesList != null && !distancesList.isEmpty() ? distancesList.get(0) : List.of();
//
//        return new QueryResult(docs, meta, dists);
//    }
//
//    // ===== Records =====
//
//    public record DocumentInput(String id, String content, Map<String, Object> metadata) {}
//
//    public record QueryResult(
//            List<String> documents,
//            List<Map<String, Object>> metadatas,
//            List<Double> distances
//    ) {}
//}
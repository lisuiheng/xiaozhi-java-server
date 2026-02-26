package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChromaQueryResponse {
    private List<String> documents;
    private List<Map<String, Object>> metadatas;
    private List<Double> distances;

}
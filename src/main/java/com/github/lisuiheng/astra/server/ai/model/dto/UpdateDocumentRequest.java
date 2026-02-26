package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateDocumentRequest {
    private String docName;
    private String content;
}
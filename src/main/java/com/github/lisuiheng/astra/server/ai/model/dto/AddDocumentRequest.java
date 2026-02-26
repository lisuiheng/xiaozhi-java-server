package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AddDocumentRequest {
    private String kbId;
    private String docName;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String content;
}
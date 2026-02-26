package com.github.lisuiheng.astra.server.server.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OTA结果响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OtaResultResponseDTO {
    private Boolean success;
    private String message;
    private Object data;
}
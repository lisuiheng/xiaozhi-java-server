package com.github.lisuiheng.astra.server.server.model.dto;

import lombok.Data;

/**
 * 函数调用DTO
 */
@Data
public class FunctionDTO {
    private String name;
    private String description;
    private Object parameters; 
}
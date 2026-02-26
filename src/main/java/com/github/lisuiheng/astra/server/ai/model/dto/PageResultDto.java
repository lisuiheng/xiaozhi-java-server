package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class PageResultDto<T> {
    private long current;
    private long size;
    private long total;
    private List<T> records;
}
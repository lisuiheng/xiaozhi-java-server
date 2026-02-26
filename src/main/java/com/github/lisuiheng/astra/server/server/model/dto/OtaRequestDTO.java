package com.github.lisuiheng.astra.server.server.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OTA请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtaRequestDTO {

    @JsonProperty("mac_address")
    private String macAddress;

    @JsonProperty("application")
    private ApplicationInfo application;

    @JsonProperty("board")
    private BoardInfo board;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplicationInfo {
        private String version;
        @JsonProperty("compile_time")
        private String compileTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardInfo {
        private String type;
    }
}
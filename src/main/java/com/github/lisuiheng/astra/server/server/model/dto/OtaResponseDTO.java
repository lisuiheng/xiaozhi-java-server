package com.github.lisuiheng.astra.server.server.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * OTA响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OtaResponseDTO {

    @JsonProperty("mqtt")
    private MqttConfig mqtt;

    @JsonProperty("server_time")
    private ServerTime serverTime;

    @JsonProperty("firmware")
    private FirmwareInfo firmware;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MqttConfig {
        private String endpoint;
        @JsonProperty("client_id")
        private String clientId;
        private String username;
        private String password;
        @JsonProperty("publish_topic")
        private String publishTopic;
        @JsonProperty("subscribe_topic")
        private String subscribeTopic;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ServerTime {
        private Long timestamp;
        private String timezone;
        @JsonProperty("timezone_offset")
        private Integer timezoneOffset;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FirmwareInfo {
        private String version;
        private String url;
        @JsonProperty("ota_info_id")
        private String otaInfoId;
        @JsonProperty("ota_md5_checksum")
        private String otaMd5Checksum;
    }
}
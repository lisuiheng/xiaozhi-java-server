package com.github.lisuiheng.astra.server.asr.model.dto.inbound;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class IotInbound extends InboundMessage {
    private final String type = "iot";
    private String sessionId;
    private boolean update;
    private List<Descriptor> descriptors;
    
    @Data
    public static class Descriptor {
        private String name;
        private String description;
        private Map<String, Property> properties;
        private Map<String, Method> methods;
    }
    
    @Data
    public static class Property {
        private String description;
        private String type;
    }
    
    @Data
    public static class Method {
        private String description;
        private Map<String, Parameter> parameters;
    }
    
    @Data
    public static class Parameter {
        private String description;
        private String type;
    }
}
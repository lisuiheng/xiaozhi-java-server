package com.github.lisuiheng.astra.server.server.model.entity;

import com.github.lisuiheng.astra.server.server.enums.SessionStatus;
import lombok.Data;

@Data
public class SessionState {
    private String sessionId;
    private String deviceId;
    private SessionStatus status;
}

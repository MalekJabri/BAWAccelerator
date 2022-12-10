package org.mj.process.model;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Set;

@Data
@Component
@Scope("session")
public class ConnectionRequest {

    FileNetServerRequest fileNetServerRequest;
    ProcessMiningServerRequest processMiningServerRequest;
    Set<String> caseTypes;

    public ConnectionRequest() {
        fileNetServerRequest = new FileNetServerRequest();
        processMiningServerRequest = new ProcessMiningServerRequest();
    }

}

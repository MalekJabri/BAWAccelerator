package org.mj.process.model.servers;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Set;

@Data
@Component
@Scope("session")
public class ConnectionRequest {

    private BAWContentServer bawContentServer;
    private ProcessMiningServer processMiningServer;
    private Set<String> caseTypes;

    public ConnectionRequest() {
        bawContentServer = new BAWContentServer();
        processMiningServer = new ProcessMiningServer();
    }

}

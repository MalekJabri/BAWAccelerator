package org.mj.process.model.servers;

import lombok.Data;
import org.mj.process.model.servers.BAWContentServer;
import org.mj.process.model.servers.ProcessMiningServer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Set;

@Data
@Component
@Scope("session")
public class ConnectionRequest {

    org.mj.process.model.servers.BAWContentServer BAWContentServer;
    ProcessMiningServer processMiningServer;
    Set<String> caseTypes;

    public ConnectionRequest() {
        BAWContentServer = new BAWContentServer();
        processMiningServer = new ProcessMiningServer();
    }

}

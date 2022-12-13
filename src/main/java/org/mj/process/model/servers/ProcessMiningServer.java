package org.mj.process.model.servers;

import lombok.Data;


@Data
public class ProcessMiningServer {
    String url;
    String apikey;
    String userID;
    private boolean save;
}

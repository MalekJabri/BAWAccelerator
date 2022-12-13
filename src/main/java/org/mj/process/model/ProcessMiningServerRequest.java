package org.mj.process.model;

import lombok.Data;


@Data
public class ProcessMiningServerRequest {
    String url;
    String apikey;
    String userID;
    private boolean save;
}

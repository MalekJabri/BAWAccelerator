package org.mj.process.model;

import lombok.Data;

@Data
public class ConfigurationRequest {
    String caseType;
    String solution;
    String level;
    String[] properties;
    boolean saveInProcessMining;

}

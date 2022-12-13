package org.mj.process.model;

import lombok.Data;

@Data
public class ConfigPMProject {
    String projectName;
    String orgID;
    String orgName;
    Boolean uploadData;
    Boolean append;
}

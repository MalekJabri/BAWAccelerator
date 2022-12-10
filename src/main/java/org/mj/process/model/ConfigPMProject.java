package org.mj.process.model;

import lombok.Data;

@Data
public class ConfigPMProject {
    String projectName;
    String OrgID;
    String OrgName;
    Boolean uploadData;
    Boolean append;
}

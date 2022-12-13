package org.mj.process.model;

import lombok.Data;


@Data
public class DocumentRequest {
    String filePath;
    boolean cleanDate;
    String delimiter;
    boolean cleanIDAttribute;
    String dateFormat;
    String targetDateFormat;
    boolean addInformation;
    String encodedFormat;
    String eventLevel;
    String caseType;
}

package org.mj.process.model;

import lombok.Data;


@Data
public class DocumentRequest {

    String filePath;
    boolean cleanDate;
    String delimiter;
    boolean cleanIDAttribute;
    String dateFormat;
    boolean addInformation;
    boolean encoded64Bit;
    String eventLevel;
    String caseType;
}

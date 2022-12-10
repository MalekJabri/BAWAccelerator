package org.mj.process.model;

import lombok.Data;


@Data
public class DocumentRequest {

    String filePath;
    boolean cleanDate;
    String delimiter;
    boolean cleanIDAttribute;
    String DateFormat;
    boolean addInformation;
}

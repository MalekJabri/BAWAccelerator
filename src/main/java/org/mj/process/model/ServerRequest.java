package org.mj.process.model;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("session")
@Data
public class ServerRequest {

    String filePath;
    boolean cleanDate;
    String delimiter;
    boolean cleanIDAttribute;
    String DateFormat;
    boolean addInformation;
}

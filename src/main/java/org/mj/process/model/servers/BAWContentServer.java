package org.mj.process.model.servers;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(name = "ServerECM", description = "the server where the document will be saved")
public class BAWContentServer implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "server", required = true, example = "ibmbaw")
    private String server;
    @Schema(name = "port", required = false, example = "9080")
    private String port;
    @Schema(name = "user", required = true, example = "dadmin")
    private String user;
    @Schema(name = "password", required = true, example = "dadmin")
    private String password;
    @Schema(name = "repository", required = true, example = "tos")
    private String repository;
    private boolean save;

}
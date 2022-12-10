package org.mj.process.restController;

import com.ibm.casemgmt.api.CaseType;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mj.process.model.Attribute;
import org.mj.process.model.ConnectionRequest;
import org.mj.process.service.CaseTypeService;
import org.mj.process.service.ServerConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping(path = "/api/core")
@Tag(name = "CaseAPI", description = "the CaseController API")
public class CaseController {
    private static Logger logger = Logger.getLogger(CaseController.class.getName());

    public static List<Attribute> extracted(String caseTypeID, ConnectionRequest connectionRequest) {
        List<Attribute> attributes = new ArrayList<>();
        if (connectionRequest == null) {
            logger.warning("The connection request is empty");
        } else {
            logger.info("The connection request has been found");
            ServerConfig serverConfig = new ServerConfig(connectionRequest.getFileNetServerRequest());
            CaseTypeService caseTypeService = new CaseTypeService(serverConfig);
            CaseType caseType = caseTypeService.getCaseType(caseTypeID);
            HashMap<String, String> values = caseTypeService.GetPropertiesForCaseType(caseType, true);
            values.forEach((key, value) -> {
                attributes.add(new Attribute(key, value));
            });
            logger.info("list attributes for the case type");
        }
        return attributes;
    }

    @GetMapping(path = "/caseTypes")
    public List<Attribute> listCaseTypes(HttpSession session, @RequestParam(name = "solution", required = true) String solution) {
        logger.info("The list of cases for the solution :" + solution);
        List<Attribute> caseTypes = new ArrayList<>();
        ConnectionRequest connectionRequest = (ConnectionRequest) session.getAttribute("connectionRequest");
        if (connectionRequest == null) {
            logger.warning("The connection request is empty");
        } else {
            logger.info("The connection request has been found");
            ServerConfig serverConfig = new ServerConfig(connectionRequest.getFileNetServerRequest());
            CaseTypeService caseTypeService = new CaseTypeService(serverConfig);
            caseTypes = caseTypeService.getAttributesCaseType(solution);
            logger.info("list attributes for the case type");
        }
        return caseTypes;
    }


    @GetMapping(path = "/caseTypesID")
    public List<Attribute> listCaseTypesID(HttpSession session, @RequestParam(name = "solution", required = true) String solution) {
        logger.info("The list of cases for the solution :" + solution);
        List<Attribute> caseTypes = new ArrayList<>();
        ConnectionRequest connectionRequest = (ConnectionRequest) session.getAttribute("connectionRequest");
        if (connectionRequest == null) {
            logger.warning("The connection request is empty");
        } else {
            logger.info("The connection request has been found");
            ServerConfig serverConfig = new ServerConfig(connectionRequest.getFileNetServerRequest());
            CaseTypeService caseTypeService = new CaseTypeService(serverConfig);
            caseTypes = caseTypeService.getAttributesIDCaseType(solution);
            logger.info("list attributes for the case type");
        }
        return caseTypes;
    }

    @GetMapping(path = "/properties")
    public List<Attribute> propertiesForCaseType(HttpSession session, @RequestParam(name = "caseType", required = true) String caseTypeID) {
        logger.info("The list of cases for the solution :" + caseTypeID);
        ConnectionRequest connectionRequest = (ConnectionRequest) session.getAttribute("connectionRequest");
        List<Attribute> attributes = extracted(caseTypeID, connectionRequest);
        return attributes;
    }


}

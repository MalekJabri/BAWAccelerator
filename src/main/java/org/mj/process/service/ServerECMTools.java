package org.mj.process.service;

import com.ibm.casemgmt.api.Case;
import com.ibm.casemgmt.api.DeployedSolution;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class ServerECMTools {

    private static Logger logger = Logger.getLogger(ServerECMTools.class.getName());

    public static void main(String[] args) throws Exception {
        String caseTypeID = "E0BAC726-36D9-4BAB-A0AF-35478A5E9F93";
        String caseId = "5041486E-0000-C214-904B-E19CE3B60182";
        ServerConfig serverConfig = new ServerConfig("TOS", 3);
        CaseTypeService caseTypeService = new CaseTypeService(serverConfig);
        List<DeployedSolution> solutions = caseTypeService.getSolutions();
        Case caseInstance = caseTypeService.getCase(caseId, null);
        HashMap<String, String> properties = caseTypeService.getProperties(caseInstance, "dd-MM-yyyy HH:mm", true);
        logger.info("the number of properties retrieved for the case are " + properties.size());
    }

}

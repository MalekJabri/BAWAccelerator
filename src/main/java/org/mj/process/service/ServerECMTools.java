package org.mj.process.service;

import java.util.logging.Logger;

public class ServerECMTools {

    private static Logger logger = Logger.getLogger(ServerECMTools.class.getName());

    public static void main(String[] args) throws Exception {
        String caseTypeID = "{12951D96-1A9A-4D74-9A68-49A2413F6E06}";
        String caseId = "5041486E-0000-C214-904B-E19CE3B60182";
        ServerConfig serverConfig = new ServerConfig("TOS", 3);
        CaseTypeService caseTypeService = new CaseTypeService(serverConfig);
        System.out.println(caseTypeService.getCaseType(caseTypeID).getDisplayName());
        /*
        List<DeployedSolution> solutions = caseTypeService.getSolutions();
        Case caseInstance = caseTypeService.getCase(caseId, null);
        HashMap<String, String> properties = caseTypeService.getProperties(caseInstance, "dd-MM-yyyy HH:mm", true);
        logger.info("the number of properties retrieved for the case are " + properties.size());
        */
    }

}

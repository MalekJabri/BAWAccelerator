package org.mj.process.service;

import com.ibm.casemgmt.api.Case;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class ServerECMTools {

    private static Logger logger = Logger.getLogger(ServerECMTools.class.getName());

    public static void main(String[] args) throws Exception {
        String caseTypeID = "{12951D96-1A9A-4D74-9A68-49A2413F6E06}";
        String caseId = "{F07B0B7B-0000-C296-AA5C-DEF5E401AFAB}";
        ServerConfig serverConfig = new ServerConfig("TOS", 3);
        CaseTypeService caseTypeService = new CaseTypeService(serverConfig);
        System.out.println(caseTypeService.getCaseType(caseTypeID).getDisplayName());

        List<String> properties = new ArrayList<>();
        properties.add("PQ_Type");
        properties.add("PQ_Nationality");
        properties.add("PQ_Departement");
        properties.add("PQ_Sender");
        properties.add("PQ_CountWaitResponse");
        Case caseInstance = caseTypeService.getCase(caseId, properties);
        HashMap<String, String> props = caseTypeService.getProperties(caseInstance, "dd-MM-yyyy HH:mm", true);
        System.out.println(props);

        /*
        List<DeployedSolution> solutions = caseTypeService.getSolutions();
        Case caseInstance = caseTypeService.getCase(caseId, null);
        HashMap<String, String> properties = caseTypeService.getProperties(caseInstance, "dd-MM-yyyy HH:mm", true);
        logger.info("the number of properties retrieved for the case are " + properties.size());
        */
    }

}

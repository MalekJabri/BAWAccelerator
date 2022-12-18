package org.mj.process.test;

import com.filenet.api.core.IndependentObject;
import com.google.gson.Gson;
import com.ibm.casemgmt.api.Case;
import org.mj.process.model.CaseEvent;
import org.mj.process.model.CaseHistory;
import org.mj.process.model.MappingAttributePM;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class Sandbox {

    private static Logger logger = Logger.getLogger(Sandbox.class.getName());

    public static void main(String[] args) throws Exception {
      /*  String caseTypeID = "{12951D96-1A9A-4D74-9A68-49A2413F6E06}";
        String caseId = "{B0B4C07B-0000-C29D-A5FB-0F89157E5952}";
        ServerConfig serverConfig = new ServerConfig("TOS", 3);
        CaseTypeService caseTypeService = new CaseTypeService(serverConfig);
        System.out.println(caseTypeService.getCaseType(caseTypeID).getDisplayName());
        Case caseInstance = caseTypeService.getCase(caseId, null);
        Sandbox sandbox = new Sandbox();
        sandbox.getHistory(caseInstance);*/
        String[] headers = {CaseEvent.EVENT_REF_ID, CaseEvent.CASE_ID, CaseEvent.END_TIME};

        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        String testDate = "2012-09-15 21:20:15";
        SimpleDateFormat orignalFormat = new SimpleDateFormat(dateFormat);
        Date eventDate = orignalFormat.parse(testDate);
        System.out.println("event date " + eventDate);
        System.out.println("converted  date " + orignalFormat.format(eventDate));
        //  System.out.println(getMappingInfo(headers, false));
    }

    public static String getMappingInfo(String[] headers, boolean augmented) {
        int count = 1;
        HashMap<String, MappingAttributePM> listAttributes = new HashMap<>();
        for (String property : headers) {
            if (property.equals(CaseEvent.EVENT_REF_ID)) {
                MappingAttributePM attributePM = new MappingAttributePM();
                attributePM.setId(property);
                attributePM.setMask("");
                attributePM.setName("PROCESS_ID");
                listAttributes.put("" + count, attributePM);
            }
            count++;
        }
        String result = new Gson().toJson(listAttributes);
        return result;
    }


    HashMap<String, CaseHistory> getHistory(Case caseInstance) {
        logger.info("caseInstance :" + caseInstance.getFolderReference().fetchCEObject().get_FolderName() + " ");
        HashMap<String, CaseHistory> listEvents = new HashMap<>();
        Case.FetchHistoryResult result = caseInstance.fetchHistory();
        List<IndependentObject> events = result.getResults();
        for (IndependentObject event : events) {
            System.out.println("information " + event.getClassName());
        }
        return listEvents;
    }
}

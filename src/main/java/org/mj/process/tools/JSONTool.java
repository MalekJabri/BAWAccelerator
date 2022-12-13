package org.mj.process.tools;

import com.google.gson.Gson;
import org.mj.process.model.CaseEvent;
import org.mj.process.model.MappingAttributePM;

import java.util.HashMap;

public class JSONTool {

    public static String getMappingInfo(String[] headers, boolean augmented, String dateFormat) {
        int count = 1;
        HashMap<String, MappingAttributePM> listAttributes = new HashMap<>();
        for (String property : headers) {
            MappingAttributePM attributePM = new MappingAttributePM();
            attributePM.setId(property);
            if (property.equals(CaseEvent.USER_NAME)) {
                attributePM.setMask("");
                attributePM.setName("RESOURCE_CODE");
                listAttributes.put("" + count, attributePM);
            }
            if (property.equals(CaseEvent.START_TIME)) {
                attributePM.setMask(dateFormat);
                attributePM.setName("START_TIME");
                listAttributes.put("" + count, attributePM);
            }
            if (property.equals(CaseEvent.END_TIME)) {
                attributePM.setMask(dateFormat);
                attributePM.setName("END_TIME");
                listAttributes.put("" + count, attributePM);
            }
            if (property.equals(CaseEvent.CASE_ID) && !augmented) {
                attributePM.setMask("");
                attributePM.setName("PROCESS_ID");
                listAttributes.put("" + count, attributePM);
            }
            if (property.equals(CaseEvent.CASE_NAME) && augmented) {
                attributePM.setMask("");
                attributePM.setName("PROCESS_ID");
                listAttributes.put("" + count, attributePM);
            }
            if (property.equals(CaseEvent.ACTIVITY)) {
                attributePM.setMask("");
                attributePM.setName("ACTIVITY_CODE");
                listAttributes.put("" + count, attributePM);
            }
            if (property.equals(CaseEvent.ROLE)) {
                attributePM.setMask("");
                attributePM.setName("ROLE_CODE");
                listAttributes.put("" + count, attributePM);
            }
            count++;
        }
        String result = new Gson().toJson(listAttributes);
        return result;
    }
}

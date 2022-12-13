package org.mj.process.model;

import lombok.Data;
import org.apache.commons.csv.CSVRecord;

import java.util.HashMap;

@Data
public class DataMining {
    boolean lowerCase;
    boolean quote;
    private HashMap<String, String> defaultAttributes;
    private String[] headers;
    private Iterable<CSVRecord> records;

    public DataMining(boolean lCase, boolean quoteField) {
        defaultAttributes = new HashMap<>();
        lowerCase = lCase;
        quote = quoteField;
        defaultAttributes.put(CaseEvent.START_TIME, getHeader(CaseEvent.START_TIME));
        defaultAttributes.put(CaseEvent.END_TIME, getHeader(CaseEvent.END_TIME));
        defaultAttributes.put(CaseEvent.CASE_TYPE, getHeader(CaseEvent.CASE_TYPE));
        defaultAttributes.put(CaseEvent.CASE_ID, getHeader(CaseEvent.CASE_ID));
        defaultAttributes.put(CaseEvent.ACTIVITY, getHeader(CaseEvent.ACTIVITY));
        defaultAttributes.put(CaseEvent.EVENT_ENTITY_ID, getHeader(CaseEvent.EVENT_ENTITY_ID));
        defaultAttributes.put(CaseEvent.EVENT_STATUS, getHeader(CaseEvent.EVENT_STATUS));
        defaultAttributes.put(CaseEvent.EVENT_REF_ID, getHeader(CaseEvent.EVENT_REF_ID));
        defaultAttributes.put(CaseEvent.EVENT_TYPE, getHeader(CaseEvent.EVENT_TYPE));
        defaultAttributes.put(CaseEvent.USER_NAME, getHeader(CaseEvent.USER_NAME));
    }

    public String getHeader(String text) {
        String result = text;
        if (!lowerCase) result = result.toUpperCase();
        if (quote) result = "\"" + result + "\"";
        return result;
    }
}

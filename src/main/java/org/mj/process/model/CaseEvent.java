package org.mj.process.model;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@ToString
public class CaseEvent {

    public static final String CASE_ID = "ch_case_id";
    public static final String EVENT_REF_ID = "ch_reference_id";
    public static final String EVENT_TYPE = "ch_type";
    public static final String CASE_TYPE = "ch_case_type";
    public static final String EVENT_STATUS = "ch_status";
    public static final String USER_NAME = "ch_user_name";
    public static final String START_TIME = "ch_start_time";
    public static final String END_TIME = "ch_end_time";
    public static final String ACTIVITY = "ch_name";

    HashMap<String, String> additionalAttribute;

    public CaseEvent(CSVRecord record, boolean lowerCase) {

        additionalAttribute = new HashMap<>();
        if (lowerCase) {
            additionalAttribute.put(CASE_ID, record.get(CASE_ID));
            additionalAttribute.put(EVENT_REF_ID, record.get(EVENT_REF_ID));
            additionalAttribute.put(EVENT_TYPE, record.get(EVENT_TYPE));
            additionalAttribute.put(EVENT_STATUS, record.get(EVENT_STATUS));
            additionalAttribute.put(USER_NAME, record.get(USER_NAME));
            additionalAttribute.put(START_TIME, cleanDate(record.get(START_TIME)));
            additionalAttribute.put(END_TIME, cleanDate(record.get(END_TIME)));
            additionalAttribute.put(ACTIVITY, record.get(ACTIVITY));
            additionalAttribute.put(CASE_TYPE, record.get(CASE_TYPE));
        } else {
            additionalAttribute.put(CASE_ID, record.get(CASE_ID.toUpperCase()));
            additionalAttribute.put(EVENT_REF_ID, record.get(EVENT_REF_ID.toUpperCase()));
            additionalAttribute.put(EVENT_TYPE, record.get(EVENT_TYPE.toUpperCase()));
            additionalAttribute.put(EVENT_STATUS, record.get(EVENT_STATUS.toUpperCase()));
            additionalAttribute.put(USER_NAME, record.get(USER_NAME.toUpperCase()));
            additionalAttribute.put(START_TIME, cleanDate(record.get(START_TIME.toUpperCase())));
            additionalAttribute.put(END_TIME, cleanDate(record.get(END_TIME.toUpperCase())));
            additionalAttribute.put(ACTIVITY, record.get(ACTIVITY.toUpperCase()));
            additionalAttribute.put(CASE_TYPE, record.get(CASE_TYPE.toUpperCase()));
        }

    }


    public List<String> getValues() {
        List<String> values = new ArrayList<>();
        additionalAttribute.forEach((key, value) -> {
            values.add(value);
        });
        return values;
    }

    public List<String> getHeaders() {
        List<String> headers = new ArrayList<>();
        if (additionalAttribute != null && additionalAttribute.size() > 0)
            additionalAttribute.forEach((key, value) -> {
                headers.add(key);
            });
        return headers;
    }


    private String cleanDate(String date) {
        return date.substring(0, date.indexOf(","));
    }

    public String getStatus() {
        return additionalAttribute.get(EVENT_STATUS);
    }

    public void setStatus(String completion) {
        additionalAttribute.replace(EVENT_STATUS, completion);
    }

    public String getReferenceID() {
        return additionalAttribute.get(EVENT_REF_ID);
    }

    public String getEventType() {
        return additionalAttribute.get(EVENT_TYPE);
    }

    public String getStart_time() {
        return additionalAttribute.get(START_TIME);
    }

    public void setStart_time(String start_time) {
        additionalAttribute.replace(EVENT_STATUS, start_time);
    }

    public String getEnd_time() {
        return additionalAttribute.get(END_TIME);
    }

    public void setEnd_time(String end_time) {
        additionalAttribute.replace(END_TIME, end_time);
    }

    public String getCaseType() {
        return additionalAttribute.get(CASE_TYPE);
    }

}

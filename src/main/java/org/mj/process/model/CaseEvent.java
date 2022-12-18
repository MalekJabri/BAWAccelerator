package org.mj.process.model;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.csv.CSVRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Data
@ToString
public class CaseEvent {

    public static final String CASE_ID = "ch_case_id";

    public static final String CONTENT = "ch_content";
    public static final String EVENT_REF_ID = "ch_reference_id";
    public static final String EVENT_ENTITY_ID = "ch_entity_id";
    public static final String EVENT_TYPE = "ch_type";
    public static final String CASE_TYPE = "ch_case_type";
    public static final String EVENT_STATUS = "ch_status";
    public static final String USER_NAME = "ch_user_name";
    public static final String START_TIME = "ch_start_time";
    public static final String END_TIME = "ch_end_time";
    public static final String ACTIVITY = "ch_name";
    public static final String ROLE = "QueueName";
    public static final String CASE_NAME = "CmAcmCaseIdentifier";

    private HashMap<String, String> additionalAttribute;

    public CaseEvent(CSVRecord record, HashMap<String, String> defaultAttributes, String dateFormat, String targetDateFormat, boolean cleanID) {
        additionalAttribute = new HashMap<>();
        defaultAttributes.forEach((key, storedKey) -> {

            if (key.toLowerCase().contains("time")) {
                String date = cleanDate(record.get(storedKey), dateFormat, targetDateFormat);
                additionalAttribute.put(key, date);
            } else if ((key.toLowerCase().contains("_id")) && cleanID) {
                String value = record.get(storedKey);
                value = value.replace("{", "");
                value = value.replace("}", "");
                additionalAttribute.put(key, value);

            } else {
                additionalAttribute.put(key, record.get(storedKey));
            }
        });
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


    private String cleanDate(String date, String dateFormat, String targetDateFormat) {
        String wrongDate = "31/12/9999 23:59:59";
        String defaultFormat = "dd/MM/yyyy hh:mm:ss";
        String result = date;
        if (result.contains("9999")) return "";
        Date eventDate;
        SimpleDateFormat orignalFormat = new SimpleDateFormat(dateFormat);
        try {
            eventDate = orignalFormat.parse(result);
            result = orignalFormat.format(eventDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (targetDateFormat != null && !targetDateFormat.isEmpty()) {
            SimpleDateFormat targetFormat = new SimpleDateFormat(targetDateFormat);
            try {
                eventDate = orignalFormat.parse(result);
                result = targetFormat.format(eventDate);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    boolean checkObsDate(Date eventDate) throws ParseException {
        String wrongDate = "31/12/9999 23:59:59";
        String defaultFormat = "dd/MM/yyyy hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(defaultFormat);
        Date obsoloteDate = simpleDateFormat.parse(wrongDate);
        return obsoloteDate.equals(eventDate);
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

    public String getEventEntityId() {
        return additionalAttribute.get(EVENT_ENTITY_ID);
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

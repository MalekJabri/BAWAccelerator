package org.mj.process.model;

import com.ibm.casemgmt.api.Case;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.csv.CSVRecord;
import org.mj.process.model.generic.Attribute;
import org.mj.process.service.CaseTypeService;
import org.mj.process.tools.XmlParserTool;

import java.util.*;
import java.util.logging.Logger;

@Data
@ToString
public class CaseHistory {

    private static Logger logger = Logger.getLogger(CaseHistory.class.getName());
    private String[] head;

    private HashMap<String, CaseEvent> events;
    private HashMap<String, Integer> caseTypes;

    public CaseHistory(String[] headers) {
        head = headers;
    }

    public void init(DataMining dataMining, DocumentRequest config) {
        caseTypes = new HashMap<>();
        for (CSVRecord record : dataMining.getRecords()) {
            CaseEvent newCaseEvent = new CaseEvent(record, dataMining.getDefaultAttributes(), config.getDateFormat(), config.getTargetDateFormat());
            if (newCaseEvent.getEventType().equals(config.getEventLevel())) {
                if (caseTypes.containsKey(newCaseEvent.getCaseType())) {
                    caseTypes.replace(newCaseEvent.getCaseType(), (caseTypes.get(newCaseEvent.getCaseType())) + 1);
                } else {
                    caseTypes.put(newCaseEvent.getCaseType(), 1);
                }
            }
        }
    }

    public void processData(DataMining dataMining, DocumentRequest config) {
        String dateFormat = config.getDateFormat();
        if (config.getTargetDateFormat() != null && !config.getTargetDateFormat().isEmpty())
            dateFormat = config.getTargetDateFormat();
        events = new HashMap<>();
        CaseEvent newCaseEvent = null;
        logger.info("The number of element in the csv");
        System.out.println("Has next " + dataMining.getRecords().iterator().hasNext());
        for (CSVRecord record : dataMining.getRecords()) {
            newCaseEvent = new CaseEvent(record, dataMining.getDefaultAttributes(), config.getDateFormat(), config.getTargetDateFormat());
            if (newCaseEvent.getCaseType().equals(config.getCaseType()) && newCaseEvent.getEventType().equals(config.getEventLevel())) {
                if (events.containsKey(newCaseEvent.getEventEntityId())) {
                    CaseEvent oldCase = events.get(newCaseEvent.getEventEntityId());
                    if (newCaseEvent.getStatus().equals("INITIATED"))
                        oldCase.setStart_time(newCaseEvent.getStart_time());
                    if (newCaseEvent.getStatus().equals("COMPLETION")) {
                        oldCase.setStatus("COMPLETION");
                        oldCase.setEnd_time(newCaseEvent.getEnd_time());
                    }
                    if (record.get(dataMining.getHeader(CaseEvent.CONTENT)) != null) {
                        oldCase.getAdditionalAttribute().putAll(XmlParserTool.getPropertiesFromXML(record.get(dataMining.getHeader(CaseEvent.CONTENT)), config.getEncodedFormat(), dateFormat));
                    }
                    if (newCaseEvent.getStatus().equals("START_WORKING")) {

                        oldCase.getAdditionalAttribute().put(dataMining.getHeader("START_WORKING"), newCaseEvent.getStart_time());
                    }
                    events.replace(oldCase.getEventEntityId(), oldCase);
                } else {
                    if (record.get(dataMining.getHeader(CaseEvent.CONTENT)) != null) {
                        newCaseEvent.getAdditionalAttribute().putAll(XmlParserTool.getPropertiesFromXML(record.get(dataMining.getHeader(CaseEvent.CONTENT)), config.getEncodedFormat(), dateFormat));
                    }
                    events.put(newCaseEvent.getEventEntityId(), newCaseEvent);

                }
            }
        }
        logger.info("The process is done with " + events.size() + " events");
        logger.info("The header is composed " + newCaseEvent.getHeaders() + "");
    }

    public void augmentCaseDetails(List<String> properties, String format, CaseTypeService caseTypeService, boolean displayName) {
        Set<String> casesID = new HashSet<>();
        HashMap<String, String> emptyProp = new HashMap<>();
        for (String prop : properties) {
            emptyProp.put(prop, "");
        }
        for (String key : getEvents().keySet()) {
            CaseEvent caseEvent = getEvents().get(key);
            String caseID = caseEvent.getAdditionalAttribute().get(CaseEvent.CASE_ID);
            if (casesID.add(caseID)) {
                logger.info("Get details for the case instance : " + caseID);
                Case caseInstance = caseTypeService.getCase(caseID, properties);
                HashMap<String, String> props = caseTypeService.getProperties(caseInstance, format, displayName);
                caseEvent.getAdditionalAttribute().putAll(props);
                getEvents().replace(key, caseEvent);
            } else {
                caseEvent.getAdditionalAttribute().putAll(emptyProp);
                getEvents().replace(key, caseEvent);
            }
        }
    }

    public String[] getHeaders() {
        String[] arr = new String[0];
        Set<String> set = events.keySet();
        if (set.size() == 0) logger.warning("No Header founds");
        else {
            CaseEvent event = events.get(set.iterator().next());
            List<String> headers = event.getHeaders();
            arr = new String[headers.size()];
            headers.toArray(arr);
        }
        return arr;
    }

    public List<Attribute> getCaseAttribute() {
        List<Attribute> attributes = new ArrayList<>();
        caseTypes.forEach((caseType, count) -> {
            attributes.add(new Attribute(caseType, caseType));
        });
        return attributes;
    }
}

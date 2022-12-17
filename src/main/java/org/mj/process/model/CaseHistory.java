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
    int eventCount = 0;
    int levelCount = 0;
    HashMap<String, Integer> eventDetails;

    private HashMap<String, CaseEvent> events;
    private HashMap<String, Integer> caseTypes;

    public CaseHistory() {

    }

    public void analyse(DataMining dataMining, DocumentRequest config) {
        eventCount = 0;
        levelCount = 0;
        eventDetails = new HashMap<>();
        CaseEvent newCaseEvent = null;
        caseTypes = new HashMap<>();
        for (CSVRecord record : dataMining.getRecords()) {
            newCaseEvent = new CaseEvent(record, dataMining.getDefaultAttributes(), config.getDateFormat(), config.getTargetDateFormat(), config.cleanIDAttribute);
            if (eventDetails.containsKey(newCaseEvent.getEventType())) {
                eventDetails.replace(newCaseEvent.getEventType(), eventDetails.get(newCaseEvent.getEventType()) + 1);
            } else {
                eventDetails.put(newCaseEvent.getEventType(), 1);
            }
            if (newCaseEvent.getEventType().replace("\"", "").equals(config.getEventLevel())) {
                if (caseTypes.containsKey(newCaseEvent.getCaseType())) {
                    caseTypes.replace(newCaseEvent.getCaseType(), (caseTypes.get(newCaseEvent.getCaseType())) + 1);
                } else {
                    logger.info("New case type found " + newCaseEvent.getCaseType());
                    caseTypes.put(newCaseEvent.getCaseType(), 1);
                }
            }
            eventCount++;
        }
        logger.info("sample case event " + newCaseEvent.getAdditionalAttribute());
        logger.info("sample case event " + newCaseEvent.getCaseType());
        logger.info("The number of events is  " + eventCount);
        logger.info("The number of events per level " + eventDetails);
        logger.info("The number of events per case type is  " + caseTypes);
    }

    public void processData(DataMining dataMining, DocumentRequest config) {
        String contentHeader = null;
        for (String header : dataMining.getHeaders()) {
            if (header.contains(dataMining.getHeader(CaseEvent.CONTENT))) {
                logger.info("Header content info " + header);
                contentHeader = header;
                break;
            }
        }
        logger.info("Review Header -- 2");
        logger.info("List of defaultAttributes : " + dataMining.getDefaultAttributes());
        String dateFormat = config.getDateFormat();
        if (config.getTargetDateFormat() != null && !config.getTargetDateFormat().isEmpty())
            dateFormat = config.getTargetDateFormat();
        events = new HashMap<>();
        CaseEvent newCaseEvent = null;
        for (CSVRecord record : dataMining.getRecords()) {
            newCaseEvent = new CaseEvent(record, dataMining.getDefaultAttributes(), config.getDateFormat(), config.getTargetDateFormat(), config.cleanIDAttribute);
            if (newCaseEvent.getCaseType().equals(config.getCaseType()) && newCaseEvent.getEventType().equals(config.getEventLevel())) {
                if (events.containsKey(newCaseEvent.getEventEntityId())) {
                    CaseEvent oldCase = events.get(newCaseEvent.getEventEntityId());
                    if (newCaseEvent.getStatus().equals("INITIATED"))
                        oldCase.setStart_time(newCaseEvent.getStart_time());
                    if (newCaseEvent.getStatus().equals("COMPLETION")) {
                        oldCase.setStatus("COMPLETION");
                        oldCase.setEnd_time(newCaseEvent.getEnd_time());
                    }
                    if (contentHeader != null && record.get(contentHeader) != null) {
                        oldCase.getAdditionalAttribute().putAll(XmlParserTool.getPropertiesFromXML(record.get(contentHeader), config.getEncodedFormat(), dateFormat));
                    }
                    if (newCaseEvent.getStatus().equals("START_WORKING")) {
                        oldCase.getAdditionalAttribute().put(dataMining.getHeader("START_WORKING"), newCaseEvent.getStart_time());
                    }
                    events.replace(oldCase.getEventEntityId(), oldCase);
                } else {

                    if (contentHeader != null && record.get(contentHeader) != null) {
                        newCaseEvent.getAdditionalAttribute().putAll(XmlParserTool.getPropertiesFromXML(record.get(contentHeader), config.getEncodedFormat(), dateFormat));
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

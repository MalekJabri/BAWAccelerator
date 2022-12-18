package org.mj.process.tools;

import lombok.Data;
import org.mj.process.model.generic.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.*;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

@Data
public class XmlParserTool {
    private static final Logger logger = LoggerFactory.getLogger(XmlParserTool.class);
    private String format;

    public static void main(String[] args) throws IOException, XMLStreamException {
        //  Path fileName = Path.of("casedetails.xml");
        //  String text = Files.readString(fileName);
        // logger.info("Encoded " + Base64Tools.encode(text));
        //Map<String, String> result = XmlParserTool.getPropertiesFromXML(Base64Tools.encode(text), true, "dd-MM-yyyy hh:mm:ss");
       /* result.forEach((key, value) -> {
            logger.info("This value  " + key + " --> " + value);
        });*/
    }

    public static Set<String> getAttributes() {
        Set<String> attributes = new HashSet<>();
        attributes.add("name");
        attributes.add("StepName");
        attributes.add("ISName");
        attributes.add("QueueName");
        attributes.add("startTime");
        attributes.add("endTime");
        attributes.add("Response");
        return attributes;
    }

    public static Map<String, String> getPropertiesFromXML(String text, String encoded, String dateFormat) throws Exception {
        Map<String, String> propertiesList = new HashMap<>();
        for (String attName : getAttributes()) {
            propertiesList.put(attName, "");
        }

        String xmlContent;
        if (encoded.equals("BASE64")) {
            xmlContent = Base64Tools.decode(text);
        } else if (encoded.equals("HEX")) {
            xmlContent = HexTools.decode(text);
        } else {
            xmlContent = text;
        }
        SimpleDateFormat formatDate = new SimpleDateFormat(dateFormat);
        Reader reader = new StringReader(xmlContent);
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader xr = xif.createXMLStreamReader(reader);
        while (xr.hasNext()) {
            xr.next();
            if (xr.getEventType() == START_ELEMENT && !xr.getLocalName().equals("Step")) {
                if (getAttributes().contains(xr.getLocalName())) {
                    Attribute attribute;
                    if (xr.getLocalName().contains("Time")) {
                        Date eventDate = new Date(Long.parseLong(xr.getElementText().toString()));
                        String info = formatDate.format(eventDate);
                        if (info.contains("9999"))
                            propertiesList.replace(xr.getLocalName().toString(), "");
                        else
                            propertiesList.replace(xr.getLocalName().toString(), formatDate.format(eventDate));
                    } else {
                        if (xr.getLocalName().toString().equals("name")) {
                            propertiesList.remove(xr.getLocalName().toString());
                            propertiesList.put("tName_" + xr.getLocalName().toString(), xr.getElementText().toString());
                        } else
                            propertiesList.replace(xr.getLocalName().toString(), xr.getElementText().toString());
                    }

                }
            }
        }

        return propertiesList;
    }
}

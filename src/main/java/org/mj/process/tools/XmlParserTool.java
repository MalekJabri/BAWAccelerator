package org.mj.process.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

public class XmlParserTool {
    private static final Logger logger = LoggerFactory.getLogger(XmlParserTool.class);

    public static void main(String[] args) throws FileNotFoundException, XMLStreamException {

        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader xr = xif.createXMLStreamReader(new FileInputStream("casedetails.xml"));
        while (xr.hasNext()) {
            xr.next();
            if (xr.getEventType() == START_ELEMENT && !xr.getLocalName().equals("Step")) {
                System.out.println(xr.getLocalName());
                System.out.println(xr.getElementText());
            }
        }


    }
}

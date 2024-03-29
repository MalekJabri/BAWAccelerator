package org.mj.process.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.mj.process.model.CaseEvent;
import org.mj.process.model.CaseHistory;
import org.mj.process.model.DataMining;
import org.mj.process.model.DocumentRequest;
import org.mj.process.tools.JSONTool;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class DataProcessingService {

    private static Logger logger = Logger.getLogger(DataProcessingService.class.getName());

    public static void main(String[] args) throws Exception {
        List<String> properties = new ArrayList<>();
        properties.add("CmAcmCaseIdentifier");
        properties.add("PQ_Type");
        properties.add("PQ_Nationality");
        properties.add("PQ_Departement");
        properties.add("PQ_Sender");
        properties.add("PQ_CountWaitResponse");
        ServerConfig serverConfig = new ServerConfig("TOS", 3);
        CaseTypeService caseTypeService = new CaseTypeService(serverConfig);
        DataProcessingService dataProcessingService = new DataProcessingService();
        HashMap<String, CaseEvent> maps = new HashMap<>();
        String path = "/Users/jabrimalek/Project/lalux/BAW_DATA/CH_CASEHIST.csv";
        String pathOut = "/Users/jabrimalek/Project/lalux/BAW_DATA/CH_CASEHIST_augmented.csv";
        String delimiter = ",";
        DataMining dataMining = dataProcessingService.GetContentProcess(path, delimiter);
        String levelEvent = "Step";
        CaseHistory caseHistory = new CaseHistory();
        DocumentRequest documentRequest = new DocumentRequest();
        documentRequest.setEventLevel(levelEvent);
        documentRequest.setEncodedFormat("BASE64");
        documentRequest.setDateFormat("yyyy-MM-dd-hh.mm.ss");
        documentRequest.setTargetDateFormat("dd-MM-yyyy hh:mm:ss");
        documentRequest.setCleanDate(true);
        documentRequest.setCleanIDAttribute(true);
        documentRequest.setCaseType("{E0BAC726-36D9-4BAB-A0AF-35478A5E9F93}");
        caseHistory.analyse(dataMining, documentRequest);
        caseHistory.getCaseTypes().forEach((type, count) -> {
            logger.info("The case " + type + " with " + count + " events");
        });
        dataMining = dataProcessingService.GetContentProcess(path, delimiter);
        caseHistory.processData(dataMining, documentRequest);
        String dateFormat = documentRequest.getDateFormat();
        if (documentRequest.getTargetDateFormat() != null && !documentRequest.getTargetDateFormat().isEmpty())
            dateFormat = documentRequest.getTargetDateFormat();
        caseHistory.augmentCaseDetails(properties, dateFormat, caseTypeService, false);
        logger.info(JSONTool.getMappingInfo(caseHistory.getHeaders(), true, documentRequest.getDateFormat()));
        dataProcessingService.save(caseHistory, pathOut);
    }

    public DataMining GetContentProcess(String path, String delimiter) throws Exception {
        boolean quote = false;
        boolean lowerCase = false;
        BufferedReader brTest = new BufferedReader(new FileReader(path));
        String fistLine = brTest.readLine();
        logger.finest(fistLine);
        if (!fistLine.contains(delimiter)) {
            throw new Exception(" Delimiter " + delimiter + " is not correct");
        }
        String secondLine = brTest.readLine();
        brTest.close();
        String[] strArray = fistLine.split(delimiter + "");
        int pos = 0;
        if (strArray[0].startsWith("\"")) {
            quote = true;
            pos = 1;
        }
        boolean startG = fistLine.startsWith("\"");
        lowerCase = Character.isLowerCase(strArray[0].charAt(pos));
        DataMining dataMining;
        Reader in = new FileReader(path);
        if (secondLine.startsWith("\"")) {
            logger.info("Extract using quote -->" + startG);
            dataMining = new DataMining(lowerCase, false, startG);
            logger.info("strArray : " + Arrays.toString(strArray));
            logger.info("Headers " + dataMining.getDefaultAttributes());
            dataMining.setRecords(CSVFormat.EXCEL.withDelimiter(delimiter.charAt(0)).withSkipHeaderRecord().withHeader(strArray).withQuote('"').parse(in));
        } else {
            logger.info("Extract without quote -->" + startG);
            dataMining = new DataMining(lowerCase, false, startG);
            logger.info("strArray : " + Arrays.toString(strArray));
            logger.info("Headers " + dataMining.getDefaultAttributes());
            dataMining.setRecords(CSVFormat.EXCEL.withDelimiter(delimiter.charAt(0)).withSkipHeaderRecord().withHeader(strArray).parse(in));
        }
        dataMining.setHeaders(strArray);
        return dataMining;
    }

    public void save(CaseHistory caseHistory, String path) throws IOException {
        logger.info("Start Writing the case event to the path " + path);
        AtomicInteger count = new AtomicInteger();
        FileWriter out = new FileWriter(path);
        logger.info("Number of evens :" + caseHistory.getEvents().size());
        List<String> result = caseHistory.getEvents().get(caseHistory.getEvents().keySet().iterator().next()).getHeaders();
        String[] myheaders = result.toArray(new String[0]);
        try {
            CSVPrinter printer = new CSVPrinter(out, CSVFormat.EXCEL.withDelimiter(';').withHeader(myheaders));
            caseHistory.getEvents().forEach((id, caseEvent) -> {
                try {
                    if (myheaders.length != caseEvent.getValues().size()) {
                        logger.info("Difference header ");
                        logger.info(Arrays.toString(myheaders));
                        logger.info("" + caseEvent.getHeaders());
                    }
                    printer.printRecord(caseEvent.getValues());
                    count.getAndIncrement();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            printer.flush();
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
        logger.info("The file " + path + " has been saved and contains " + count + " records");

    }

}

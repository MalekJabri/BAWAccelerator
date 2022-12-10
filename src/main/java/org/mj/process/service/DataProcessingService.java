package org.mj.process.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.mj.process.model.CaseEvent;
import org.mj.process.model.CaseHistory;
import org.mj.process.model.DataMining;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class DataProcessingService {

    private static Logger logger = Logger.getLogger(DataProcessingService.class.getName());

    public static void main(String[] args) throws Exception {
        DataProcessingService dataProcessingService = new DataProcessingService();
        HashMap<String, CaseEvent> maps = new HashMap<>();
        String path = "/Users/jabrimalek/Project/lalux/history.csv";
        String pathOut = "/Users/jabrimalek/Project/lalux/compiled.csv";
        String delimiter = ";";
        DataMining dataMining = dataProcessingService.GetContentProcess(path, delimiter);
        String levelEvent = "Step";
        CaseHistory caseHistory = new CaseHistory(dataMining.getHeaders());
        caseHistory.init(dataMining, levelEvent);
        // caseHistory.cleanCompletion();
        dataProcessingService.save(caseHistory, pathOut);
    }

    public DataMining GetContentProcess(String path, String delimiter) throws Exception {
        DataMining dataMining = new DataMining();
        BufferedReader brTest = new BufferedReader(new FileReader(path));
        String text = brTest.readLine();
        brTest.close();
        String[] strArray = text.split(delimiter + "");
        Reader in = new FileReader(path);
        dataMining.setRecords(CSVFormat.EXCEL.withDelimiter(delimiter.charAt(0)).withSkipHeaderRecord().withHeader(strArray).parse(in));
        dataMining.setHeaders(strArray);
        return dataMining;
    }

    public void save(CaseHistory caseHistory, String path) throws IOException {
        logger.info("Start Writing the case event to the path " + path);
        AtomicInteger count = new AtomicInteger();
        FileWriter out = new FileWriter(path);
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.EXCEL.withDelimiter(';')
                .withHeader(caseHistory.getHeaders()))) {
            caseHistory.getEvents().forEach((id, caseEvent) -> {
                try {
                    printer.printRecord(caseEvent.getValues());
                    count.getAndIncrement();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            printer.flush();
        }
        logger.info("The file " + path + " has been saved and contains " + count + " records");

    }

}
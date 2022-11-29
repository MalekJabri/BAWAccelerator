package org.mj.process.model;

import lombok.Data;
import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class DataMining {
    private String[] headers;
    private Iterable<CSVRecord> records;

}

package org.mj.process.model;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Data
@ToString
public class CaseHistory {

    private String[] head;
    private HashMap<String , CaseEvent> events;
    public void init(DataMining dataMining, String level){
        events = new HashMap<>();
        boolean lowerCase = Character.isLowerCase(head[0].charAt(0));
        for (CSVRecord record : dataMining.getRecords()) {
            CaseEvent newCaseEvent = new CaseEvent(record, lowerCase);
            if(newCaseEvent.getEventType().equals(level)) {
                if(events.containsKey(newCaseEvent.getReferenceID())){
                    CaseEvent oldCase = events.get(newCaseEvent.getReferenceID());
                    if(newCaseEvent.getStatus().equals( "INITIATED")) oldCase.setStart_time(newCaseEvent.getStart_time());
                    if(newCaseEvent.getStatus().equals( "COMPLETION"))  {
                        oldCase.setStatus("COMPLETION");
                        oldCase.setEnd_time(newCaseEvent.getEnd_time());
                    }
                    events.replace(oldCase.getReferenceID(), oldCase);
                }
                else {
                    events.put(newCaseEvent.getReferenceID(), newCaseEvent);
                }
            }
        }

    }


  public void cleanCompletion(){
        List<String> idtoClean = new ArrayList<>();
      events.forEach((id, events)->{
          if(!events.getStatus().equals("COMPLETION")) idtoClean.add(id);
      });
      for(String id: idtoClean){
          events.remove(id);
      }

  }
 public CaseHistory(String[] headers){
         head= headers;
    }


    public String[] getHeaders() {
        List<String> headers = new ArrayList<>();
        Set<String> set = events.keySet();
        CaseEvent event =events.get(set.iterator().next());
        headers =  event.getHeaders();
        String[]arr = new String [headers.size()];
        //Converting List to Array
        headers.toArray(arr);

      return arr;
    }
}

package org.mj.process.pageController.config.baw;

import com.ibm.mj.ApiClient;
import org.mj.process.model.*;
import org.mj.process.restController.CaseController;
import org.mj.process.service.CaseTypeService;
import org.mj.process.service.DataProcessingService;
import org.mj.process.service.ProcessMiningService;
import org.mj.process.service.ServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Controller
public class CaseServerPageController {

    private static Logger logger = Logger.getLogger(CaseServerPageController.class.getName());
    @Value("${process-mining.default-value}")
    private boolean initValue;

    public static List<Attribute> getEventsType() {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("Step", "Step"));
        attributes.add(new Attribute("Step Event", "StepEvent"));
        attributes.add(new Attribute("Task", "Task"));
        attributes.add(new Attribute("Task Event", "TaskEvent"));
        return attributes;
    }

    @PostMapping("/testConnection")
    public String testConnection(HttpSession session, HttpServletResponse response, Model model, ConnectionRequest connectionRequest) {
        DocumentRequest documentRequest = (DocumentRequest) session.getAttribute("documentRequest");
        ServerConfig serverConfig;
        try {
            serverConfig = new ServerConfig(connectionRequest.getFileNetServerRequest());
            logger.info("The object store is  " + serverConfig.getOs().get_Name());
            ProcessMiningService processMiningService = new ProcessMiningService();
            ApiClient apiClient = processMiningService.getDefaultClient(connectionRequest.getProcessMiningServerRequest());
            logger.info("Apiclient :" + apiClient);
        } catch (Exception e) {
            model.addAttribute("message", "Connection to server failed, please verify parameters");
            model.addAttribute("connectionRequest", connectionRequest);
            model.addAttribute("classAlert", "alert alert-warning");
            model.addAttribute("displayMessage", "");
            return "connectToServer";
        }
        try {
            DataProcessingService dataProcessingService = new DataProcessingService();
            DataMining dataMining = dataProcessingService.GetContentProcess(documentRequest.getFilePath(), documentRequest.getDelimiter());
            CaseHistory caseHistory = new CaseHistory(dataMining.getHeaders());
            caseHistory.init(dataMining, "TaskEvent");
            dataMining = null;
            Set<String> caseTypes = new HashSet<>();
            caseHistory.getEvents().forEach((id, event) -> {
                caseTypes.add(event.getCaseType());
            });
            logger.info("The number of case type found in the CSV is " + caseTypes.size());
            CaseTypeService caseTypeService = new CaseTypeService(serverConfig);
            //  model.addAttribute("CaseTypes", caseTypeService.getAttributesForCaseTypes(caseTypes));
            //   model.addAttribute("Solutions", caseTypeService.getAttributesForCaseSolution(caseTypes));
            model.addAttribute("Solutions", caseTypeService.getAttributesSolutions());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Connection to server failed, please verify parameters");
            model.addAttribute("connectionRequest", connectionRequest);
            model.addAttribute("classAlert", "alert alert-warning");
            model.addAttribute("displayMessage", "");
            return "uploadDocument";
        }
        session.setAttribute("connectionRequest", connectionRequest);
        model.addAttribute("message", "Successful connection to Filenet / Process mining");
        model.addAttribute("configurationRequest", new ConfigurationRequest());
        model.addAttribute("properties", getProperties());
        model.addAttribute("Levels", getEventsType());
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "");
        model.addAttribute("position", 3);
        model.addAttribute("displayProperty", "display:none");
        return "configuration1";
    }

    private List<Attribute> getProperties() {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("Empty", ""));
        return attributes;
    }

    @GetMapping("/getPropertiesForCase")
    public String GetPropertyList(HttpSession session, Model model, @RequestParam(name = "caseType", required = true) String caseTypeID, @RequestParam(name = "solution", required = true) String solution) {
        logger.info("Get properties caseType " + caseTypeID + " -- Solution " + solution);
        ConnectionRequest connectionRequest = (ConnectionRequest) session.getAttribute("connectionRequest");
        ServerConfig serverConfig = new ServerConfig(connectionRequest.getFileNetServerRequest());
        ConfigurationRequest configurationRequest = new ConfigurationRequest();
        configurationRequest.setCaseType(caseTypeID);
        configurationRequest.setSolution(solution);
        logger.info("Get properties and show it for the case type " + caseTypeID);
        CaseTypeService caseTypeService = new CaseTypeService(serverConfig);
        //   model.addAttribute("Solutions", caseTypeService.getAttributesForCaseSolution(caseTypes));
        model.addAttribute("Solutions", caseTypeService.getAttributesSolutions());
        model.addAttribute("CaseTypes", caseTypeService.getAttributesCaseType(solution));
        model.addAttribute("properties", CaseController.extracted(caseTypeID, connectionRequest));
        model.addAttribute("configurationRequest", configurationRequest);
        model.addAttribute("Levels", getEventsType());
        model.addAttribute("message", "Configuration review");
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "");
        model.addAttribute("displayProperty", "");
        return "configuration1";
    }
}

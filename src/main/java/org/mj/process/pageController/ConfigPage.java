package org.mj.process.pageController;

import org.mj.process.model.*;
import org.mj.process.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Controller
public class ConfigPage {
    private static Logger logger = Logger.getLogger(ServerECMTools.class.getName());
    @Value("${spring.servlet.multipart.location}")
    private String path;

    @GetMapping("/")
    public String startingPage(HttpServletRequest request, HttpServletResponse response, Model model) {
        //   model.addAttribute("serverECM", new ServerECM());
        model.addAttribute("csvImport", new ServerRequest());
        model.addAttribute("message", "");
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "display:none");
        model.addAttribute("position", 1);
        return "uploadDocument";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(HttpSession session, Model model, ServerRequest serverRequest, @RequestParam("document") MultipartFile document) {
        logger.info("Document will be uploaded");
        if (document != null) {

            serverRequest.setFilePath(ContentService.storeDocument("test", document, path, "csv"));
            session.setAttribute("ServerRequest", serverRequest);
        } else if (serverRequest.getFilePath() != null && !serverRequest.getFilePath().isEmpty()) {
            session.setAttribute("ServerRequest", serverRequest);
        } else {
            model.addAttribute("csvImport", new ServerRequest());
            model.addAttribute("message", "Issue with the file! please check the logs");
            model.addAttribute("classAlert", "alert alert-warning");
            model.addAttribute("displayMessage", "");
            return "uploadDocument";
        }

        try {
            DataProcessingService dataProcessingService = new DataProcessingService();
            DataMining dataMining = dataProcessingService.GetContentProcess(serverRequest.getFilePath(), serverRequest.getDelimiter());
            ServerECM serverECM = new ServerECM();
            serverECM.setServer("baw.malek-jabri-s-account.cloud");
            serverECM.setPassword("!Filenet1!");
            serverECM.setPort("9080");
            serverECM.setUser("dadmin");
            serverECM.setRepository("TOS");
            model.addAttribute("serverECM", serverECM);
            model.addAttribute("message", "Upload of the file has been done correctly");
            model.addAttribute("classAlert", "alert alert-success");
            model.addAttribute("displayMessage", "display:none");
            return "connectToServer";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("csvImport", new ServerRequest());
            model.addAttribute("message", "Issue with the file! please check the logs");
            model.addAttribute("classAlert", "alert alert-warning");
            model.addAttribute("displayMessage", "");
            return "uploadDocument";
        }
    }

    @PostMapping("/testConnection")
    public String testConnection(HttpSession session, HttpServletResponse response, Model model, ServerECM serverECM) {
        ServerRequest serverRequest = (ServerRequest) session.getAttribute("ServerRequest");
        ServerConfig serverConfig;
        try {
            serverConfig = new ServerConfig(serverECM);
            logger.info("The object store is  " + serverConfig.getOs().get_Name());
        } catch (Exception e) {
            model.addAttribute("message", "Connection to server failed, please verify parameters");
            model.addAttribute("serverECM", new ServerECM());
            model.addAttribute("classAlert", "alert alert-warning");
            model.addAttribute("displayMessage", "");
            return "connectToServer";
        }
        try {
            DataProcessingService dataProcessingService = new DataProcessingService();
            DataMining dataMining = dataProcessingService.GetContentProcess(serverRequest.getFilePath(), serverRequest.getDelimiter());
            CaseHistory caseHistory = new CaseHistory(dataMining.getHeaders());
            caseHistory.init(dataMining, "TaskEvent");
            dataMining = null;
            Set<String> caseTypes = new HashSet<>();
            caseHistory.getEvents().forEach((id, event) -> {
                caseTypes.add(event.getCaseType());
            });
            logger.info("The number of case type id are " + caseTypes.size() + caseTypes.iterator().next());
            CaseTypeService caseTypeService = new CaseTypeService(serverConfig);
            model.addAttribute("CaseTypes", caseTypeService.getCaseTypeAttribute(caseTypes));
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Connection to server failed, please verify parameters");
            model.addAttribute("serverECM", new ServerECM());
            model.addAttribute("classAlert", "alert alert-warning");
            model.addAttribute("displayMessage", "");
            return "uploadDocument";
        }
        model.addAttribute("message", "Connection to server successful / configuration");
        model.addAttribute("configurationRequest", new ConfigurationRequest());
        model.addAttribute("Levels", getEventsType());
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "");
        model.addAttribute("position", 3);
        return "configuration";
    }

    @PostMapping("/configuration")
    public String configuration(HttpSession session, Model model, ConfigurationRequest configurationRequest) {
        ServerRequest serverRequest = (ServerRequest) session.getAttribute("ServerRequest");
        try {
            DataProcessingService dataProcessingService = new DataProcessingService();
            DataMining dataMining = dataProcessingService.GetContentProcess(serverRequest.getFilePath(), serverRequest.getDelimiter());
            CaseHistory caseHistory = new CaseHistory(dataMining.getHeaders());
            dataMining = null;
            Set<String> caseTypes = new HashSet<>();
            caseHistory.getEvents().forEach((id, event) -> {
                caseTypes.add(event.getCaseType().replace("{", "").replace("}", ""));
            });
            logger.info("Number of case type id : " + caseTypes.size());
            dataProcessingService.save(caseHistory, path + "cleaned" + ".csv");
        } catch (Exception e) {

        }
        model.addAttribute("message", "Configuration review");
        model.addAttribute("configurationRequest", new ConfigurationRequest());
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "");
        return "final";
    }

    private List<Attribute> getEventsType() {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("Step", "StepEvent"));
        attributes.add(new Attribute("Task", "TaskEvent"));
        return attributes;
    }

    @PostMapping("/generateFile")
    public String generateFile(HttpServletRequest request, HttpServletResponse response, Model model, ServerECM serverECM) {
        System.out.println(serverECM);
        model.addAttribute("message", "Connection to server successful");
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "");
        model.addAttribute("position", 3);
        return "final";
    }
}

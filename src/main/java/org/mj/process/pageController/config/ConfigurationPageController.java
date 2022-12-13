package org.mj.process.pageController.config;

import com.ibm.casemgmt.api.CaseType;
import org.mj.process.model.*;
import org.mj.process.model.servers.ConnectionRequest;
import org.mj.process.pageController.config.baw.CaseServerPageController;
import org.mj.process.service.CaseTypeService;
import org.mj.process.service.DataProcessingService;
import org.mj.process.service.ServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class ConfigurationPageController {

    private static Logger logger = Logger.getLogger(CaseServerPageController.class.getName());
    @Value("${process-mining.default-value}")
    private boolean initValue;
    @Value("${spring.servlet.multipart.location}")
    private String path;

    /*
     * After the configuration, we will generate the file based on the parameters
     * Compose the document and generate the link.
     */
    @PostMapping(value = "/configuration", params = "action=generate")
    public String generateFile(HttpSession session, Model model, ConfigurationRequest configurationRequest) {
        logger.info("Generate file " + configurationRequest);
        preparationCSV(session, configurationRequest);
        model.addAttribute("message", "Configuration review");
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "");
        return "download";
    }

    @PostMapping(value = "/configuration", params = "action=publish")
    public String publish(HttpSession session, Model model, ConfigurationRequest configurationRequest) {
        logger.info("Publish " + configurationRequest);

        preparationCSV(session, configurationRequest);
        model.addAttribute("message", "Configuration review");
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "");
        return "redirect:/setProcessMiningServer";
    }

    private void preparationCSV(HttpSession session, ConfigurationRequest configurationRequest) {
        logger.warning("properties Selected " + configurationRequest.getProperties().length);
        logger.warning(Arrays.toString(configurationRequest.getProperties()));
        DocumentRequest documentRequest = (DocumentRequest) session.getAttribute("documentRequest");
        ConnectionRequest connectionRequest = (ConnectionRequest) session.getAttribute("connectionRequest");
        CaseTypeService caseTypeService = null;
        if (connectionRequest != null) {
            ServerConfig serverConfig = new ServerConfig(connectionRequest.getBAWContentServer());
            caseTypeService = new CaseTypeService(serverConfig);
            CaseType caseType = caseTypeService.getCaseTypeByName(configurationRequest.getCaseType());
            documentRequest.setCaseType(caseType.getId().toString());
        } else {
            documentRequest.setCaseType(configurationRequest.getCaseType());
        }
        logger.info("teh case type is the following : " + documentRequest.getCaseType());
        try {
            logger.info("Preparation CSV Started");
            DataProcessingService dataProcessingService = new DataProcessingService();
            DataMining dataMining = dataProcessingService.GetContentProcess(documentRequest.getFilePath(), documentRequest.getDelimiter());
            CaseHistory caseHistory = new CaseHistory(dataMining.getHeaders());
            caseHistory.processData(dataMining, documentRequest);
            logger.info("The case type " + configurationRequest.getCaseType() + " has " + caseHistory.getEvents().size() + " events");
            if (documentRequest.isAddInformation() && configurationRequest.getProperties() != null && configurationRequest.getProperties().length > 0) {
                logger.info("Add Additional information from BAW Server");
                List<String> properties = new ArrayList<>();
                for (String prop : configurationRequest.getProperties()) properties.add(prop);
                properties.add("CmAcmCaseIdentifier");
                caseHistory.augmentCaseDetails(properties, documentRequest.getDateFormat(), caseTypeService, false);
                logger.info("The number of event is " + caseHistory.getEvents().size());
            }
            dataProcessingService.save(caseHistory, path + "-compiled" + ".csv");
            session.setAttribute("finalDoc", path + "-compiled" + ".csv");
            dataMining = null;
            logger.info("Preparation CSV ended");
        } catch (Exception e) {
            logger.warning("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
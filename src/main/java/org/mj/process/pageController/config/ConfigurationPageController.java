package org.mj.process.pageController.config;

import com.ibm.casemgmt.api.CaseType;
import org.mj.process.model.CaseHistory;
import org.mj.process.model.ConfigurationRequest;
import org.mj.process.model.DataMining;
import org.mj.process.model.DocumentRequest;
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
import java.util.List;
import java.util.logging.Logger;

@Controller
public class ConfigurationPageController {

    private static Logger logger = Logger.getLogger(CaseServerPageController.class.getName());
    @Value("${spring.servlet.multipart.location}")
    private String path;

    /*
     * After the configuration, we will generate the file based on the parameters
     * Compose the document and generate the link.
     */
    @PostMapping(value = "/configuration", params = "action=generate")
    public String generateFile(HttpSession session, Model model, ConfigurationRequest configurationRequest) {
        logger.info("Generate file " + configurationRequest);
        try {
            preparationCSV(session, configurationRequest);
        } catch (Exception e) {
            model.addAttribute("message", "Generation partially failed due to " + e.getMessage());
            model.addAttribute("classAlert", "alert alert-warning");
            model.addAttribute("displayMessage", "");
        }
        model.addAttribute("message", "Generation of the document has been completed");
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "");
        return "download";
    }

    @PostMapping(value = "/configuration", params = "action=publish")
    public String publish(HttpSession session, Model model, ConfigurationRequest configurationRequest) {
        logger.info("Publish " + configurationRequest);

        try {
            preparationCSV(session, configurationRequest);
        } catch (Exception e) {
            model.addAttribute("message", "Generation partially failed due to " + e.getMessage());
            model.addAttribute("classAlert", "alert alert-warning");
            model.addAttribute("displayMessage", "");
        }
        model.addAttribute("message", "Generation of the document has been completed");
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "");
        return "redirect:/setProcessMiningServer";
    }

    private void preparationCSV(HttpSession session, ConfigurationRequest configurationRequest) throws Exception {
        DocumentRequest documentRequest = (DocumentRequest) session.getAttribute("documentRequest");
        ConnectionRequest connectionRequest = (ConnectionRequest) session.getAttribute("connectionRequest");
        CaseTypeService caseTypeService = null;
        if (documentRequest.isAddInformation() && connectionRequest != null) {
            ServerConfig serverConfig = new ServerConfig(connectionRequest.getBawContentServer());
            caseTypeService = new CaseTypeService(serverConfig);
            logger.info("Case type selected " + configurationRequest.getCaseType());
            CaseType caseType = caseTypeService.getCaseTypeByName(configurationRequest.getCaseType());
            documentRequest.setCaseType(caseType.getId().toString());
        } else {
            documentRequest.setCaseType(configurationRequest.getCaseType());
        }
        logger.info("The case type is the following :" + documentRequest.getCaseType());

        logger.info("Preparation CSV Started");
        DataProcessingService dataProcessingService = new DataProcessingService();
        DataMining dataMining = dataProcessingService.GetContentProcess(documentRequest.getFilePath(), documentRequest.getDelimiter());
        CaseHistory caseHistory = new CaseHistory();
        caseHistory.processData(dataMining, documentRequest);
        logger.info("The case type " + configurationRequest.getCaseType() + " has " + caseHistory.getEvents().size() + " events");
        if (documentRequest.isAddInformation() && configurationRequest.getProperties() != null && configurationRequest.getProperties().length > 0) {
            String dateFormat = documentRequest.getDateFormat();
            if (documentRequest.getTargetDateFormat() != null && !documentRequest.getTargetDateFormat().isEmpty())
                dateFormat = documentRequest.getTargetDateFormat();
            logger.info("Add Additional information from BAW Server");
            List<String> properties = new ArrayList<>();
            for (String prop : configurationRequest.getProperties()) properties.add(prop);
            properties.add("CmAcmCaseIdentifier");
            caseHistory.augmentCaseDetails(properties, dateFormat, caseTypeService, false);
            logger.info("The number of event is " + caseHistory.getEvents().size());
        }
        dataProcessingService.save(caseHistory, path + "-compiled" + ".csv");
        session.setAttribute("headerCSV", caseHistory.getHeaders());
        session.setAttribute("finalDoc", path + "-compiled" + ".csv");
        dataMining = null;
        logger.info("Preparation CSV ended");

    }


}

package org.mj.process.pageController.config;

import org.mj.process.model.CaseHistory;
import org.mj.process.model.ConfigurationRequest;
import org.mj.process.model.DataMining;
import org.mj.process.model.DocumentRequest;
import org.mj.process.pageController.config.baw.CaseServerPageController;
import org.mj.process.service.DataProcessingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Set;
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
    @PostMapping("/configuration")
    public String configuration(HttpSession session, Model model, ConfigurationRequest configurationRequest) {
        logger.info("Create information " + configurationRequest);
        DocumentRequest documentRequest = (DocumentRequest) session.getAttribute("documentRequest");
        try {
            DataProcessingService dataProcessingService = new DataProcessingService();
            DataMining dataMining = dataProcessingService.GetContentProcess(documentRequest.getFilePath(), documentRequest.getDelimiter());
            CaseHistory caseHistory = new CaseHistory(dataMining.getHeaders());
            dataMining = null;
            Set<String> caseTypes = new HashSet<>();
            caseHistory.getEvents().forEach((id, event) -> {
                caseTypes.add(event.getCaseType().replace("{", "").replace("}", ""));
            });
            logger.info("Number of case type id : " + caseTypes.size());
            dataProcessingService.save(caseHistory, path + "cleaned" + ".csv");
        } catch (Exception e) {
            logger.warning("Message: " + e.getMessage());
        }
        model.addAttribute("message", "Configuration review");
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "");

        return "final";
    }


}

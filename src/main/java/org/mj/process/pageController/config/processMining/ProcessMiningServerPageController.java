package org.mj.process.pageController.config.processMining;

import com.ibm.mj.ApiClient;
import org.mj.process.model.Attribute;
import org.mj.process.model.ConfigPMProject;
import org.mj.process.model.ConnectionRequest;
import org.mj.process.model.DocumentRequest;
import org.mj.process.service.ProcessMiningService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class ProcessMiningServerPageController {

    private static Logger logger = Logger.getLogger(ProcessMiningServerPageController.class.getName());
    @Value("${process-mining.default-value}")
    private boolean initValue;

    @GetMapping("/getPropertiesForCase")
    public String GetPropertyList(HttpSession session, Model model) {
        //   model.addAttribute("Solutions", caseTypeService.getAttributesForCaseSolution(caseTypes));
        model.addAttribute("connectionRequest", new ConnectionRequest());
        model.addAttribute("message", "Configuration review");
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "");
        model.addAttribute("displayProperty", "display:none");
        return "configuration1";
    }

    @PostMapping("/testConnectionPM")
    public String testConnectionProcessMining(HttpSession session, HttpServletResponse response, Model model, ConnectionRequest connectionRequest) {
        DocumentRequest documentRequest = (DocumentRequest) session.getAttribute("documentRequest");
        List<Attribute> organisations = new ArrayList<>();
        try {
            ProcessMiningService processMiningService = new ProcessMiningService();
            ApiClient apiClient = processMiningService.getDefaultClient(connectionRequest.getProcessMiningServerRequest());
            organisations = processMiningService.getOrg(apiClient);
            logger.info("Apiclient :" + apiClient);
        } catch (Exception e) {
            model.addAttribute("message", "Connection to the process mining server failed, please verify parameters");
            model.addAttribute("connectionRequest", connectionRequest);
            model.addAttribute("classAlert", "alert alert-warning");
            model.addAttribute("displayMessage", "");
            e.printStackTrace();
            return "connectToProcessMining";
        }
        session.setAttribute("connectionRequest", connectionRequest);
        model.addAttribute("message", "Successful connection to Filenet / Process mining");
        model.addAttribute("configPMProject", new ConfigPMProject());
        model.addAttribute("organisations", organisations);
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "");
        model.addAttribute("position", 3);
        return "configProcessProject";
    }

    @PostMapping("/publishProject")
    public String publishProject(HttpSession session, HttpServletResponse response, Model model, ConfigPMProject configPMProject) {
        DocumentRequest documentRequest = (DocumentRequest) session.getAttribute("documentRequest");
        ConnectionRequest connectionRequest = (ConnectionRequest) session.getAttribute("connectionRequest");
        List<Attribute> organisations = new ArrayList<>();
        try {
            ProcessMiningService processMiningService = new ProcessMiningService();
            ApiClient apiClient = processMiningService.getDefaultClient(connectionRequest.getProcessMiningServerRequest());
            boolean projectSuccess = processMiningService.createNewProject(configPMProject.getProjectName(), configPMProject.getOrgID(), apiClient);
            logger.info("Creation for the project :" + configPMProject.getProjectName() + "-->" + projectSuccess);
        } catch (Exception e) {
            model.addAttribute("message", "Connection to the process mining server failed, please verify parameters");
            model.addAttribute("connectionRequest", connectionRequest);
            model.addAttribute("classAlert", "alert alert-warning");
            model.addAttribute("displayMessage", "");
            e.printStackTrace();
            return "connectToProcessMining";
        }
        model.addAttribute("message", "Creation of the project " + configPMProject.getProjectName());
        model.addAttribute("configPMProject", configPMProject);
        model.addAttribute("organisations", organisations);
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "");
        model.addAttribute("position", 3);
        return "configProcessProject";
    }

}

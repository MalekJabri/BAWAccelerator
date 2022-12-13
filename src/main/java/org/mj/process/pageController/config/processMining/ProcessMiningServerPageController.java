package org.mj.process.pageController.config.processMining;

import com.ibm.mj.ApiClient;
import com.ibm.mj.processmining.model.ProcessInfo;
import org.mj.process.model.Attribute;
import org.mj.process.model.ConfigPMProject;
import org.mj.process.model.ConnectionRequest;
import org.mj.process.model.DocumentRequest;
import org.mj.process.service.LocalPropertiesTools;
import org.mj.process.service.ProcessMiningService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private LocalPropertiesTools localPropertiesTools;

    @GetMapping("/setProcessMiningServer")
    public String GetPropertyList(HttpSession session, Model model) {
        ConnectionRequest connectionRequest = new ConnectionRequest();
        if (initValue) connectionRequest = localPropertiesTools.getConnectionRequest();
        model.addAttribute("connectionRequest", connectionRequest);
        model.addAttribute("message", "Configuration review");
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "");
        model.addAttribute("displayProperty", "display:none");
        return "processMining/connectToProcessMining";
    }

    @PostMapping("/testConnectionPM")
    public String testConnectionProcessMining(HttpSession session, HttpServletResponse response, Model model, ConnectionRequest connectionRequest) {
        List<Attribute> organisations;
        try {
            // if (initValue) connectionRequest = localPropertiesTools.getConnectionRequest();
            ProcessMiningService processMiningService = new ProcessMiningService();
            ApiClient apiClient = processMiningService.getDefaultClient(connectionRequest.getProcessMiningServerRequest());
            List<ProcessInfo> processInfos = processMiningService.listProjects(apiClient);
            organisations = processMiningService.getOrg(processInfos);
            session.setAttribute("projectsInfo", processInfos);
            logger.info("Apiclient :" + apiClient);
        } catch (Exception e) {
            model.addAttribute("message", "Connection to the process mining server failed, please verify parameters");
            model.addAttribute("connectionRequest", connectionRequest);
            model.addAttribute("classAlert", "alert alert-warning");
            model.addAttribute("displayMessage", "");
            e.printStackTrace();
            return "processMining/connectToProcessMining";
        }
        session.setAttribute("connectionRequest", connectionRequest);
        model.addAttribute("message", "Successful connection toProcess mining");
        model.addAttribute("configPMProject", new ConfigPMProject());
        model.addAttribute("organisations", organisations);
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "");
        model.addAttribute("position", 3);
        return "processMining/configProcessProject";
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
            ProcessInfo processInfo = processMiningService.getProject(apiClient, configPMProject.getProjectName());
            if (configPMProject.getUploadData() && processInfo != null) {
                String finalDoc = (String) session.getAttribute("finalDoc");
                processMiningService.uploadData(apiClient, processInfo.getProjectName(), configPMProject.getOrgID(), finalDoc, configPMProject.getAppend());
            }
            logger.info("Creation for the project :" + configPMProject.getProjectName() + "-->" + projectSuccess);
        } catch (Exception e) {
            model.addAttribute("message", "Connection to the process mining server failed, please verify parameters");
            model.addAttribute("connectionRequest", connectionRequest);
            model.addAttribute("classAlert", "alert alert-warning");
            model.addAttribute("displayMessage", "");
            e.printStackTrace();
            return "processMining/connectToProcessMining";
        }
        model.addAttribute("message", "Creation of the project " + configPMProject.getProjectName());
        model.addAttribute("configPMProject", configPMProject);
        model.addAttribute("organisations", organisations);
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "");
        model.addAttribute("position", 3);
        return "processMining/configProcessProject";
    }

}
